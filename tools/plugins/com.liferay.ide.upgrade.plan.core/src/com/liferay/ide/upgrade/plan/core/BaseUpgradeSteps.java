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

import java.util.Dictionary;

import org.eclipse.core.runtime.Adapters;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Terry Jia
 */
public class BaseUpgradeSteps extends BaseUpgradeStep implements UpgradeSteps {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(componentContext);

		Dictionary<String, Object> properties = componentContext.getProperties();

		String stepIdsValue = getStringProperty(properties, "stepIds", "");

		_stepIds = stepIdsValue.split(",");
	}

	@Override
	public boolean equals(Object object) {
		BaseUpgradeSteps baseUpgradeSteps = Adapters.adapt(object, BaseUpgradeSteps.class);

		if (baseUpgradeSteps == null) {
			return false;
		}

		String[] stepIds = baseUpgradeSteps.getStepIds();

		if (_stepIds.length != stepIds.length) {
			return false;
		}

		for (int i = 0; i < _stepIds.length; i++) {
			String stepId1 = stepIds[i];
			String stepId2 = _stepIds[i];

			if (!isEqualIgnoreCase(stepId1, stepId2)) {
				return false;
			}
		}

		return super.equals(object);
	}

	@Override
	public String[] getStepIds() {
		return _stepIds;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();

		for (String stepId : _stepIds) {
			hash = 31 * hash + (stepId != null ? stepId.hashCode() : 0);
		}

		return hash;
	}

	private String[] _stepIds;

}