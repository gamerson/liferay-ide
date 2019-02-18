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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionStatus;
import com.liferay.ide.upgrade.plan.ui.internal.tasks.UpgradeTaskViewer;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
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
public class UpgradePlanView extends ViewPart implements ISelectionProvider {

	public static final String ID = "com.liferay.ide.upgrade.plan.view";

	public UpgradePlanView() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanView.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradePlannerServiceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_upgradePlannerServiceTracker.open();
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

		if (_upgradePlanViewer != null) {
			_upgradePlanViewer.dispose();
		}

		if (_upgradeTaskViewer != null) {
			_upgradeTaskViewer.dispose();
		}
	}

	@Override
	public ISelection getSelection() {
		return _upgradeTaskViewer.getSelection();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		Optional.ofNullable(
			memento
		).map(
			m -> m.getString("upgradePlanName")
		).filter(
			Objects::nonNull
		).ifPresent(
			upgradePlanName -> {
				UpgradePlanner upgradePlanner = _upgradePlannerServiceTracker.getService();

				String currentVersion = memento.getString("currentVersion");
				String targetVersion = memento.getString("targetVersion");

				String path = memento.getString("currentProjectLocation");

				Path currentProjectLocation = Paths.get(path);

				UpgradePlan upgradePlan = upgradePlanner.loadUpgradePlan(
					upgradePlanName, currentVersion, targetVersion, currentProjectLocation);

				upgradePlanner.startUpgradePlan(upgradePlan);

				_loadActionStatus(memento, upgradePlan);

				_loadUpgradeProblems(memento, upgradePlan);
			}
		);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		Object upgradeTaskViewerInput = _upgradePlanViewer.getInput();

		if (upgradeTaskViewerInput instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)upgradeTaskViewerInput;

			memento.putString("upgradePlanName", upgradePlan.getName());
			memento.putString("currentVersion", upgradePlan.getCurrentVersion());
			memento.putString("targetVersion", upgradePlan.getTargetVersion());

			Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

			memento.putString("currentProjectLocation", currentProjectLocation.toString());

			_saveActionStatus(memento, upgradePlan);

			_saveUpgradeProblems(memento, upgradePlan);
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(ISelection selection) {
		_upgradeTaskViewer.setSelection(selection);
	}

	private void _createPartControl(Composite parentComposite) {
		parentComposite.setLayout(new FillLayout());

		_upgradePlanViewer = new UpgradePlanViewer(parentComposite);

		_upgradePlanViewer.addPostSelectionChangedListener(this::_fireSelectionChanged);

		UpgradePlanner upgradePlanner = _upgradePlannerServiceTracker.getService();

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradePlanStartedEvent) {
					UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

					UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

					UIUtil.async(() -> setContentDescription("Active upgrade plan: " + upgradePlan.getName()));
				}
			});

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradeTaskStepActionDoneEvent) {
					UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");
				}
			});

		_upgradeTaskViewer = new UpgradeTaskViewer(parentComposite, _upgradePlanViewer);

		_upgradeTaskViewer.addSelectionChangedListener(this::_fireSelectionChanged);

		setContentDescription(
			"No active upgrade plan. Use view menu 'New Upgrade Plan' action to start a new upgrade.");
	}

	private void _fireSelectionChanged(SelectionChangedEvent selectionChangedEvent) {
		_listeners.forEach(
			selectionChangedListener -> {
				try {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}
				catch (Exception e) {
					UpgradePlanUIPlugin.logError("Error in selection changed listener.", e);
				}
			});
	}

	private void _loadActionStatus(IMemento memento, UpgradePlan upgradePlan) {
		List<UpgradeTask> tasks = upgradePlan.getTasks();

		for (UpgradeTask task : tasks) {
			IMemento taskMemento = memento.getChild(task.getId());

			List<UpgradeTaskStep> steps = task.getSteps();

			for (UpgradeTaskStep step : steps) {
				IMemento stepMemento = taskMemento.getChild(step.getId());

				List<UpgradeTaskStepAction> actions = step.getActions();

				for (UpgradeTaskStepAction action : actions) {
					IMemento actionMemento = stepMemento.getChild(action.getId());

					if (actionMemento != null) {
						String status = actionMemento.getString("status");

						action.setStatus(UpgradeTaskStepActionStatus.valueOf(status));
					}
				}
			}
		}
	}

	private void _loadUpgradeProblems(IMemento memento, UpgradePlan upgradePlan) {
		IMemento[] upgradeProblemsMemento = memento.getChildren("upgradeProblem");

		List<UpgradeProblem> upgradeProblems = Stream.of(
			upgradeProblemsMemento
		).map(
			upgradeProblemMemento -> {
				String autoCorrectContext = upgradeProblemMemento.getString("autoCorrectContext");
				String html = upgradeProblemMemento.getString("html");
				String summary = upgradeProblemMemento.getString("summary");
				String ticket = upgradeProblemMemento.getString("ticket");
				String title = upgradeProblemMemento.getString("title");
				String type = upgradeProblemMemento.getString("type");
				String uuid = upgradeProblemMemento.getString("uuid");
				String version = upgradeProblemMemento.getString("version");
				int endOffset = upgradeProblemMemento.getInteger("endOffset");
				int lineNumber = upgradeProblemMemento.getInteger("lineNumber");

				long markerId = 0;

				try {
					markerId = Long.parseLong(upgradeProblemMemento.getString("markerId"));
				}
				catch (NumberFormatException nfe) {
				}

				Integer markerType = upgradeProblemMemento.getInteger("markerType");

				Integer startOffset = upgradeProblemMemento.getInteger("startOffset");
				int status = upgradeProblemMemento.getInteger("status");

				IFile[] resources = CoreUtil.findFilesForLocationURI(
					new File(upgradeProblemMemento.getString("resourceLocation")).toURI());

				UpgradeProblem upgradeProblem = new UpgradeProblem(
					uuid, title, summary, type, ticket, version, resources[0], lineNumber, startOffset, endOffset, html,
					autoCorrectContext, status, markerId, markerType);

				return upgradeProblem;
			}
		).collect(
			Collectors.toList()
		);

		upgradePlan.addUpgradeProblems(upgradeProblems);
	}

	private void _saveActionStatus(IMemento memento, UpgradePlan upgradePlan) {
		List<UpgradeTask> tasks = upgradePlan.getTasks();

		for (UpgradeTask task : tasks) {
			IMemento taskMemento = memento.createChild(task.getId());

			List<UpgradeTaskStep> steps = task.getSteps();

			for (UpgradeTaskStep step : steps) {
				IMemento stepMemento = taskMemento.createChild(step.getId());

				List<UpgradeTaskStepAction> actions = step.getActions();

				for (UpgradeTaskStepAction action : actions) {
					IMemento actionMemento = stepMemento.createChild(action.getId());

					actionMemento.putString("status", String.valueOf(action.getStatus()));
				}
			}
		}
	}

	private void _saveUpgradeProblems(IMemento memento, UpgradePlan upgradePlan) {
		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		for (UpgradeProblem upgradeProblem : upgradeProblems) {
			IMemento upgradeProblemMemento = memento.createChild("upgradeProblem");

			upgradeProblemMemento.putString("autoCorrectContext", upgradeProblem.getAutoCorrectContext());
			upgradeProblemMemento.putString("html", upgradeProblem.getHtml());
			upgradeProblemMemento.putString("summary", upgradeProblem.getSummary());
			upgradeProblemMemento.putString("ticket", upgradeProblem.getTicket());
			upgradeProblemMemento.putString("title", upgradeProblem.getTitle());
			upgradeProblemMemento.putString("type", upgradeProblem.getType());
			upgradeProblemMemento.putString("uuid", upgradeProblem.getUuid());
			upgradeProblemMemento.putString("version", upgradeProblem.getVersion());
			upgradeProblemMemento.putInteger("endOffset", upgradeProblem.getEndOffset());
			upgradeProblemMemento.putInteger("lineNumber", upgradeProblem.getLineNumber());

			long markerId = upgradeProblem.getMarkerId();

			upgradeProblemMemento.putString("markerId", String.valueOf(markerId));

			upgradeProblemMemento.putInteger("markerType", upgradeProblem.getMarkerType());
			upgradeProblemMemento.putInteger("number", upgradeProblem.getNumber());

			IResource resource = upgradeProblem.getResource();

			IPath location = resource.getLocation();

			upgradeProblemMemento.putString("resourceLocation", location.toOSString());

			upgradeProblemMemento.putInteger("startOffset", upgradeProblem.getStartOffset());
			upgradeProblemMemento.putInteger("status", upgradeProblem.getStatus());
		}
	}

	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ServiceTracker<UpgradePlanner, UpgradePlanner> _upgradePlannerServiceTracker;
	private UpgradePlanViewer _upgradePlanViewer;
	private UpgradeTaskViewer _upgradeTaskViewer;

}