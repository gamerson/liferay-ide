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

import com.liferay.ide.upgrade.planner.core.UpgradePlan;
import com.liferay.ide.upgrade.planner.core.UpgradeTask;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * @author Terry Jia
 */
public class UpgradeTasksContentProvider implements IStructuredContentProvider {

	public static final Object NO_TASKS = new Object();

	public static final Object NO_UPGRADE_PLAN_ACTIVE = new Object();

	@Override
	public Object[] getElements(Object element) {
		if (NO_UPGRADE_PLAN_ACTIVE.equals(element)) {
			return new Object[] {NO_TASKS};
		}
		else if (element instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)element;

			List<UpgradeTask> upgradeTasks = upgradePlan.getTasks();

			return upgradeTasks.toArray(new UpgradeTask[0]);
		}
		else {
			return null;
		}
	}

}