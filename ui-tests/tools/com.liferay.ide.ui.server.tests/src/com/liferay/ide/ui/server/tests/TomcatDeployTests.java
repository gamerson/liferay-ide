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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class TomcatDeployTests extends SwtbotBase {

	@BeforeClass
	public static void startServer() throws IOException {
		envAction.unzipServer();

		envAction.prepareGeoFile();

		envAction.preparePortalExtFile();

		envAction.preparePortalSetupWizardFile();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(_serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(_serverName);

		wizardAction.finish();

		viewAction.servers.start(_serverStoppedLabel);

		jobAction.waitForServerStarted(_serverName);
	}

	@AfterClass
	public static void stopServer() throws IOException {
		viewAction.servers.stop(_serverStartedLabel);

		jobAction.waitForServerStopped(_serverName);

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(_serverName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void deploySampleProject() {
		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test";

		wizardAction.newModule.prepare(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		projectName = "test2";

		wizardAction.newModule.prepare(projectName);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(_serverStartedLabel);

		dialogAction.addAndRemove.addModule(projectName);

		dialogAction.confirm(FINISH);

		jobAction.waitForConsoleContent(_serverName, "STARTED " + projectName + "_", 20 * 1000);
	}

	private static final String _serverName = "Liferay 7-deploy";
	private static final String _serverStartedLabel = _serverName + "  [Started]";
	private static final String _serverStoppedLabel = _serverName + "  [Stopped]";

}