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

package com.liferay.ide.upgrade.planner.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

import org.osgi.framework.Bundle;

import com.liferay.ide.upgrade.planner.ui.tasks.TasksViewer;
import com.liferay.ide.upgrade.planner.ui.tasks.TaskStepsViewer;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlannerView extends ViewPart {

	public static final String ID = "com.liferay.ide.upgrade.planner.view";
	private PageBook _pageBook;

	@Override
	public void createPartControl(Composite parentComposite) {
		_createActionBars();

		_createPartControl(parentComposite);
	}

	private void _createPartControl(Composite parentComposite) {
		parentComposite.setLayout(new FillLayout());

		_pageBook = new PageBook(parentComposite, SWT.NONE);

		_tasksViewer = new TasksViewer(parentComposite);

		_taskStepsViewer = new TaskStepsViewer(this, parentComposite);

		_tasksViewer.addSelectionChangedListener(_taskStepsViewer);
	}

	public void dispose() {
		super.dispose();

		if (_taskStepsViewer != null) {
			_taskStepsViewer.dispose();
		}
	}

	public TasksViewer getTasksViewer() {
		return _tasksViewer;
	}

	@Override
	public void setFocus() {
	}

	private void _createActionBars() {
		IViewSite viewSite = getViewSite();

		IActionBars actionBars = viewSite.getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();

		Bundle upgradeUI = UpgradePlannerUIPlugin.getDefaultBundle();

		ImageDescriptor openNewPlanImageDescriptor = ImageDescriptor.createFromURL(
			upgradeUI.getEntry("icons/liferay_logo_16.png"));

		IAction openNewUpgradePlan = new Action("Open new Liferay upgrade plan", openNewPlanImageDescriptor) {

			@Override
			public void run() {
				NewUpgradePlanDialog newUpgradePlanDialog = new NewUpgradePlanDialog(viewSite.getShell());

				newUpgradePlanDialog.open();
			}

		};

		toolBarManager.add(openNewUpgradePlan);
	}

	private TaskStepsViewer _taskStepsViewer;
	private TasksViewer _tasksViewer;

}