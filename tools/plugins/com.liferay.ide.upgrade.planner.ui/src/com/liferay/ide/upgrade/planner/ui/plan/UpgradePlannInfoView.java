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

package com.liferay.ide.upgrade.planner.ui.plan;

import com.liferay.ide.upgrade.planner.ui.UpgradePlannerView;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlannInfoView extends PageBookView {

	public static final String ID = "com.liferay.ide.upgrade.planner.info.view";

	@Override
	protected IPage createDefaultPage(PageBook pageBook) {
		MessagePage page = new MessagePage();

		initPage(page);

		page.createControl(pageBook);
		page.setMessage("Liferay Upgrade Plan Information");

		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart workbenchPart) {
		UpgradePlannerInfoPage upgradeInfoPage = new UpgradePlannerInfoPage();

		initPage(upgradeInfoPage);

		if (workbenchPart instanceof ProjectExplorer) {
			upgradeInfoPage.setMessage("Information View for the ProjectExplorer");
		}
		else if (workbenchPart instanceof UpgradePlannerView) {
			upgradeInfoPage.setMessage("Information View for the current Upgrade Plan");
		}

		upgradeInfoPage.createControl(getPageBook());

		return new PageRec(workbenchPart, upgradeInfoPage);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart workbenchPart, PageRec pageRec) {
		IPage page = pageRec.page;

		page.dispose();

		pageRec.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();

		if (page != null) {
			return page.getActiveEditor();
		}

		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof ProjectExplorer) {
			return true;
		}
		else if (part instanceof UpgradePlannerView) {
			return true;
		}

		return false;
	}

}