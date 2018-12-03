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

package com.liferay.ide.upgrade.task.problem.ui.navigator;

import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;
import com.liferay.ide.upgrade.task.problem.api.FileProblems;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblems;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblemsContainer;
import com.liferay.ide.upgrade.task.problem.api.ProjectUpgradeProblems;
import com.liferay.ide.upgrade.task.problem.ui.util.UpgradeAssistantSettingsUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsContentProvider extends AbstractNavigatorContentProvider {

	public Object[] getChildren(Object element) {
		if (element instanceof IProject) {
			IProject project = (IProject)element;

			try {
				MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
					MigrationProblemsContainer.class);

				if (container == null) {
					return null;
				}

				for (MigrationProblems migrationProblems : container.getProblemsArray()) {
					String suffix = migrationProblems.getSuffix();

					if (suffix.equals(project.getName())) {
						Set<ProjectUpgradeProblems> set = Collections.singleton(
							new ProjectUpgradeProblems(project, migrationProblems.getProblems()));

						return set.toArray();
					}
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		else if (element instanceof ProjectUpgradeProblems) {
			ProjectUpgradeProblems projectMigrationProblems = (ProjectUpgradeProblems)element;

			return projectMigrationProblems.getFileProjects();
		}
		else if (element instanceof FileProblems) {
			FileProblems fileProblems = (FileProblems)element;

			return fileProblems.problems.toArray();
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			return true;
		}
		else if (element instanceof ProjectUpgradeProblems) {
			return true;
		}
		else if (element instanceof FileProblems) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

}