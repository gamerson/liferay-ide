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

package com.liferay.ide.ui.liferay;

import com.liferay.ide.ui.liferay.action.DialogAction;
import com.liferay.ide.ui.liferay.action.EditorAction;
import com.liferay.ide.ui.liferay.action.EnvAction;
import com.liferay.ide.ui.liferay.action.ViewAction;
import com.liferay.ide.ui.liferay.action.WizardAction;
import com.liferay.ide.ui.liferay.page.LiferayIDE;
import com.liferay.ide.ui.swtbot.Keys;
import com.liferay.ide.ui.swtbot.UI;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SwtbotBase implements UI, Keys, Messages, FileConstants {

	public static SWTWorkbenchBot bot;
	public static DialogAction dialogAction;
	public static EditorAction editorAction;
	public static EnvAction envAction;
	public static LiferayIDE ide;
	public static ViewAction viewAction;
	public static WizardAction wizardAction;

	@AfterClass
	public static void afterClass() {
		viewAction.deleteProject("init-project");
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();

		ide = new LiferayIDE(bot);

		dialogAction = new DialogAction(bot);
		envAction = new EnvAction(bot);
		editorAction = new EditorAction(bot);
		wizardAction = new WizardAction(bot);
		viewAction = new ViewAction(bot);

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1000;

			ide.getWelcomeView().close();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}

		ide.getLiferayWorkspacePerspective().activate();

		SWTBotPreferences.TIMEOUT = 30 * 1000;

		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT, "30000");
		System.setProperty(SWTBotPreferenceConstants.KEY_DEFAULT_POLL_DELAY, "5000");

		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

		dialogAction.openPreferencesDialog();

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1000;
			
			dialogAction.openPreferencesRecorderDialog();

			dialogAction.preparePreferencesRecorder();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("init-project");

		wizardAction.finishToWait();
	}

	protected static void sleep(long millis) {
		bot.sleep(millis);
	}

	protected void sleep() {
		sleep(1000);
	}

}