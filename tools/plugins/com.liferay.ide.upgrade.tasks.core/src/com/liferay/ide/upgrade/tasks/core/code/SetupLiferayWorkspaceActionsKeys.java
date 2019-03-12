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

package com.liferay.ide.upgrade.tasks.core.code;

/**
 * @author Terry Jia
 */
public class SetupLiferayWorkspaceActionsKeys {

	public static final String ACTION_IDS =
		ImportLiferayWorkspaceActionKeys.ID + "," + CreateNewLiferayWorkspaceActionKeys.ID;

	public static final String DESCRIPTION =
		"In order to continue upgrade plan, you need {0} or {0} to have one Liferay Workspace inside this plan.";

	public static final String ID = "setup_liferay_workspace";

	public static final String TITLE = "Setup Liferay Workspace";

}