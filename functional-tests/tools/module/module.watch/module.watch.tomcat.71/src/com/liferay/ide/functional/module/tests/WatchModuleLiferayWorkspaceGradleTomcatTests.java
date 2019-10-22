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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.LiferaryWorkspaceTomcat7xSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Rui Wang
 */
public class WatchModuleLiferayWorkspaceGradleTomcatTests extends SwtbotBase {

	public static LiferayWorkspaceGradle72Support workspace = new LiferayWorkspaceGradle72Support(bot);
	public static LiferaryWorkspaceTomcat7xSupport server = new LiferaryWorkspaceTomcat7xSupport(bot, workspace);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningLiferayWokrspaceRuleChain(bot, workspace, server);

	@Test
	public void watchMVCPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET, "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.servers.startWatchingProject(server.getStartedLabel(), workspace.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModuleFromPortal(
			server.getStartedLabel(), workspace.getName() + " [watching]", project.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + "_", M1);

		viewAction.servers.stopWatchingProject(server.getStartedLabel(), workspace.getName() + " [watching]");

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(workspace.getModuleFiles(project.getName()));

		Assert.assertFalse(viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName()));
	}

	@Test
	public void watchServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_BUILDER, "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.runBuildService(workspace.getName(), "modules", project.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.servers.startWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + "-api");

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + ".api_", M1);

		viewAction.servers.startWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + "-service");

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + ".service_", M1);

		viewAction.servers.stopWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + "-api [Active] [watching]");

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + ".api_", M1);

		viewAction.servers.refresh(server.getStartedLabel(), workspace.getName());

		ide.sleep(5000);

		Assert.assertTrue(
			viewAction.servers.visibleWorkspaceTry(
				server.getStartedLabel(), workspace.getName(), project.getName() + "-service [Installed] [watching]"));

		viewAction.servers.startWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + "-api");

		viewAction.servers.stopWatchingProject(server.getStartedLabel(), workspace.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(
			workspace.getModuleFiles(project.getName(), project.getName() + "-service"));

		viewAction.project.closeAndDeleteFromDisk(
			workspace.getModuleFiles(project.getName(), project.getName() + "-api"));

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDelete(workspace.getModuleFiles(project.getName()));
	}

	@Test
	public void watchWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET, "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.servers.startWatchingProject(server.getStartedLabel(), workspace.getName(), project.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + "_", M2);

		viewAction.servers.stopWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + " [watching]");

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + "_", M1);

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(workspace.getWarFiles(project.getName()));

		Assert.assertFalse(viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}