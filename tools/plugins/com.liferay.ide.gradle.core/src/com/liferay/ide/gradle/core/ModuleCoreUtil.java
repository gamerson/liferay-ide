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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author Andy Wu
 */
public class ModuleCoreUtil {

	public static void addFacetsIfNeeded(final File projectLocation, IProgressMonitor monitor) throws CoreException {
		List<IProject> projects = new ArrayList<>();

		ProjectUtil.collectProjectsFromDirectory(projects, projectLocation);

		for (final IProject project : projects) {
			if (_hasJsp(project)) {
				_addFacets(project, monitor);
			}
		}
	}

	private static void _addConfigFile(final IProject project) throws CoreException {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("\n");
		sb.append("<project-modules id=\"moduleCoreId\" project-version=\"1.5.0\">");
		sb.append("\n");
		sb.append("<wb-module deploy-name=\"PROJECT_NAME\">");
		sb.append("\n");
		sb.append("<wb-resource deploy-path=\"/\" source-path=\"/src/main/resources/META-INF/resources\" ");
		sb.append("tag=\"defaultRootSource\"/>");
		sb.append("\n");
		sb.append("</wb-module>");
		sb.append("\n");
		sb.append("</project-modules>");
		sb.append("\n");

		String s = sb.toString();

		String finalContent = s.replace("PROJECT_NAME", project.getName());

		IPath location = project.getLocation();

		File compoment = new File(location.toFile(), ".settings/org.eclipse.wst.common.component");

		if (FileUtil.notExists(compoment)) {
			FileUtil.writeFile(compoment, finalContent.getBytes(), project.getName());
		}
	}

	private static void _addFacets(IProject project, IProgressMonitor monitor) throws CoreException {
		_addNature(project, "org.eclipse.wst.common.modulecore.ModuleCoreNature", monitor);
		_addNature(project, "org.eclipse.wst.common.project.facet.core.nature", monitor);

		_addConfigFile(project);

		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	private static void _addNature(IProject project, String natureId, IProgressMonitor monitor) throws CoreException {
		if ((monitor != null) && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (!_hasNature(project, natureId)) {
			IProjectDescription description = project.getDescription();

			String[] prevNatures = description.getNatureIds();

			String[] newNatures = new String[prevNatures.length + 1];

			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

			newNatures[prevNatures.length] = natureId;

			description.setNatureIds(newNatures);

			project.setDescription(description, monitor);
		}
		else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}

	private static boolean _hasJsp(IProject project) {
		List<String> list = new ArrayList<>();

		try {
			IPath path = project.getLocation();

			IPath projectPath = path.append(".project");

			File file = projectPath.toFile();

			File projectFile = file.getCanonicalFile();

			project.accept(
				new IResourceVisitor() {

					@Override
					public boolean visit(IResource resource) throws CoreException {
						try {
							IPath childProject = FileUtil.pathAppend(resource.getLocation(), ".project");

							File f = childProject.toFile();

							File childFile = f.getCanonicalFile();

							// don't check child project

							if (FileUtil.exists(childFile) && !projectFile.equals(childFile)) {
								return false;
							}

							IPath location = resource.getLocation();

							String path = location.toPortableString();

							String name = resource.getName();

							if (path.contains("resources/META-INF/resources") && name.endsWith(".jsp")) {
								list.add(path);
							}
						}
						catch (IOException ioe) {
						}

						return true;
					}

				});
		}
		catch (Exception e) {
		}

		return ListUtil.isNotEmpty(list);
	}

	private static boolean _hasNature(IProject project, String natureId) {
		try {
			if (!project.hasNature(natureId)) {
				return false;
			}
		}
		catch (CoreException ce) {
			return false;
		}

		return true;
	}

}