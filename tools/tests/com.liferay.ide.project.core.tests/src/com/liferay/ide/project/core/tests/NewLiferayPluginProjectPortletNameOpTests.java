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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewLiferayPluginProjectPortletNameOpTests extends ProjectCoreBase {

	@AfterClass
	public static void removePluginsSDK() throws CoreException {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if ((project != null) && project.isAccessible() && project.exists()) {
				try {
					project.close(new NullProgressMonitor());
					project.delete(true, new NullProgressMonitor());
				}
				catch (ResourceException re) {
					project.close(new NullProgressMonitor());
					project.delete(true, new NullProgressMonitor());
				}
			}
		}
	}

	@Test
	public void testNewJSFPortletProjectPortletName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String jsfSuite = "jsf-2.x";
		String suffix = "";
		String customPortletName = "test111";

		IProject jsfProject = createNewJSFPortletProjectCustomPortletName(jsfSuite, suffix, customPortletName);

		IFolder defaultDocroot = LiferayCore.create(IWebProject.class, jsfProject).getDefaultDocrootFolder();

		IFile portletXml = defaultDocroot.getFile("WEB-INF/portlet.xml");

		Assert.assertEquals(true, portletXml.exists());

		String portletXmlContent = CoreUtil.readStreamToString(portletXml.getContents());

		Assert.assertEquals(true, portletXmlContent.contains(customPortletName));

		IFile liferayPortletXml = defaultDocroot.getFile("WEB-INF/liferay-portlet.xml");

		Assert.assertEquals(true, liferayPortletXml.exists());

		String liferayPortletXmlContent = CoreUtil.readStreamToString(liferayPortletXml.getContents());

		Assert.assertEquals(true, liferayPortletXmlContent.contains(customPortletName));

		IFile liferayDisplayXml = defaultDocroot.getFile("WEB-INF/liferay-display.xml");

		Assert.assertEquals(true, liferayDisplayXml.exists());

		String liferayDisplayXmlContent = CoreUtil.readStreamToString(liferayDisplayXml.getContents());

		Assert.assertEquals(true, liferayDisplayXmlContent.contains(customPortletName));
	}

	@Test
	public void testNewMVCPortletProjectCustomPortletName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String customPortletName = "test111";

		IProject jsfProject = createNewMVCPortletProjectCustomPortletName(customPortletName);

		IFolder docroot = LiferayCore.create(IWebProject.class, jsfProject).getDefaultDocrootFolder();

		IFile portletXml = docroot.getFile("WEB-INF/portlet.xml");

		Assert.assertEquals(true, portletXml.exists());

		String portletXmlContent = CoreUtil.readStreamToString(portletXml.getContents());

		Assert.assertEquals(true, portletXmlContent.contains(customPortletName));

		IFile liferayPortletXml = docroot.getFile("WEB-INF/liferay-portlet.xml");

		Assert.assertEquals(true, liferayPortletXml.exists());

		String liferayPortletXmlContent = CoreUtil.readStreamToString(liferayPortletXml.getContents());

		Assert.assertEquals(true, liferayPortletXmlContent.contains(customPortletName));

		IFile liferayDisplayXml = docroot.getFile("WEB-INF/liferay-display.xml");

		Assert.assertEquals(true, liferayDisplayXml.exists());

		String liferayDisplayXmlContent = CoreUtil.readStreamToString(liferayDisplayXml.getContents());

		Assert.assertEquals(true, liferayDisplayXmlContent.contains(customPortletName));
	}

	protected IProject createNewJSFPortletProjectCustomPortletName(
			String jsfSuite, String suffix, String customPortletName)
		throws Exception {

		String projectName = "test-" + jsfSuite + suffix + "-sdk-project";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setIncludeSampleCode(true);
		op.setPortletFramework("jsf-2.x");
		op.setPortletFrameworkAdvanced("liferay_faces_alloy");
		op.setPortletName(customPortletName);

		IProject jsfProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, jsfProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile config = webappRoot.getFile("WEB-INF/faces-config.xml");

		Assert.assertEquals(true, config.exists());

		return jsfProject;
	}

	protected IProject createNewMVCPortletProjectCustomPortletName(String customPortletName) throws Exception {
		String projectName = "test-mvc-sdk-project";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setIncludeSampleCode(true);
		op.setPortletFramework("mvc");
		op.setPortletName(customPortletName);

		IProject mvcProject = createAntProject(op);

		Assert.assertNotNull(LiferayCore.create(IWebProject.class, mvcProject).getDefaultDocrootFolder());

		return mvcProject;
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

}