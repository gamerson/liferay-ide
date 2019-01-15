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

package com.liferay.ide.upgrade.planner.ui.tasks;

import com.liferay.ide.upgrade.planner.core.UpgradeTask;
import com.liferay.ide.upgrade.planner.ui.UpgradePlannerUIPlugin;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (UpgradeTasksContentProvider.NO_TASKS.equals(element)) {
			return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.NO_TASKS_IMAGE);
		}
		else if (element instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)element;

			String categoryId = upgradeTask.getCategoryId();

			if ("database".equals(categoryId)) {
				return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.CATEGORY_DATABASE_IMAGE);
			}
			else if ("config".equals(categoryId)) {
				return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.CATEGORY_CONFIG_IMAGE);
			}
			else if ("code".equals(categoryId)) {
				return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.CATEGORY_CODE_IMAGE);
			}
		}

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (UpgradeTasksContentProvider.NO_TASKS.equals(element)) {
			return "No upgrade tasks.";
		}

		if (element instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)element;

			return upgradeTask.getTitle();
		}

		return null;
	}

}