/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.task.problem.eclipse.provider;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.task.problem.api.FileMigrator;
import com.liferay.ide.upgrade.task.problem.api.Migration;
import com.liferay.ide.upgrade.task.problem.api.Problem;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@Component
public class ProjectMigrationService implements Migration {

	@Activate
	public void activate(BundleContext context) {
		_context = context;

		_fileMigratorTracker = new ServiceTracker<>(context, FileMigrator.class, null);

		_fileMigratorTracker.open();
	}

	@Override
	public List<Problem> findProblems(File projectDir, IProgressMonitor monitor) {
		return findProblems(projectDir, Collections.emptyList(), monitor);
	}

	@Override
	public List<Problem> findProblems(File projectDir, List<String> versions, IProgressMonitor monitor) {
		monitor.beginTask("Searching for migration problems in " + projectDir, -1);

		List<Problem> problems = Collections.synchronizedList(new ArrayList<Problem>());

		monitor.beginTask("Analyzing files", -1);

		_countTotal(projectDir);

		_walkFiles(projectDir, problems, versions, monitor);

		monitor.done();

		_count = 0;
		_total = 0;

		return problems;
	}

	@Override
	public List<Problem> findProblems(Set<File> files, IProgressMonitor monitor) {
		return findProblems(files, Collections.emptyList(), monitor);
	}

	@Override
	public List<Problem> findProblems(Set<File> files, List<String> versions, IProgressMonitor monitor) {
		List<Problem> problems = Collections.synchronizedList(new ArrayList<Problem>());

		monitor.beginTask("Analyzing files", -1);

		_total = files.size();

		for (File file : files) {
			_count++;

			if (monitor.isCanceled()) {
				return Collections.emptyList();
			}

			analyzeFile(file, problems, versions, monitor);
		}

		monitor.done();

		_count = 0;
		_total = 0;

		return problems;
	}

	protected FileVisitResult analyzeFile(
		File file, List<Problem> problems, List<String> versions, IProgressMonitor monitor) {

		Path path = file.toPath();

		Path pathFileName = path.getFileName();

		String fileName = pathFileName.toString();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		monitor.setTaskName("Analyzing file " + _count + "/" + _total + " " + fileName);

		ServiceReference<FileMigrator>[] fileMigratorsServiceReferences = _fileMigratorTracker.getServiceReferences();

		if (ListUtil.isNotEmpty(fileMigratorsServiceReferences)) {
			List<ServiceReference<FileMigrator>> fileMigrators = Stream.of(
				fileMigratorsServiceReferences
			).filter(
				serviceReference -> {
					String fileExtensions = (String)serviceReference.getProperty("file.extensions");

					List<String> extensions = Arrays.asList(fileExtensions.split(","));

					return extensions.contains(extension);
				}
			).filter(
				serviceReference -> {
					if (ListUtil.isNotEmpty(versions)) {
						String version = (String)serviceReference.getProperty("version");

						return versions.contains(version);
					}
					else {
						return true;
					}
				}
			).collect(
				Collectors.toList()
			);

			if (ListUtil.isNotEmpty(versions) && (versions.size() > 1)) {
				Collections.sort(
					versions,
					new Comparator<String>() {

						@Override
						public int compare(String version1, String version2) {
							Version osgiVersion1 = new Version(version1);
							Version osgiVersion2 = new Version(version2);

							return osgiVersion2.compareTo(osgiVersion1);
						}

					});

				String version = versions.get(0);

				Stream<ServiceReference<FileMigrator>> stream = fileMigrators.stream();

				List<ServiceReference<FileMigrator>> serviceReferencesWithVersion = stream.filter(
					serviceReference -> version.equals(serviceReference.getProperty("version"))
				).collect(
					Collectors.toList()
				);

				stream = fileMigrators.stream();

				List<ServiceReference<FileMigrator>> serviceReferencesWithoutVersion = stream.filter(
					predicate -> !version.equals(predicate.getProperty("version"))
				).collect(
					Collectors.toList()
				);

				for (ServiceReference<FileMigrator> serviceReferenceWithoutVersion : serviceReferencesWithoutVersion) {
					if (serviceReferencesWithVersion.contains(serviceReferenceWithoutVersion)) {
						fileMigrators.remove(serviceReferenceWithoutVersion);
					}
				}
			}

			if (ListUtil.isNotEmpty(fileMigrators)) {
				try {
					Stream<ServiceReference<FileMigrator>> migratorStream = fileMigrators.stream();

					migratorStream.map(
						_context::getService
					).parallel(
					).forEach(
						fm -> {
							List<Problem> fileProlbems = fm.analyze(file);

							if (ListUtil.isNotEmpty(fileProlbems)) {
								problems.addAll(fileProlbems);
							}
						}
					);
				}
				catch (Exception e) {
				}
			}

			_count++;
		}

		return FileVisitResult.CONTINUE;
	}

	private void _countTotal(File startDir) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith(".settings")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("bin") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("target") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/classes")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/service")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				File file = path.toFile();

				if (file.isFile() && attrs.isRegularFile() && (attrs.size() > 0)) {
					_total++;
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(startDir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void _walkFiles(File startDir, List<Problem> problems, List<String> versions, IProgressMonitor monitor) {
		SubMonitor progressMonitor = SubMonitor.convert(monitor, _total);

		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith(".settings")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("bin") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("target") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/classes")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/service")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (monitor.isCanceled()) {
					return FileVisitResult.TERMINATE;
				}

				File file = path.toFile();

				progressMonitor.split(1);

				if (file.isFile() && attrs.isRegularFile() && (attrs.size() > 0)) {
					FileVisitResult result = analyzeFile(file, problems, versions, monitor);

					if (result.equals(FileVisitResult.TERMINATE)) {
						return result;
					}
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(startDir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static int _count = 0;
	private static int _total = 0;

	private BundleContext _context;
	private ServiceTracker<FileMigrator, FileMigrator> _fileMigratorTracker;

}