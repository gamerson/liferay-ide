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
import com.liferay.ide.ui.liferay.base.LiferayWorkspaceMavenSupport;
import com.liferay.ide.ui.liferay.base.TomcatRunningSupport;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
@Ignore("ignore for more research")
public class DeployModuleLiferayWorkspaceMavenTomcatTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@ClassRule
	public static TomcatRunningSupport tomcat = new TomcatRunningSupport(bot);

	@Test
	public void deployActivator() {
		String projectName = "deploy-activator-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, ACTIVATOR);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		viewAction.servers.openAddAndRemoveDialog(tomcat.getStartedLabel());

		dialogAction.addAndRemove.addModule(projectName);

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(tomcat.getStartedLabel(), projectName);

		jobAction.waitForConsoleContent(
			tomcat.getServerName(), "STARTED " + projectName.replace('-', '.') + "_", 20 * 1000);

		viewAction.project.closeAndDelete(projectNames);
	}

}