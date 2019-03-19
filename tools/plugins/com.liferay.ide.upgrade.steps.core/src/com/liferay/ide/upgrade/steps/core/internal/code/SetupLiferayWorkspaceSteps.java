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

package com.liferay.ide.upgrade.steps.core.internal.code;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeSteps;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.steps.core.code.SetupLiferayWorkspaceStepKeys;
import com.liferay.ide.upgrade.steps.core.code.SetupLiferayWorkspaceStepsKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=" + SetupLiferayWorkspaceStepsKeys.ID, "order=0", "requirement=required",
		"parentId=" + SetupLiferayWorkspaceStepKeys.ID, "title=" + SetupLiferayWorkspaceStepsKeys.TITLE,
		"stepIds=" + SetupLiferayWorkspaceStepsKeys.STEP_IDS,
		"description=" + SetupLiferayWorkspaceStepsKeys.DESCRIPTION
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class SetupLiferayWorkspaceSteps extends BaseUpgradeSteps {
}