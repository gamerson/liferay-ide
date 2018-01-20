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

package com.liferay.ide.project.core.tests;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.util.SapphireUtil;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp625Tests extends NewLiferayPluginProjectOpBase {

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Ignore
	@Override
	@Test
	public void testLocationListener() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testLocationListener();
	}

	@Ignore
	@Override
	@Test
	public void testNewJsfRichfacesProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testNewJsfRichfacesProjects();
	}

	@Override
	@Test
	public void testNewLayoutAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testNewLayoutAntProject();
	}

	@Ignore
	@Test
	public void testNewProjectCustomLocationPortlet() throws Exception {

		// not supported in 6.2.3

	}

	@Ignore
	@Test
	public void testNewProjectCustomLocationWrongSuffix() throws Exception {

		// not supported in 6.2.3

	}

	@Ignore
	@Test
	public void testNewSDKProjectCustomLocation() throws Exception {

		// not supported in 6.2.3

	}

	@Ignore
	@Test
	public void testNewSDKProjectEclipseWorkspace() throws Exception {

		// not supported in 6.2.3

	}

	@Override
	@Test
	public void testNewSDKProjectInSDK() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testNewSDKProjectInSDK();
	}

	@Override
	@Test
	public void testNewSDKProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		createAntProject(newProjectOp("test-name-1"));
		createAntProject(newProjectOp("test_name_1"));
		createAntProject(newProjectOp("-portlet-portlet"));
		createAntProject(newProjectOp("-portlet-hook"));

		NewLiferayPluginProjectOp op = newProjectOp("-hook-hook");

		op.setPluginType(PluginType.hook);

		createAntProject(op);
	}

	@Override
	@Test
	public void testNewThemeProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testNewThemeProjects();
	}

	@Test
	public void testNewWebAntProjectValidation() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String projectName = "test-web-project-sdk";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPluginType(PluginType.web);

		op.setSdkLocation(PathBridge.create(getLiferayPluginsSdkDir()));

		Assert.assertEquals(
			"The selected Plugins SDK does not support creating new web type plugins. Please configure version 7.0 or greater.",
			SapphireUtil.message(op.getSdkLocation()));
	}

	@Override
	@Test
	public void testPluginTypeListener() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testPluginTypeListener(true);
	}

	@Test
	public void testProjectNameValidation() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testProjectNameValidation("project-name-validation-623");
	}

	@Test
	public void testProjectNameValidationServiceAfterProjectCreated() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		// test service-builder project

		NewLiferayPluginProjectOp opCreateProjectA = newProjectOp("test-project-name");

		opCreateProjectA.setIncludeSampleCode(false);
		opCreateProjectA.setPluginType(PluginType.portlet);

		createAntProject(opCreateProjectA);

		Status projectNameAValidationResult = opCreateProjectA.getProjectName().validation();

		Assert.assertEquals(true, projectNameAValidationResult.ok());

		NewLiferayPluginProjectOp opCreateProjectB = newProjectOp("test-project-name");

		Status projectNameBValidationResult = opCreateProjectB.getProjectName().validation();

		Assert.assertEquals(false, projectNameBValidationResult.ok());
	}

	@Override
	protected String getServiceXmlDoctype() {
		return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
	}

}