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

package com.liferay.ide.upgrade.ui.view.plan;

import com.liferay.ide.upgrade.ui.UpgradeUI;
import com.liferay.ide.upgrade.ui.dialog.NewUpgradePlanDialog;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 */
public class UpgradePlanView extends ViewPart {

	public UpgradePlanView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		_createActionBars();

		UpgradePlanPage page = new UpgradePlanPage("demo-workspace", new ArrayList<>());

		page.createPart(parent);
	}

	@Override
	public void setFocus() {
	}

	private void _createActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();

		Bundle upgradeUI = UpgradeUI.getDefaultBundle();

		ImageDescriptor openNewPlanImageDescriptor = ImageDescriptor.createFromURL(
			upgradeUI.getEntry("icons/liferay_logo_16.png"));

		IAction openNewUpgradePlan = new Action("Open new Liferay upgrade plan", openNewPlanImageDescriptor) {

			@Override
			public void run() {
				NewUpgradePlanDialog newUpgradePlanDialog = new NewUpgradePlanDialog(getViewSite().getShell());

				newUpgradePlanDialog.open();
			}

		};

		toolBarManager.add(openNewUpgradePlan);
	}

}