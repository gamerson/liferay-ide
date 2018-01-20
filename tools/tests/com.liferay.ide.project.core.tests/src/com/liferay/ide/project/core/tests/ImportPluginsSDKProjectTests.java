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

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.SDKClasspathContainer;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.net.URL;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class ImportPluginsSDKProjectTests extends ProjectCoreBase {

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Test
	public void testImportBasicHookProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		IPath projectPath = _importProject("hooks", "Import-IDE3.0-hook");

		IProject hookProjectForIDE3 = ProjectImportUtil.importProject(projectPath, new NullProgressMonitor(), null);

		Assert.assertNotNull(hookProjectForIDE3);

		IJavaProject javaProject = JavaCore.create(hookProjectForIDE3);

		IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();

		List<IClasspathEntry> rawClasspaths = Arrays.asList(rawClasspath);

		boolean hasPluginClasspathDependencyContainer = _isLiferayRuntimePluginClassPath(
			rawClasspaths, SDKClasspathContainer.ID);

		Assert.assertEquals(hasPluginClasspathDependencyContainer, true);
	}

	@Test
	public void testImportConfiguredPortletProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		IPath projectPath = _importProject("portlets", "Import-Old-Configured-portlet");

		IProject portletProjectForIDE3 = ProjectImportUtil.importProject(projectPath, new NullProgressMonitor(), null);

		Assert.assertNotNull(portletProjectForIDE3);

		IJavaProject javaProject = JavaCore.create(portletProjectForIDE3);

		IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();

		List<IClasspathEntry> rawClasspaths = Arrays.asList(rawClasspath);

		boolean hasOldPluginClasspathContainer = _isLiferayRuntimePluginClassPath(
			rawClasspaths, PluginClasspathContainerInitializer.ID);

		boolean hasPluginClasspathDependencyContainer = _isLiferayRuntimePluginClassPath(
			rawClasspaths, SDKClasspathContainer.ID);

		boolean hasOldRuntimeClasspathContainer = _isLiferayRuntimePluginClassPath(
			rawClasspaths, "com.liferay.studio.server.tomcat.runtimeClasspathProvider");

		Assert.assertEquals(hasOldPluginClasspathContainer, false);
		Assert.assertEquals(hasOldRuntimeClasspathContainer, false);
		Assert.assertEquals(hasPluginClasspathDependencyContainer, true);
	}

	@Test
	public void testSDKSetting() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		SDK sdk = SDKUtil.getWorkspaceSDK();

		Map<String, Object> sdkProperties = sdk.getBuildProperties(true);

		Assert.assertNotNull(sdkProperties.get("app.server.type"));
		Assert.assertNotNull(sdkProperties.get("app.server.dir"));
		Assert.assertNotNull(sdkProperties.get("app.server.deploy.dir"));
		Assert.assertNotNull(sdkProperties.get("app.server.lib.global.dir"));
		Assert.assertNotNull(sdkProperties.get("app.server.parent.dir"));
		Assert.assertNotNull(sdkProperties.get("app.server.portal.dir"));

		Assert.assertEquals(sdkProperties.get("app.server.type"), "tomcat");

		IPath path = getLiferayRuntimeDir();

		Assert.assertEquals(sdkProperties.get("app.server.dir"), path.toPortableString());
		Assert.assertEquals(sdkProperties.get("app.server.deploy.dir"), path.append("webapps").toPortableString());
		Assert.assertEquals(sdkProperties.get("app.server.lib.global.dir"), path.append("lib/ext").toPortableString());
		Assert.assertEquals(sdkProperties.get("app.server.parent.dir"), path.removeLastSegments(1).toPortableString());
		Assert.assertEquals(sdkProperties.get("app.server.portal.dir"), path.append("webapps/ROOT").toPortableString());
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

	private IPath _importProject(String pluginType, String name) throws Exception {
		SDK sdk = SDKUtil.getWorkspaceSDK();

		IPath pluginTypeFolder = sdk.getLocation().append(pluginType);

		URL projectZipUrl = Platform.getBundle("com.liferay.ide.project.core.tests").getEntry(
			"projects/" + name + ".zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		ZipUtil.unzip(projectZipFile, pluginTypeFolder.toFile());

		IPath projectFolder = pluginTypeFolder.append(name);

		Assert.assertEquals(true, projectFolder.toFile().exists());

		return projectFolder;
	}

	private boolean _isLiferayRuntimePluginClassPath(List<IClasspathEntry> entries, String entryPath) {
		boolean retval = false;

		for (Iterator<IClasspathEntry> iterator = entries.iterator(); iterator.hasNext();) {
			IClasspathEntry entry = iterator.next();

			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				for (String path : entry.getPath().segments()) {
					if (path.equals(entryPath)) {
						retval = true;

						break;
					}
				}
			}
		}

		return retval;
	}

}