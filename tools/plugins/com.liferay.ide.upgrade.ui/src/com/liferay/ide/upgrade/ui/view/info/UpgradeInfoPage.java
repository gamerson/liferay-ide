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

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

		_label = new Label(_composite, SWT.LEFT | SWT.TOP | SWT.WRAP);

		_label.setText(_message);
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

		setMessage(selection.toString());

		//TODO show the information bases on the selection

		getControl().redraw();
	}

	@Override
	public void setFocus() {
		_composite.setFocus();
	}

	public void setMessage(String message) {
		_message = message;

		if (_label != null) {
			_label.setText(message);
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

	private Composite _composite;
	private Label _label;
	private String _message;

}