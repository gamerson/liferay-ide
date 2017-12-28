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

package com.liferay.ide.ui.theme.tests;

import com.liferay.ide.ui.liferay.base.SdkBase;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewThemeProjectSdkTests extends SdkBase {

	@Ignore("ignore as the jre problem on testing server for right now")
	@Test
	public void createTheme() {
		wizardAction.openNewLiferayPluginProjectWizard();

		String projectName = "test-theme";

		wizardAction.newPlugin.prepareThemeSdk(projectName);

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectName);
	}

}