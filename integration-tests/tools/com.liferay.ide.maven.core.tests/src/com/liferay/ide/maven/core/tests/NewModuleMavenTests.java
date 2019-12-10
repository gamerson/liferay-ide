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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.maven.core.tests.base.NewModuleMavenBase;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.sapphire.Value;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class NewModuleMavenTests extends NewModuleMavenBase {

	@Test
	public void createActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("activator");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createApi() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("api");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createContentTargetingReport() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-report");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createContentTargetingRule() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-rule");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createContentTargetingTrackingAction() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-tracking-action");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createControlMenuEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("control-menu-entry");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createFormField() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("form-field");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("mvc-portlet");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPanelApp() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("panel-app");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Ignore("no portlet template since blade 3.7.0")
	@Test
	public void createPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletConfigurationIcon() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-configuration-icon");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletProvider() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-provider");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletToolbarContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-toolbar-contributor");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createRest() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("rest");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createSimulationPanelEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("simulation-panel-entry");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Ignore("ignore as endless building, no this template since blade 3.7.0")
	@Test
	public void createSoyPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("soy-portlet");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createTemplateContextContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("template-context-contributor");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createThemeContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("theme-contributor");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Override
	protected String shape() {
		return "jar";
	}

}