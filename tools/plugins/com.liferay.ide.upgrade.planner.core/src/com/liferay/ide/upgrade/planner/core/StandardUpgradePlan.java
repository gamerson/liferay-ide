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

import java.util.List;

import com.liferay.ide.upgrade.planner.api.UpgradePlan;
import com.liferay.ide.upgrade.planner.api.UpgradeTask;

/**
 * @author Gregory Amerson
 */
public class StandardUpgradePlan implements UpgradePlan {

	private final String _name;

	public StandardUpgradePlan(String name) {
		_name = name;
	}

	/* (non-Javadoc)
	 * @see com.liferay.ide.upgrade.planner.api.UpgradePlan#getTasks()
	 */
	@Override
	public List<UpgradeTask> getTasks() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.ide.upgrade.planner.api.UpgradePlan#getName()
	 */
	@Override
	public String getName() {
		return _name;
	}

}