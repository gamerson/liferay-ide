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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem;

import com.liferay.ide.upgrade.plan.core.MessageDialogTaskStep;
import com.liferay.ide.upgrade.plan.core.MigrationProblemsContainer;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepDoneEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.plan.core.util.UpgradeAssistantSettingsUtil;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=remove_previous_result", "requirement=optional", "order=200", "taskId=find_upgrade_problems",
		"title=Remove Previous Result"
	},
	service = UpgradeTaskStep.class
)
public class RemovePreviousResultTaskStep extends MessageDialogTaskStep {

	@Override
	public IStatus execute(IProgressMonitor progressMonitor) {
		try {
			MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
				MigrationProblemsContainer.class);

			if (container != null) {
				UpgradeAssistantSettingsUtil.setObjectToStore(MigrationProblemsContainer.class, null);
			}
		}
		catch (IOException ioe) {
		}

		getUpgradePlanner().dispatch(new UpgradeTaskStepDoneEvent());

		return Status.OK_STATUS;
	}

	@Override
	public String getLabel() {
		return "Remove Previous Result?";
	}

	@Override
	public String getMessage() {
		return "All previous results will be deleted.";
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

}