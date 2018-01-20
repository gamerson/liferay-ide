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

package com.liferay.ide.service.core.tests;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.service.core.operation.NewServiceBuilderDataModelProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ServiceCoreTests extends BaseTests {

	@Test
	public void testCreateBlankServiceXmlFile() throws Exception {
		_createServiceXmlFile("6.2.0", false);
	}

	@Test
	public void testCreateServiceXmlFiles() throws Exception {
		_createServiceXmlFile("6.2.0", true);
	}

	private void _createServiceXmlFile(String version, boolean useSample) throws Exception {
		IProject project = createProject("serviceXmlFiles-" + version + "-" + useSample);

		IDataModel model = DataModelFactory.createDataModel(new NewServiceBuilderDataModelProvider());

		model.setProperty(NewServiceBuilderDataModelProvider.AUTHOR, "junit");
		model.setProperty(NewServiceBuilderDataModelProvider.PACKAGE_PATH, "com.liferay.sample");
		model.setProperty(NewServiceBuilderDataModelProvider.NAMESPACE, "SAMPLE");
		model.setBooleanProperty(NewServiceBuilderDataModelProvider.USE_SAMPLE_TEMPLATE, useSample);

		IFolder folder = project.getFolder("test");

		CoreUtil.prepareFolder(folder);

		IFile serviceXmlFile = folder.getFile("service.xml");

		Assert.assertEquals(false, serviceXmlFile.exists());

		WizardUtil.createDefaultServiceBuilderFile(
			serviceXmlFile, version, model.getBooleanProperty(NewServiceBuilderDataModelProvider.USE_SAMPLE_TEMPLATE),
			model.getStringProperty(NewServiceBuilderDataModelProvider.PACKAGE_PATH),
			model.getStringProperty(NewServiceBuilderDataModelProvider.NAMESPACE),
			model.getStringProperty(NewServiceBuilderDataModelProvider.AUTHOR), new NullProgressMonitor());

		Assert.assertEquals(true, serviceXmlFile.exists());

		String serviceXmlContent = CoreUtil.readStreamToString(serviceXmlFile.getContents());

		Class<?> clazz = getClass();

		String expectedServiceXmlContent = CoreUtil.readStreamToString(
			clazz.getResourceAsStream("files/service-sample-" + version + "-" + useSample + ".xml"));

		Assert.assertEquals(stripCarriageReturns(expectedServiceXmlContent), stripCarriageReturns(serviceXmlContent));
	}

}