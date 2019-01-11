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

package com.liferay.ide.upgrade.planner.core;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.planner.api.UpgradePlan;
import com.liferay.ide.upgrade.planner.api.UpgradePlanner;

/**
 * @author Gregory Amerson
 */
@Component
public class UpgradePlannerService implements UpgradePlanner {

	/* (non-Javadoc)
	 * @see com.liferay.ide.upgrade.planner.api.UpgradePlanner#createNewUpgradePlan()
	 */
	@Override
	public UpgradePlan createNewUpgradePlan(String name) {
		return new StandardUpgradePlan(name);
	}

	/* (non-Javadoc)
	 * @see com.liferay.ide.upgrade.planner.api.UpgradePlanner#startUpgradePlan(com.liferay.ide.upgrade.planner.api.UpgradePlan)
	 */
	@Override
	public void startUpgradePlan(UpgradePlan upgradePlan) {
	}

	/* (non-Javadoc)
	 * @see com.liferay.ide.upgrade.planner.api.UpgradePlanner#stopUpgradePlan(com.liferay.ide.upgrade.planner.api.UpgradePlan)
	 */
	@Override
	public void stopUpgradePlan(UpgradePlan upgradePlan) {
	}

}
