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

package com.liferay.ide.functional.modules.ext.base;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.workspace.LiferayWorkspaceGradleSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Rui Wang
 * @author Ashley Yuan
 */
public class NewModulesExtWizardGradleBase extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradleSupport liferayWorkspace = new LiferayWorkspaceGradleSupport(bot) {

		@Override
		public void prepareGradleWorkspace() {
			wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(getName());
		}

	};

	public void addModulesExtWithJava() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("com", "liferay", "login", "web", "internal", "constants", "LoginPortletKeys.java");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/java",
				"com.liferay.login.web.internal.constants", "LoginPortletKeys.java"));
	}

	public void addModulesExtWithJSP() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("META-INF", "resources", "configuration.jsp");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "META-INF", "resources",
				"configuration.jsp"));
	}

	public void addModulesExtWithLanguageProperties() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("content", "Language.properties");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "content",
				"Language.properties"));
	}

	public void addModulesExtWithManifest() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("META-INF", "MANIFEST.MF");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "META-INF", "MANIFEST.MF"));
	}

	public void addModulesExtWithoutOverrideFiles() {
		overrideOriginalModule("com.liferay.login.web");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();
	}

	public void addModulesExtWithPortletProperties() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("portlet.properties");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "portlet.properties"));
	}

	public void addModulesExtWithResourceAction() {
		overrideOriginalModule("com.liferay.login.web");

		addOverrideFiles("resource-actions", "default.xml");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "resource-actions",
				"default.xml"));
	}

	public void addOverrideFiles(String... file) {
		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile(file);
	}

	public void overrideOriginalModule(String module) {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText(module);

		dialogAction.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}