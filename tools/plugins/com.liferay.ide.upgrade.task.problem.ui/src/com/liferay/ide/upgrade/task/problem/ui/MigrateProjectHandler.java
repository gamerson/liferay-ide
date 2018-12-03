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

package com.liferay.ide.upgrade.task.problem.ui;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.upgrade.task.problem.api.FileProblems;
import com.liferay.ide.upgrade.task.problem.api.Migration;
import com.liferay.ide.upgrade.task.problem.api.MigrationConstants;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblems;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblemsContainer;
import com.liferay.ide.upgrade.task.problem.api.Problem;
import com.liferay.ide.upgrade.task.problem.api.UpgradeProblems;
import com.liferay.ide.upgrade.task.problem.ui.util.FileProblemsUtil;
import com.liferay.ide.upgrade.task.problem.ui.util.MigrationUtil;
import com.liferay.ide.upgrade.task.problem.ui.util.UpgradeAssistantSettingsUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 * @author Lovett Li
 * @author Terry Jia
 * @author Simon Jiang
 */
public class MigrateProjectHandler {

	public static void findMigrationProblems(final IPath[] locations, final String version) {
		findMigrationProblems(locations, new String[] {""}, version);
	}

	public static void findMigrationProblems(
		final IPath[] locations, final String[] projectName, final String version) {

		Job job = new WorkspaceJob("Finding migration problems...") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				IStatus retval = Status.OK_STATUS;

				Bundle bundle = FrameworkUtil.getBundle(getClass());

				BundleContext context = bundle.getBundleContext();

				try {
					ServiceReference<Migration> sr = context.getServiceReference(Migration.class);

					Migration m = context.getService(sr);

					List<Problem> allProblems = null;

					boolean singleFile = false;

					if ((locations.length == 1) && FileUtil.isFile(locations[0])) {
						singleFile = true;
					}

					MigrationProblemsContainer container = null;

					List<MigrationProblems> migrationProblemsList = new ArrayList<>();

					if (container == null) {
						container = new MigrationProblemsContainer();
					}

					if (container.getProblemsArray() != null) {
						List<MigrationProblems> mpList = Arrays.asList(container.getProblemsArray());

						for (MigrationProblems mp : mpList) {
							migrationProblemsList.add(mp);
						}
					}

					for (int i = 0; i < locations.length; i++) {
						allProblems = new ArrayList<>();

						File searchFile = locations[i].toFile();

						if (!monitor.isCanceled()) {
							List<Problem> problems = null;

							List<String> versions = new ArrayList<>();

							versions.add("7.0");

							if ("7.1".equals(version)) {
								versions.add("7.1");
							}

							if (singleFile) {
								MarkerUtil.clearMarkers(searchFile, MigrationConstants.MARKER_TYPE);

								Set<File> files = new HashSet<>();

								files.add(searchFile);

								problems = m.findProblems(files, versions, monitor);
							}
							else {
								problems = m.findProblems(searchFile, versions, monitor);
							}

							for (Problem problem : problems) {
								allProblems.add(problem);
							}
						}

						if (ListUtil.isNotEmpty(allProblems)) {
							MigrationUtil.addMarkers(allProblems);

							MigrationProblems migrationProblems = new MigrationProblems();

							FileProblems[] fileProblems = FileProblemsUtil.newFileProblemsListFrom(
								allProblems.toArray(new Problem[0]));

							migrationProblems.setProblems(fileProblems);

							migrationProblems.setType("Code Problems");
							migrationProblems.setSuffix(projectName[i]);

							int index = _isAlreadyExist(migrationProblemsList, projectName, i);

							if (index != -1) {
								if (singleFile) {
									UpgradeProblems up = migrationProblemsList.get(index);

									FileProblems[] problems = up.getProblems();

									for (int n = 0; n < problems.length; n++) {
										FileProblems fp = problems[n];

										String problemFilePath = FileUtil.getFilePath(fp.getFile());

										String initProblemFilePath = FileUtil.getFilePath(locations[0]);

										if (problemFilePath.equals(initProblemFilePath)) {
											problems[n] = fileProblems[0];

											break;
										}
									}

									migrationProblems.setProblems(problems);
								}

								migrationProblemsList.set(index, migrationProblems);
							}
							else {
								migrationProblemsList.add(migrationProblems);
							}
						}
						else {
							int index = _isAlreadyExist(migrationProblemsList, projectName, i);

							if (index != -1) {
								if (singleFile) {
									MigrationProblems mp = migrationProblemsList.get(index);

									FileProblems[] fps = mp.getProblems();

									List<FileProblems> fpList = Arrays.asList(fps);

									List<FileProblems> newFPList = new ArrayList<>();

									for (FileProblems fp : fpList) {
										File file = fp.getFile();

										String problemFilePath = file.getPath();

										String initProblemFilePath = FileUtil.getFilePath(locations[0]);

										if (!problemFilePath.equals(initProblemFilePath)) {
											newFPList.add(fp);
										}
									}

									mp.setProblems(newFPList.toArray(new FileProblems[0]));

									migrationProblemsList.set(index, mp);
								}
								else {
									migrationProblemsList.remove(index);
								}
							}
						}
					}

					if (ListUtil.isNotEmpty(migrationProblemsList)) {
						container.setProblemsArray(migrationProblemsList.toArray(new MigrationProblems[0]));

						UpgradeAssistantSettingsUtil.setObjectToStore(MigrationProblemsContainer.class, container);
					}
					else {
						UpgradeAssistantSettingsUtil.setObjectToStore(MigrationProblemsContainer.class, null);
					}

					allProblems.add(new Problem());

					m.reportProblems(allProblems, Migration.DETAIL_LONG, "ide");
				}
				catch (Exception e) {
				}

				return retval;
			}

		};

		job.schedule();
	}

	private static int _isAlreadyExist(
		List<MigrationProblems> migrationProblemsList, String[] projectName, int projectIndex) {

		int index = -1;

		for (int i = 0; i < migrationProblemsList.size(); i++) {
			UpgradeProblems upgradeProblems = migrationProblemsList.get(i);

			if (projectName[projectIndex].equals(((MigrationProblems)upgradeProblems).getSuffix())) {
				index = i;

				break;
			}
		}

		return index;
	}

}