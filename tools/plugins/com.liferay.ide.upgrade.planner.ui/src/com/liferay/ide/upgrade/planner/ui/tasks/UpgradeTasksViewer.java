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

import com.liferay.ide.upgrade.planner.core.UpgradeEvent;
import com.liferay.ide.upgrade.planner.core.UpgradeListener;
import com.liferay.ide.upgrade.planner.core.UpgradePlan;
import com.liferay.ide.upgrade.planner.core.UpgradePlanStartedEvent;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksViewer implements UpgradeListener {

	public UpgradeTasksViewer(Composite parentComposite) {
		_tableViewer = new TableViewer(parentComposite);

		_tableViewer.setContentProvider(new UpgradeTasksContentProvider());
		_tableViewer.setLabelProvider(new UpgradeTasksLabelProvider());

		_tableViewer.setInput(UpgradeTasksContentProvider.NO_UPGRADE_PLAN_ACTIVE);
	}

	public void addPostSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		_tableViewer.addPostSelectionChangedListener(selectionChangedListener);
	}

	public Object getInput() {
		return _tableViewer.getInput();
	}

	public ISelection getSelection() {
		if (_tableViewer != null) {
			return _tableViewer.getSelection();
		}

		return null;
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			_tableViewer.setInput(upgradePlan);
		}
	}

	private TableViewer _tableViewer;

}