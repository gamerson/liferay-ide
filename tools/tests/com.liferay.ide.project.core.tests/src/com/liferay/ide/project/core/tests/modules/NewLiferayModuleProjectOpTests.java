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

package com.liferay.ide.project.core.tests.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.tests.util.SapphireUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NewLiferayModuleProjectOpTests {

	@Ignore
	@Test
	public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my-test-project");

		op.setProjectTemplateName("portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));
	}

	@Ignore
	@Test
	public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my.test.project");

		op.setProjectTemplateName("portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));
	}

	@Ignore
	@Test
	public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my.test.project");

		op.setProjectTemplateName("portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectName("my_abc-test");

		Assert.assertEquals("MyAbcTest", op.getComponentName().content(true));
	}

	@Ignore
	@Test
	public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectTemplateName() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my.test.project");

		op.setProjectTemplateName("activator");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectTemplateName("portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectTemplateName("mvc-portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectTemplateName("service");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectTemplateName("service-wrapper");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));

		op.setProjectTemplateName("service-builder");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));
	}

	@Ignore
	@Test
	public void testNewLiferayModuleProjectDefaultValueServiceUnderscores() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my_test_project");

		op.setProjectTemplateName("portlet");

		Assert.assertEquals("MyTestProject", op.getComponentName().content(true));
	}

	@Test
	public void testNewLiferayModuleProjectNameValidataionService() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my-test-project");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("1");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("a");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("A");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("my-test-project-");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("my-test-project.");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));

		op.setProjectName("my-test-project_");

		Assert.assertTrue(SapphireUtil.ok(op.getProjectName()));
	}

	@Test
	public void testNewLiferayModuleProjectNewProperties() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("test-properties-in-portlet");

		op.setProjectTemplateName("portlet");
		op.setComponentName("Test");

		PropertyKey pk = op.getPropertyKeys().insert();

		pk.setName("property-test-key");
		pk.setValue("property-test-value");

		Assert.assertEquals(
			"OK",
			SapphireUtil.message(NewLiferayModuleProjectOpMethods.execute(op, SapphireUtil.getNullProgressMonitor())));

		IProject modPorject = CoreUtil.getProject(op.getProjectName().content());

		modPorject.open(new NullProgressMonitor());

		SearchFilesVisitor sv = new SearchFilesVisitor();

		List<IFile> searchFiles = sv.searchFiles(modPorject, "TestPortlet.java");

		IFile componentClassFile = searchFiles.get(0);

		Assert.assertEquals(componentClassFile.exists(), true);

		String actual = CoreUtil.readStreamToString(componentClassFile.getContents());

		Assert.assertTrue(actual, actual.contains("\"property-test-key=property-test-value\""));
	}

	@Ignore
	@Test
	public void testNewLiferayModuleProjectPackageDefaultValueService() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my-test-project");

		op.setProjectTemplateName("Portlet");

		Assert.assertEquals("my.test.project", op.getPackageName().content(true));

		op.setProjectName("my.test.foo");

		Assert.assertEquals("my.test.foo", op.getPackageName().content(true));

		op.setProjectName("my_test_foo1");

		op.setProjectTemplateName("ServiceWrapper");

		Assert.assertEquals("my.test.foo1", op.getPackageName().content(true));
	}

	@Test
	public void testNewLiferayPortletProviderNewProperties() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("test-properties-in-portlet-provider");
		op.setComponentName("Test");
		op.setProjectTemplateName("portlet-provider");

		PropertyKey pk = op.getPropertyKeys().insert();

		pk.setName("property-test-key");
		pk.setValue("property-test-value");

		Status status = NewLiferayModuleProjectOpMethods.execute(op, SapphireUtil.getNullProgressMonitor());

		Assert.assertTrue(status.message(), status.ok());

		IProject modPorject = CoreUtil.getProject(op.getProjectName().content());

		modPorject.open(new NullProgressMonitor());

		IFile testAddPortletProvider = modPorject.getFile(
			"src/main/java/test/properties/in/portlet/provider/portlet/TestAddPortletProvider.java");

		Assert.assertTrue(testAddPortletProvider.exists());

		SearchFilesVisitor sv = new SearchFilesVisitor();

		List<IFile> searchFiles = sv.searchFiles(modPorject, "TestAddPortletProvider.java");

		IFile componentClassFile = searchFiles.get(0);

		Assert.assertEquals(componentClassFile.exists(), true);

		String actual = CoreUtil.readStreamToString(componentClassFile.getContents());

		Assert.assertTrue(actual.contains("property-test-key=property-test-value"));
	}

}