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
import com.liferay.ide.upgrade.task.problem.api.MigrationProblemsContainer;
import com.liferay.ide.upgrade.task.problem.api.ProjectUpgradeProblems;
import com.liferay.ide.upgrade.task.problem.ui.util.UpgradeAssistantSettingsUtil;

import java.io.IOException;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsContentProvider extends AbstractNavigatorContentProvider {

	public Object[] getChildren(Object element) {
		if (element instanceof MigrationProblemsContainer) {
			MigrationProblemsContainer container = (MigrationProblemsContainer)element;

			return container.getProblemsArray();
		}
		else if (element instanceof ProjectUpgradeProblems) {
			ProjectUpgradeProblems projectMigrationProblems = (ProjectUpgradeProblems)element;

			return projectMigrationProblems.getFileProblems();
		}
		else if (element instanceof FileProblems) {
			FileProblems fileProblems = (FileProblems)element;

			return fileProblems.problems.toArray();
		}

		return null;
	}

	public Object[] getElements(Object inputElement) {
		try {
			MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
				MigrationProblemsContainer.class);

			if (container != null) {
				return new Object[] {container};
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof MigrationProblemsContainer) {
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