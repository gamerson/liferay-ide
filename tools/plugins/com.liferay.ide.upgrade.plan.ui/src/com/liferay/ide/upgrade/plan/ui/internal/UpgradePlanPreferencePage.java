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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class UpgradePlanPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public UpgradePlanPreferencePage() {
		super("Upgrade Plan");

		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanPreferencePage.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public Control createContents(Composite parent) {
		Composite pageComposite = new Composite(parent, SWT.NULL);

		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

		pageComposite.setLayout(gridLayout);

		GridData gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		pageComposite.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);

		Label label = new Label(pageComposite, SWT.LEFT);

		label.setText("You can configurate Upgrade Plan Outline Name and URL here.");

		gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;

		label.setLayoutData(gridData);

		_upgradePlanOutlineTableViewer = new TableViewer(pageComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		gridData = new GridData(GridData.FILL_HORIZONTAL);

		_createColumns();

		Table table = _upgradePlanOutlineTableViewer.getTable();

		table.setHeaderVisible(true);

		gridData.heightHint = 400;

		table.setLayoutData(gridData);

		_upgradePlanOutlineTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		_upgradePlanOutlineTableViewer.setInput(_outlines.toArray());

		Composite groupComponent = new Composite(pageComposite, SWT.NULL);

		GridLayout groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);

		gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		groupComponent.setLayoutData(gridData);

		_addButton = new Button(groupComponent, SWT.PUSH);

		_addButton.setText("Add");

		_addButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}

				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					AddOutlineDialog addOutlineDialog = new AddOutlineDialog(getShell());

					addOutlineDialog.create();

					if (addOutlineDialog.open() == Window.OK) {
						String name = addOutlineDialog.getName();
						String url = addOutlineDialog.getURL();

						String outline = name + "," + url;

						_outlines.add(outline);

						_upgradePlanOutlineTableViewer.add(outline);
					}
				}

			});

		setButtonLayoutData(_addButton);

		_removeButton = new Button(groupComponent, SWT.PUSH);

		_removeButton.setText("Remove");

		_removeButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}

				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					StructuredSelection selection = (StructuredSelection)_upgradePlanOutlineTableViewer.getSelection();

					String outline = (String)selection.getFirstElement();

					_outlines.remove(outline);

					_upgradePlanOutlineTableViewer.setInput(_outlines.toArray());
				}

			});

		setButtonLayoutData(_removeButton);

		return pageComposite;
	}

	@Override
	public void init(IWorkbench workbench) {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		_outlines.addAll(upgradePlanner.loadAllUpgradePlanOutlines());
	}

	@Override
	public boolean performOk() {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		upgradePlanner.updateUpgradePlanOutlines(_outlines);

		return super.performOk();
	}

	private void _createColumns() {
		TableViewerColumn tableViewerColumn = _createTableViewerColumn("Outline", 50, _upgradePlanOutlineTableViewer);

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					String s = (String)element;

					return s.split(",")[0];
				}

			});

		tableViewerColumn = _createTableViewerColumn("URL", 100, _upgradePlanOutlineTableViewer);

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					String s = (String)element;

					return s.split(",")[1];
				}

			});
	}

	private TableViewerColumn _createTableViewerColumn(String title, int bound, TableViewer viewer) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);

		TableColumn tableColumn = tableViewerColumn.getColumn();

		tableColumn.setText(title);
		tableColumn.setWidth(bound);
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);

		return tableViewerColumn;
	}

	private Button _addButton;
	private List<String> _outlines = new ArrayList<>();
	private Button _removeButton;
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private TableViewer _upgradePlanOutlineTableViewer;

}