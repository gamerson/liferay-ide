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

package com.liferay.ide.ui.module.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.LiferayWorkspaceGradleSupport;
import com.liferay.ide.ui.liferay.base.ProjectSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class NewModuleProjectWizardLiferayWorkspaceGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradleSupport liferayWorkspace = new LiferayWorkspaceGradleSupport(bot);

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Test
	public void createActivator() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), ACTIVATOR);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);
	}

	@Test
	public void createApi() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), API);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingReport() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), CONTENT_TARGETING_REPORT);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingRule() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), CONTENT_TARGETING_RULE);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingTrackingAction() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createControlMenuEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), CONTROL_MENU_ENTRY);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFormField() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), FORM_FIELD);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPanelApp() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PANEL_APP);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletConfigurationIcon() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_CONFIGURATION_ICON);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletProvider() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_PROVIDER);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletToolbarContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createRest() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), REST);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createService() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_WRAPPER);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createSimulationPanelEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SIMULATION_PANEL_ENTRY);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createTemplateContextContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createThemeContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), THEME_CONTRIBUTOR);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createWarHook() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_HOOK);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "wars", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getLiferayWorkspaceName(), "wars", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

}