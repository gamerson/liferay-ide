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

package com.liferay.ide.upgrade.ui.view.info;

import com.liferay.ide.upgrade.ui.view.plan.UpgradePlanView;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * @author Terry Jia
 */
public class UpgradeInfoView extends PageBookView implements ISelectionProvider, ISelectionChangedListener {

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionProvider().addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return getSelectionProvider().getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionProvider().removeSelectionChangedListener(listener);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		getSelectionProvider().selectionChanged(event);
	}

	@Override
	public void setSelection(ISelection selection) {
		getSelectionProvider().setSelection(selection);
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();

		initPage(page);

		page.createControl(book);
		page.setMessage("Liferay Upgrade Information");

		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (part instanceof ProjectExplorer) {
			UpgradeInfoPage page = new UpgradeInfoPage();

			initPage(page);

			//TODO need to improve the message

			page.setMessage("Information View for the ProjectExplorer");

			page.createControl(getPageBook());

			return new PageRec(part, page);
		}
		else if (part instanceof UpgradePlanView) {
			UpgradeInfoPage page = new UpgradeInfoPage();

			initPage(page);

			//TODO need to improve the message

			page.setMessage("Information View for the UpgradePlanView");

			page.createControl(getPageBook());

			return new PageRec(part, page);
		}

		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
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
		else if (part instanceof UpgradePlanView) {
			return true;
		}

		return false;
	}

}