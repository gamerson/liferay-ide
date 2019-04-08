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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.ui.util.Editors;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.ui.internal.migration.AutoCorrectAction;
import com.liferay.ide.upgrade.problems.ui.internal.migration.IgnoreAction;
import com.liferay.ide.upgrade.problems.ui.internal.migration.IgnoreAlwaysAction;
import com.liferay.ide.upgrade.problems.ui.internal.migration.MarkDoneAction;
import com.liferay.ide.upgrade.problems.ui.internal.migration.MarkUndoneAction;

import java.io.File;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeProblemsActionProvider extends CommonActionProvider {

	public UpgradeProblemsActionProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();

		ICommonActionExtensionSite commonActionExtensionSite = getActionSite();

		if (commonActionExtensionSite != null) {
			StructuredViewer structuredViewer = commonActionExtensionSite.getStructuredViewer();

			structuredViewer.removeDoubleClickListener(_doubleClickListener);
		}

		_doubleClickListener = null;
	}

	@Override
	public void fillContextMenu(IMenuManager menuManager) {
		menuManager.removeAll();

		ICommonViewerSite site = _actionSite.getViewSite();

		ISelectionProvider provider = site.getSelectionProvider();

		ISelection selection = provider.getSelection();

		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;

			Iterator<?> items = treeSelection.iterator();

			boolean selectionCompatible = true;

			while (items.hasNext()) {
				Object item = items.next();

				if (!(item instanceof UpgradeProblem)) {
					selectionCompatible = false;

					break;
				}
			}

			if (selectionCompatible) {
				MarkDoneAction markDoneAction = new MarkDoneAction(provider);

				MarkUndoneAction markUndoneAction = new MarkUndoneAction(provider);

				IgnoreAction ignoreAction = new IgnoreAction(provider);

				AutoCorrectAction autoCorrectAction = new AutoCorrectAction(provider);

				IgnoreAlwaysAction ignoreAlwaysAction = new IgnoreAlwaysAction(provider);

				menuManager.add(markDoneAction);
				menuManager.add(markUndoneAction);
				menuManager.add(ignoreAction);
				menuManager.add(autoCorrectAction);
				menuManager.add(ignoreAlwaysAction);
			}
		}
	}

	@Override
	public void init(ICommonActionExtensionSite commandActionExtensionSite) {
		_actionSite = commandActionExtensionSite;

		_doubleClickListener = new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();

				if (selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection)selection;

					Object element = treeSelection.getFirstElement();

					if (element instanceof UpgradeProblem) {
						UpgradeProblem upgradeProblem = (UpgradeProblem)element;

						int offset = upgradeProblem.getStartOffset();

						int length = upgradeProblem.getEndOffset() - offset;

						Editors.open(upgradeProblem.getResource(), upgradeProblem.getMarkerId(), offset, length);
					}
					else if (element instanceof FileProblemsContainer) {
						FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

						File file = fileProblemsContainer.getFile();

						Editors.open(file);
					}
				}
			}

		};

		StructuredViewer structuredViewer = commandActionExtensionSite.getStructuredViewer();

		structuredViewer.addDoubleClickListener(_doubleClickListener);
	}

	private ICommonActionExtensionSite _actionSite;
	private IDoubleClickListener _doubleClickListener;

}