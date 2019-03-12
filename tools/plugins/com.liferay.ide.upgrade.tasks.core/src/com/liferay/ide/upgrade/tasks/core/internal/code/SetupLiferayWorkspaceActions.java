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

package com.liferay.ide.upgrade.tasks.core.internal.code;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepActions;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.code.SetupLiferayWorkspaceActionsKeys;
import com.liferay.ide.upgrade.tasks.core.code.SetupLiferayWorkspaceStepKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=" + SetupLiferayWorkspaceActionsKeys.ID, "order=0", "requirement=required",
		"stepId=" + SetupLiferayWorkspaceStepKeys.ID, "title=" + SetupLiferayWorkspaceActionsKeys.TITLE,
		"actionIds=" + SetupLiferayWorkspaceActionsKeys.ACTION_IDS,
		"description=" + SetupLiferayWorkspaceActionsKeys.DESCRIPTION
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class SetupLiferayWorkspaceActions extends BaseUpgradeTaskStepActions {
}