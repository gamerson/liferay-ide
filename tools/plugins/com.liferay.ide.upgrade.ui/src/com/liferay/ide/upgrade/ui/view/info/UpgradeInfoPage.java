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

import com.liferay.blade.api.Problem;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.ui.navigator.ProjectMigrationProblems;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.Page;

/**
 * @author Terry Jia
 */
public class UpgradeInfoPage extends Page implements ISelectionChangedListener {

	@Override
	public void createControl(Composite parent) {
		_composite = new Composite(parent, SWT.NULL);

		_composite.setLayout(new FillLayout());

		_browser = new Browser(_composite, SWT.BORDER);

		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void dispose() {
		super.dispose();

		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			commonViewer.removePostSelectionChangedListener(this);
		}
	}

	@Override
	public Control getControl() {
		return _composite;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();

		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;

			Object element = treeSelection.getFirstElement();

			if (element instanceof Problem) {
				Problem problem = (Problem)element;

				_browser.setText(problem.html);
			}
			else if (element instanceof FileProblems) {
				FileProblems fileProblems = (FileProblems)element;

				StringBuffer sb = new StringBuffer();

				List<Problem> problems = fileProblems.problems;

				sb.append(fileProblems.getFile());
				sb.append("<br />");
				sb.append("It has " + problems.size() + " issue(s) need to be solved.");
				sb.append("<br />");

				for (Problem problem : problems) {
					sb.append(problem.title);
					sb.append("<br />");
				}

				_browser.setText(sb.toString());
			}
			else if (element instanceof ProjectMigrationProblems) {
				ProjectMigrationProblems fileProblems = (ProjectMigrationProblems)element;

				StringBuffer sb = new StringBuffer();

				FileProblems[] problems = fileProblems.getFileProjects();

				sb.append(fileProblems.getProject());
				sb.append("<br />");
				sb.append("It has " + problems.length + " file(s) need to be solved.");
				sb.append("<br />");

				for (FileProblems problem : problems) {
					sb.append(problem.getFile());
					sb.append("<br />");
				}

				_browser.setText(sb.toString());
			}
			else {
				_browser.setUrl("about:blank");
			}
		}

		getControl().redraw();
	}

	@Override
	public void setFocus() {
		_composite.setFocus();
	}

	public void setMessage(String message) {
		if (_browser != null) {
			_browser.setText(message);
		}
	}

	protected UpgradeInfoPage() {
		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			commonViewer.addPostSelectionChangedListener(this);
		}
	}

	private Browser _browser;
	private Composite _composite;

}