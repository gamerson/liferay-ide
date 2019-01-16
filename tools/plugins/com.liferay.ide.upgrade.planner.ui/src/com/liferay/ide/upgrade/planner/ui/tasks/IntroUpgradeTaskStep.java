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

package com.liferay.ide.upgrade.planner.ui.tasks;

import com.liferay.ide.upgrade.planner.core.UpgradeTask;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStepRequirement;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStepStatus;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class IntroUpgradeTaskStep implements UpgradeTaskStep {

	public IntroUpgradeTaskStep(UpgradeTask upgradeTask) {
		_upgradeTask = upgradeTask;
	}

	@Override
	public IStatus execute(IProgressMonitor progressMonitor) {
		return Status.OK_STATUS;
	}

	@Override
	public String getDescription() {
		return _upgradeTask.getDescription();
	}

	@Override
	public UpgradeTaskStepRequirement getRequirement() {
		return null;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Introduction";
	}

	@Override
	public String getUrl() {
		return null;
	}

	private UpgradeTask _upgradeTask;

}