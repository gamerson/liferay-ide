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

package com.liferay.ide.gradle.core.tests;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.gradle.core.tests.base.NewModuleGradleBase;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Value;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
public class NewModuleGradleTests extends NewModuleGradleBase {

	@Test
	public void createActivator() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("activator");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createApi() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("api");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createContentTargetingReport() {
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
	public void createContentTargetingRule() {
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
	public void createContentTargetingTrackingAction() {
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
	public void createControlMenuEntry() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("control-menu-entry");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createFormField() {
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
	public void createMvcPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("mvc-portlet");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPanelApp() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("panel-app");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Ignore("no portlet template since blade 3.7.0")
	@Test
	public void createPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletConfigurationIcon() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-configuration-icon");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletProvider() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-provider");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createPortletToolbarContributor() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-toolbar-contributor");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createRest() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("rest");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createService() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("service");
		op.setComponentName("MyService");
		op.setPackageName("com.liferay.test");
		op.setServiceName("com.liferay.portal.kernel.json.JSONFactoryUtil");

		createOrImportAndBuild(op, projectSupport.getName(), null, false);

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createServiceBuilder() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("service-builder");
		op.setPackageName("com.liferay.test");

		createOrImport(op, projectSupport.getName());

		assertProjectExists(projectSupport.getName());

		assertBundleProject(projectSupport.getName() + "-api");

		IBundleProject bundleProject = assertBundleProject(projectSupport.getName() + "-service");

		IProjectBuilder projectBuilder = bundleProject.adapt(IProjectBuilder.class);

		projectBuilder.buildService(new NullProgressMonitor());

		verifyProject(projectSupport.getName() + "-api");

		assertProjectFileExists(projectSupport.getName() + "-api", "build/libs/com.liferay.test.api-1.0.0.jar");

		assertBundleProject(projectSupport.getName() + "-service");

		verifyProject(projectSupport.getName() + "-service");

		assertProjectFileExists(projectSupport.getName() + "-service", "build/libs/com.liferay.test.service-1.0.0.jar");

		deleteProject(projectSupport.getName() + "-api");

		deleteProject(projectSupport.getName() + "-service");

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createServiceWrapper() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("service-wrapper");
		op.setPackageName("com.liferay.test");
		op.setServiceName("com.liferay.portal.kernel.service.UserLocalServiceWrapper");
		op.setComponentName("MyServiceWrapper");

		createOrImportAndBuild(op, projectSupport.getName(), null, false);

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createSimulationPanelEntry() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("simulation-panel-entry");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Ignore("no this template since blade 3.7.0")
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
	public void createTemplateContextContributor() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(projectSupport.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("template-context-contributor");

		createOrImportAndBuild(op, projectSupport.getName());

		deleteProject(projectSupport.getName());
	}

	@Test
	public void createThemeContributor() {
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