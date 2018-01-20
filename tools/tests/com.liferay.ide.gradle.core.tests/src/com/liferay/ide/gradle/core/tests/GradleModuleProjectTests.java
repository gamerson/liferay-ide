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
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class GradleModuleProjectTests {

	@Test
	public void testProjectTemplateActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("activator-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("activator");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateApi() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("api-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("api");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingReport() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-report-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("content-targeting-report");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingRule() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-rule-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("content-targeting-rule");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingTrackingAction() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-tracking-action-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("content-targeting-tracking-action");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateControlMenuEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("control-menu-entry-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("control-menu-entry");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateFormField() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("form-field-test");
		op.setProjectProvider("gradle-module");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateLayoutTemplate() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("layout-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("layout-template");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, _monitor);

		Assert.assertTrue(project.getFile("build/libs/layout-test.war").exists());
	}

	@Test
	public void testProjectTemplateMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("mvc-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("mvc-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmAngularPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-angular-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-angular-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmBillboardjsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-billboardjs-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-billboardjs-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmIsomorphicPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-isomorphic-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-isomorphic-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmJqueryPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-jquery-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-jquery-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmMetaljsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-metaljs-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-metaljs-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmReactPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-react-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-react-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmVuejsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-vuejs-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("npm-vuejs-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePanelApp() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("panel-app-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("panel-app");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletConfigurationIcon() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-configuration-icon-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("portlet-configuration-icon");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletProvider() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-provider-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("portlet-provider");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletToolbarContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-toolbar-contributor-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("portlet-toolbar-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateRest() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("rest-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("rest");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateService() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("service");
		op.setServiceName("com.liferay.portal.kernel.events.LifecycleAction");

		IProject project = _create(op);

		IFile serviceFile = project.getFile("src/main/java/service/test/ServiceTest.java");

		Assert.assertTrue(serviceFile.exists());

		StringBuffer sb = new StringBuffer();

		sb.append("package service.test;\n");
		sb.append("import com.liferay.portal.kernel.events.ActionException;\n");
		sb.append("import com.liferay.portal.kernel.events.LifecycleAction;\n");
		sb.append("import com.liferay.portal.kernel.events.LifecycleEvent;\n");
		sb.append("import org.osgi.service.component.annotations.Component;\n");
		sb.append("@Component(\n");
		sb.append("immediate = true, property = {\"key=login.events.pre\"},\n");
		sb.append("service = LifecycleAction.class\n");
		sb.append(")\n");
		sb.append("public class ServiceTest implements LifecycleAction {\n");
		sb.append("@Override public void processLifecycleEvent(LifecycleEvent lifecycleEvent)\n");
		sb.append("throws ActionException { }\n");
		sb.append("}");

		try (ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes())) {
			serviceFile.setContents(in, IResource.FORCE, _monitor);
		}

		_verifyProject(project);
	}

	@Test
	public void testProjectTemplateServiceBuilder() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-builder-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("service-builder");
		op.setPackageName("com.liferay.test");

		IProject parent = _create(op);

		Assert.assertTrue(parent != null && parent.exists());

		IProject api = CoreUtil.getProject("service-builder-test-api");

		Assert.assertTrue(api != null && api.exists());

		IProject service = CoreUtil.getProject("service-builder-test-service");

		Assert.assertTrue(service != null && service.exists());

		api.build(IncrementalProjectBuilder.FULL_BUILD, _monitor);

		service.build(IncrementalProjectBuilder.FULL_BUILD, _monitor);

		IBundleProject apiBundle = LiferayCore.create(IBundleProject.class, api);

		Assert.assertNotNull(apiBundle);

		IPath apiOutput = apiBundle.getOutputBundle(true, _monitor);

		Assert.assertNotNull(apiOutput);

		Assert.assertTrue(apiOutput.toFile().exists());

		Assert.assertEquals("com.liferay.test.api-1.0.0.jar", apiOutput.lastSegment());

		IBundleProject serviceBundle = LiferayCore.create(IBundleProject.class, service);

		IPath serviceOutput = serviceBundle.getOutputBundle(true, _monitor);

		Assert.assertNotNull(serviceOutput);

		Assert.assertTrue(serviceOutput.toFile().exists());

		Assert.assertEquals("com.liferay.test.service-1.0.0.jar", serviceOutput.lastSegment());
	}

	@Test
	public void testProjectTemplateServiceWrapper() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-wrapper-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("service-wrapper");
		op.setServiceName("com.liferay.portal.kernel.service.UserLocalServiceWrapper");
		op.setComponentName("MyServiceWrapper");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSimulationPanelEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("simulation-panel-entry-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("simulation-panel-entry");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSoyPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("soy-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("soy-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSpringMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("spring-mvc-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("spring-mvc-portlet");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, _monitor);

		Assert.assertTrue(project.getFile("build/libs/spring-mvc-portlet-test.war").exists());
	}

	@Test
	public void testProjectTemplateTemplateContextContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("template-context-contributor-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("template-context-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateTheme() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("theme-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("theme");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateThemeContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("theme-contributor-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("theme-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateWarHook() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("war-hook-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("war-hook");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, _monitor);

		Assert.assertTrue(project.getFile("build/libs/war-hook-test.war").exists());
	}

	@Test
	public void testProjectTemplateWarMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("war-mvc-portlet-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("war-mvc-portlet");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, _monitor);

		Assert.assertTrue(project.getFile("build/libs/war-mvc-portlet-test.war").exists());
	}

	private IProject _create(NewLiferayModuleProjectOp op) throws Exception {
		Status status = op.execute(ProgressMonitorBridge.create(_monitor));

		Assert.assertNotNull(status);
		Assert.assertTrue(status.message(), status.ok());

		Util.waitForBuildAndValidation();

		return CoreUtil.getProject(op.getProjectName().content());
	}

	private IProject _createAndBuild(NewLiferayModuleProjectOp op) throws Exception {
		Assert.assertTrue(op.validation().message(), op.validation().ok());

		IProject project = _create(op);

		_verifyProject(project);

		return project;
	}

	private void _verifyProject(IProject project) throws Exception {
		Assert.assertNotNull(project);
		Assert.assertTrue(project.exists());

		Assert.assertTrue(project.getFile("build.gradle").exists());

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, _monitor);

		Util.waitForBuildAndValidation();

		project.build(IncrementalProjectBuilder.FULL_BUILD, _monitor);

		Util.waitForBuildAndValidation();

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);

		IPath outputBundle = bundleProject.getOutputBundle(true, _monitor);

		Assert.assertNotNull(outputBundle);

		Assert.assertTrue(outputBundle.toFile().exists());
	}

	private IProgressMonitor _monitor = new NullProgressMonitor();

}