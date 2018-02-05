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

package com.liferay.ide.ui.fragment.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.ProjectSupport;
import com.liferay.ide.ui.liferay.base.TomcatSupport;
import com.liferay.ide.ui.liferay.page.wizard.NewFragmentFilesWizard;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ValidationFragmentFilesTests extends SwtbotBase {

	@ClassRule
	public static TomcatSupport tomcat = new TomcatSupport(bot);

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Test
	public void checkInitialState() {
		wizardAction.openFileMenuFragmentFilesWizard();

		Assert.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, _newFragmentFilesWizard.getProjectName().getText());

		// Assert.assertEquals("<None>", _newFragmentFilesWizard.getLiferyRuntimes().getText());

		Assert.assertTrue(_newFragmentFilesWizard.getNewRuntimeBtn().isEnabled());

		Assert.assertEquals(StringPool.BLANK, _newFragmentFilesWizard.getHostOsgiBundle().getText());

		Assert.assertFalse(_newFragmentFilesWizard.getAddOverrideFilesBtn().isEnabled());

		Assert.assertFalse(_newFragmentFilesWizard.getDeleteBtn().isEnabled());

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void testAddAllFilesOnFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnAllFilesFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		// wait for IDE-3566 fixed
		// Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnNoneFilesFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnNonFragment() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		Assert.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		Assert.assertFalse(_newFragmentFilesWizard.getAddOverrideFilesBtn().isEnabled());

		Assert.assertFalse(_newFragmentFilesWizard.getDeleteBtn().isEnabled());

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	private static final NewFragmentFilesWizard _newFragmentFilesWizard = new NewFragmentFilesWizard(bot);

}