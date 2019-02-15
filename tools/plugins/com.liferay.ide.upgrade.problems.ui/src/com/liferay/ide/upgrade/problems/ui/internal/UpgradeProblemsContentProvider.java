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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;
import com.liferay.ide.upgrade.problems.core.FileUpgradeProblem;
import com.liferay.ide.upgrade.problems.core.UpgradeProblemsCorePlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsContentProvider extends AbstractNavigatorContentProvider {

	public Object[] getChildren(Object element) {
		if (element instanceof MigrationProblemsContainer) {
			MigrationProblemsContainer migrationProblemsContainer = (MigrationProblemsContainer)element;

			return migrationProblemsContainer.getProjectProblemsConatiners();
		}
		else if (element instanceof ProjectProblemsContainer) {
			ProjectProblemsContainer projectProblemsContainer = (ProjectProblemsContainer)element;

			return projectProblemsContainer.getFileProblemsContainers();
		}
		else if (element instanceof FileProblemsContainer) {
			FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

			return fileProblemsContainer.getProblems();
		}

		return null;
	}

	public Object[] getElements(Object inputElement) {
		UpgradeProblemsCorePlugin upgradeProblemsCorePlugin = UpgradeProblemsCorePlugin.getDefault();

		IPath storageLocation = upgradeProblemsCorePlugin.getStateLocation();

		File storageFile = storageLocation.toFile();

		File[] files = storageFile.listFiles();

		return Stream.of(
			files
		).filter(
			file -> StringUtil.startsWith(file.getName(), "upgrade-")
		).map(
			file -> {
				try {
					return new FileReader(file);
				}
				catch (FileNotFoundException fnfe) {
					return null;
				}
			}
		).filter(
			Objects::nonNull
		).map(
			fileReader -> {
				try {
					return XMLMemento.createReadRoot(fileReader);
				}
				catch (WorkbenchException we) {
					return null;
				}
			}
		).filter(
			Objects::nonNull
		).map(
			root -> {
				MigrationProblemsContainer migrationProblemsContainer = new MigrationProblemsContainer();

				IMemento[] projectNodes = root.getChildren();

				ProjectProblemsContainer[] projectProblemsContainers =
					new ProjectProblemsContainer[projectNodes.length];

				for (int i = 0; i < projectProblemsContainers.length; i++) {
					IMemento projectNode = projectNodes[i];

					ProjectProblemsContainer projectProblemsContainer = new ProjectProblemsContainer();

					projectProblemsContainers[i] = projectProblemsContainer;

					String projectName = projectNode.getString("projectName");

					projectProblemsContainer.setProjectName(projectName);

					IMemento[] fileNodes = projectNode.getChildren("file");

					FileProblemsContainer[] fileProblemsContainers = new FileProblemsContainer[fileNodes.length];

					if (fileProblemsContainers.length > 0) {
						for (int t = 0; t < fileProblemsContainers.length; t++) {
							IMemento fileNode = fileNodes[t];

							FileProblemsContainer fileProblemsContainer = new FileProblemsContainer();

							fileProblemsContainers[t] = fileProblemsContainer;

							File file = new File(fileNode.getString("filePath"));

							fileProblemsContainer.setFile(file);

							IMemento[] problemNodes = fileNode.getChildren("problem");

							FileUpgradeProblem[] fileUpgradeProblems = new FileUpgradeProblem[problemNodes.length];

							if (fileUpgradeProblems.length > 0) {
								for (int m = 0; m < fileUpgradeProblems.length; m++) {
									long markerId = 0;

									try {
										markerId = Long.parseLong(problemNodes[m].getString("markerId"));
									}
									catch (NumberFormatException nfe) {
									}

									fileUpgradeProblems[m] = new FileUpgradeProblem(
										problemNodes[m].getString("title"), problemNodes[m].getString("summary"),
										problemNodes[m].getString("type"), problemNodes[m].getString("ticket"),
										problemNodes[m].getString("version"), file,
										problemNodes[m].getInteger("lineNumber"),
										problemNodes[m].getInteger("startOffset"),
										problemNodes[m].getInteger("endOffset"), problemNodes[m].getString("html"),
										problemNodes[m].getString("autoCorrectContext"),
										problemNodes[m].getInteger("status"), markerId,
										problemNodes[m].getInteger("markerType"));
								}
							}

							fileProblemsContainer.setProblems(fileUpgradeProblems);
						}
					}

					projectProblemsContainer.setFileProblemsContainers(fileProblemsContainers);
				}

				migrationProblemsContainer.setProjectProblemsConatiners(projectProblemsContainers);

				return migrationProblemsContainer;
			}
		).toArray();
	}

	public boolean hasChildren(Object element) {
		if (element instanceof MigrationProblemsContainer) {
			return true;
		}
		else if (element instanceof ProjectProblemsContainer) {
			return true;
		}
		else if (element instanceof FileProblemsContainer) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

}