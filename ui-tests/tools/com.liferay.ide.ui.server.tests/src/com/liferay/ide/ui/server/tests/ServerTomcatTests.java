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

import org.eclipse.core.runtime.IPath;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcatTests extends SwtbotBase {

	@BeforeClass
	public static void prepareServer() throws IOException {
		envAction.unzipServer();

		envAction.prepareGeoFile();

		envAction.preparePortalExtFile();

		envAction.preparePortalSetupWizardFile();

		String serverName = "Liferay 7-initialization";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.serverStart(serverStoppedLabel);

		jobAction.waitForServerStarted(serverName);

		String serverStartedLabel = serverName + "  [Started]";

		// viewAction.openLiferayPortalHome(serverStartedLabel);

		viewAction.serverStop(serverStartedLabel);

		jobAction.waitForServerStopped(serverName);

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay7RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 7-add-runtime";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay7ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 7-add-server";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer("Liferay 7-add-server");

		wizardAction.finish();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		String serverName = "Liferay 7-custom-launch-settings";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.selectCustomLaunchSettings();

		editorAction.server.selectUseDeveloperMode();

		editorAction.save();

		editorAction.close();

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.selectDefaultLaunchSettings();

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChangeAndStart() {
		String serverName = "Liferay 7-custom-launch-settings-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.selectCustomLaunchSettings();

		editorAction.server.selectUseDeveloperMode();

		editorAction.save();

		editorAction.close();

		viewAction.serverStart(serverStoppedLabel);

		jobAction.waitForServerStarted(serverName);

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		jobAction.waitForServerStopped(serverName);

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.selectDefaultLaunchSettings();

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChange() {
		String serverName = "Liferay 7-http-port-change";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.setHttpPort("8081");

		editorAction.save();

		editorAction.close();

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.setHttpPort("8080");

		editorAction.save();

		editorAction.close();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChangeAndStart() {
		String serverName = "Liferay 7-http-port-change-and-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.setHttpPort("8082");

		editorAction.save();

		editorAction.close();

		viewAction.serverStart(serverStoppedLabel);

		jobAction.waitForServerStarted(serverName);

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		jobAction.waitForServerStopped(serverName);

		viewAction.openServerEditor(serverStoppedLabel);

		editorAction.server.setHttpPort("8080");

		editorAction.save();

		editorAction.close();
	}

	@Test
	public void testLiferay7ServerDebug() {
		String serverName = "Liferay 7-debug";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.serverDebug(serverStoppedLabel);

		jobAction.waitForServerStarted(serverName);

		String serverDebuggingLabel = serverName + "  [Debugging]";

		viewAction.serverStop(serverDebuggingLabel);

		jobAction.waitForServerStopped(serverName);

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

}