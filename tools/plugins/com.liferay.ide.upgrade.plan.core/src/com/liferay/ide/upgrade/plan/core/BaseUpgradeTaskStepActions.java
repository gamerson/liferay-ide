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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Terry Jia
 */
public abstract class BaseUpgradeTaskStepActions extends BaseUpgradeTaskStepAction implements UpgradeTaskStepActions {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(componentContext);

		_lookupActions(componentContext);
	}

	@Override
	public boolean equals(Object object) {
		BaseUpgradeTaskStepActions baseUpgradeTaskStepActions = Adapters.adapt(
			object, BaseUpgradeTaskStepActions.class);

		if (baseUpgradeTaskStepActions == null) {
			return false;
		}

		List<UpgradeTaskStepAction> actions = baseUpgradeTaskStepActions.getActions();

		if (_upgradeTaskStepActions.size() != actions.size()) {
			return false;
		}

		for (int i = 0; i < _upgradeTaskStepActions.size(); i++) {
			UpgradeTaskStepAction action1 = actions.get(i);
			UpgradeTaskStepAction action2 = _upgradeTaskStepActions.get(i);

			if (!isEqualIgnoreCase(action1.getStepId(), action2.getStepId())) {
				return false;
			}
		}

		return super.equals(object);
	}

	@Override
	public List<UpgradeTaskStepAction> getActions() {
		return Collections.unmodifiableList(_upgradeTaskStepActions);
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();

		for (UpgradeTaskStepAction action : _upgradeTaskStepActions) {
			String stepId = action.getStepId();

			hash = 31 * hash + (stepId != null ? stepId.hashCode() : 0);
		}

		return hash;
	}

	public IStatus perform(IProgressMonitor progressMonitor) {
		return Status.OK_STATUS;
	}

	private void _lookupActions(ComponentContext componentContext) {
		Dictionary<String, Object> properties = componentContext.getProperties();

		String actionIdsValue = getStringProperty(properties, "actionIds");

		String[] actionIds = actionIdsValue.split(",");

		_upgradeTaskStepActions = Stream.of(
			actionIds
		).map(
			actionId -> ServicesLookup.getSingleService(UpgradeTaskStepAction.class, "(id=" + actionId + ")")
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toList()
		);
	}

	private List<UpgradeTaskStepAction> _upgradeTaskStepActions;

}