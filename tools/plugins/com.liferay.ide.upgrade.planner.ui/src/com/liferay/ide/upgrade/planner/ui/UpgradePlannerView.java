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

import com.liferay.ide.upgrade.planner.core.UpgradePlan;
import com.liferay.ide.upgrade.planner.core.UpgradePlanner;
import com.liferay.ide.upgrade.planner.ui.tasks.UpgradeTaskStepsViewer;
import com.liferay.ide.upgrade.planner.ui.tasks.UpgradeTasksViewer;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlannerView extends ViewPart implements ISelectionProvider {

	public static final String ID = "com.liferay.ide.upgrade.planner.view";

	public UpgradePlannerView() {
		_serviceTracker = _getServiceTracker();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	@Override
	public void createPartControl(Composite parentComposite) {
		_createPartControl(parentComposite);

		IViewSite viewSite = getViewSite();

		viewSite.setSelectionProvider(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_upgradeTaskStepsViewer != null) {
			_upgradeTaskStepsViewer.dispose();
		}
	}

	@Override
	public ISelection getSelection() {
		return _upgradeTaskStepsViewer.getSelection();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		String upgradePlanName = memento.getString("upgradePlanName");

		if (upgradePlanName != null) {
			UpgradePlanner upgradePlanner = _serviceTracker.getService();

			upgradePlanner.startUpgradePlan(upgradePlanName);
		}
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		Object upgradeTaskViewerInput = _upgradeTasksViewer.getInput();

		if (upgradeTaskViewerInput instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)upgradeTaskViewerInput;

			memento.putString("upgradePlanName", upgradePlan.getName());
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(ISelection selection) {
		_upgradeTaskStepsViewer.setSelection(selection);
	}

	private static ServiceTracker<UpgradePlanner, UpgradePlanner> _getServiceTracker() {
		if (_serviceTracker == null) {
			Bundle bundle = FrameworkUtil.getBundle(UpgradePlannerView.class);

			BundleContext bundleContext = bundle.getBundleContext();

			_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

			_serviceTracker.open();
		}

		return _serviceTracker;
	}

	private void _createPartControl(Composite parentComposite) {
		parentComposite.setLayout(new FillLayout());

		_upgradeTasksViewer = new UpgradeTasksViewer(parentComposite);

		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		upgradePlanner.addListener(_upgradeTasksViewer);

		_upgradeTaskStepsViewer = new UpgradeTaskStepsViewer(parentComposite, _upgradeTasksViewer);

		_upgradeTaskStepsViewer.addSelectionChangedListener(this::_fireSelectionChanged);
	}

	private void _fireSelectionChanged(SelectionChangedEvent selectionChangedEvent) {
		_listeners.forEach(
			selectionChangedListener -> {
				try {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}
				catch (Exception e) {
					UpgradePlannerUIPlugin.logError("Error in selection changed listener.", e);
				}
			});
	}

	private static ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private UpgradeTaskStepsViewer _upgradeTaskStepsViewer;
	private UpgradeTasksViewer _upgradeTasksViewer;

}