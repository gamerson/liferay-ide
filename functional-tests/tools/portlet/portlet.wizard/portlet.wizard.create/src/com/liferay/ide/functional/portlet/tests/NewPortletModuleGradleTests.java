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

package com.liferay.ide.functional.portlet.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Rui Wang
 * @author Ying Xu
 */
public class NewPortletModuleGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle71Support liferayWorkspace = new LiferayWorkspaceGradle71Support(bot);

	@Ignore("ignore because blade 3.10 remove freemarker-portlet template")
	@Test
	public void createFreemarkerPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), FREEMARKER_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		jobAction.waitForValidate(project.getName());

		viewAction.project.openFile(
			liferayWorkspace.getModuleFiles(
				project.getName(), "src/main/java", project.getName() + ".constants",
				project.getCapitalName() + "PortletKeys.java"));

		String projectName = project.getName();

		validationAction.assertContains(
			"public static final String " + projectName.toUpperCase(), editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createNpmAngularPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), NPM_ANGULAR_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createNpmReactPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), NPM_REACT_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createNpmVuejsPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), NPM_VUEJS_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Ignore("ignore because blade 3.7 remove portlet template")
	@Test
	public void createPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		jobAction.waitForValidate(project.getName());

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore because blade 3.7 remove soy-portlet template")
	@Test
	public void createSoyPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SOY_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore to wait new Spring MVC Portlet Wizard")
	@Test
	public void createSpringMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SPRING_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}