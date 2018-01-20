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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp701Tests extends NewLiferayPluginProjectOpBase {

	@BeforeClass
	public static void removeAllProjects() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();

		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, monitor);

			Assert.assertFalse(project.exists());
		}
	}

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Override
	public String getRuntimeVersion() {
		return "7.0.2";
	}

	@Ignore
	@Override
	@Test
	public void testLocationListener() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testNewJsfAntProjects() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testNewJsfRichfacesProjects() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testNewLayoutAntProject() throws Exception {
	}

	@Ignore
	@Test
	public void testNewProjectCustomLocationPortlet() throws Exception {
	}

	@Ignore
	@Test
	public void testNewProjectCustomLocationWrongSuffix() throws Exception {
	}

	@Ignore
	@Test
	public void testNewSDKProjectCustomLocation() throws Exception {
	}

	@Ignore
	@Test
	public void testNewSDKProjectEclipseWorkspace() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testNewSDKProjectInSDK() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testNewSDKProjects() throws Exception {
	}

	@Override
	@Test
	public void testNewThemeProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		super.testNewThemeProjects();
	}

	@Ignore
	@Test
	public void testNewWebAntProjectValidation() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testPluginTypeListener() throws Exception {
	}

	@Ignore
	@Test
	public void testProjectNameValidation() throws Exception {
	}

	@Override
	protected IPath getLiferayPluginsSdkDir() {
		return ProjectCore.getDefaultStateLocation().append("com.liferay.portal.plugins.sdk-1.0.16-withdependencies");
	}

	@Override
	protected IPath getLiferayPluginsSDKZip() {
		return getLiferayBundlesPath().append("com.liferay.portal.plugins.sdk-1.0.16-withdependencies.zip");
	}

	@Override
	protected String getLiferayPluginsSdkZipFolder() {
		return "com.liferay.portal.plugins.sdk-1.0.16-withdependencies/";
	}

	@Override
	protected IPath getLiferayRuntimeDir() {
		return ProjectCore.getDefaultStateLocation().append("liferay-ce-portal-7.0-ga5/tomcat-8.0.32");
	}

	@Override
	protected IPath getLiferayRuntimeZip() {
		return getLiferayBundlesPath().append("liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip");
	}

	@Override
	protected String getServiceXmlDoctype() {
		return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 7.0.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_7_0_0.dtd";
	}

}