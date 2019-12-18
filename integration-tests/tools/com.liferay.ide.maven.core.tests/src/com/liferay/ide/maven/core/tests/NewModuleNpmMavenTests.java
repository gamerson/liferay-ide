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

import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewModuleNpmMavenTests extends NewModuleMavenBase {

	@Test
	public void createNpmAngularPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setLiferayVersion("7.2");

		op.setProjectTemplateName("npm-angular-portlet");

		Status validation = op.validation();

		Assert.assertEquals("Can only create npm portlet for 7.1", _errorMessage, validation.message());
	}

	@Test
	public void createNpmReactPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setLiferayVersion("7.2");

		op.setProjectTemplateName("npm-react-portlet");

		Status validation = op.validation();

		Assert.assertEquals("Can only create npm portlet for 7.1", _errorMessage, validation.message());
	}

	@Test
	public void createNpmVuejsPortletForPortal72() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setLiferayVersion("7.2");

		op.setProjectTemplateName("npm-vuejs-portlet");

		Status validation = op.validation();

		Assert.assertEquals("Can only create npm portlet for 7.1", _errorMessage, validation.message());
	}

	@Override
	protected String shape() {
		return "jar";
	}

	private static String _errorMessage = new String(
		"NPM portlet project templates generated from this tool are not supported for specified Liferay version. See " +
			"LPS-97950 for full details.");

}