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
import com.liferay.ide.ui.liferay.page.editor.ServerEditor;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ServerWildflyTests extends SwtbotBase {

	@BeforeClass
	public static void prepareServer() throws IOException {
		envAction.unzipWildflyServer();

		envAction.prepareGeoFile();

		envAction.preparePortalWildflyExtFile();

		envAction.prepareWildflyPortalSetupWizardFile();

		String serverName = "Liferay Wildfly 7-initialization";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayWildflyServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayWildflyPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		// String serverStoppedLabel = serverName + " [Stopped]";

		//

		// viewAction.serverStart(serverStoppedLabel);

		//

		// viewAction.serverStartWait();

		//

		// String serverStartedLabel = serverName + " [Started]";

		//

		// viewAction.openLiferayPortalHome(serverStartedLabel);

		//

		// viewAction.serverStop(serverStartedLabel);

		//

		// viewAction.serverStopWait();

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

		String runtimeName = "Liferay 7-wildfly-runtime";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferayWildflyServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 7-wildfly-server";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer("Liferay 7-wildfly-server");

		wizardAction.finish();

		viewAction.showServersView();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		String serverName = "Liferay 7-wildfly-custom-launch-settings";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		ServerEditor serverEditor = new ServerEditor(bot, serverName);
		ServerEditor serverEditorWithLabel = new ServerEditor(bot, serverStoppedLabel);

		try {
			serverEditor.getCustomLaunchSettings().click();
		} catch (Exception e) {
			serverEditorWithLabel.getCustomLaunchSettings().click();
		}

		try {
			serverEditor.getUseDeveloperMode().select();
		} catch (Exception e) {
			serverEditorWithLabel.getUseDeveloperMode().select();
		}

		editorAction.save();

		editorAction.close();

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getDefaultLaunchSettings().click();
		} catch (Exception e) {
			serverEditorWithLabel.getDefaultLaunchSettings().click();
		}

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

}