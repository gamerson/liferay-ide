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

package com.liferay.ide.upgrade.planner.internal.config;

import com.liferay.ide.upgrade.planner.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStepStatus;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=scan_portal_ext_properties", "requirement=required", "service.ranking=100", "taskId=analyze_portal_ext_properties", "title=Scan Portal Ext Properties"
	},
	service = UpgradeTaskStep.class
)
public class ScanPortalExtPropertiesTaskStep extends BaseUpgradeTaskStep {

	@Override
	public IStatus execute(IProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

}