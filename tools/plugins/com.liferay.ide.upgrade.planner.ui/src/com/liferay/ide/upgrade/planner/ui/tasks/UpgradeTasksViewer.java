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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksViewer {

	public UpgradeTasksViewer(Composite parent) {
		_taskList = new ListViewer(parent);

		_taskList.setContentProvider(new UpgradeTasksContentProvider());
		_taskList.setLabelProvider(new UpgradeTaskLabelProvider());

		_taskList.setInput(new Object());
	}

	public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		_taskList.addSelectionChangedListener(selectionChangedListener);
	}

	public Object getInput() {
		return _taskList.getInput();
	}

	private ListViewer _taskList;

}