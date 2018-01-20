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

package com.liferay.ide.layouttpl.core.tests;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplElementsFactory;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public abstract class LayoutTplCoreTests extends BaseTests {

	@Before
	public void createTestProject() throws Exception {
		deleteProject("a");

		_project = createProject("a");
	}

	@Test
	public void evalTemplateFromFile_0_columns() throws Exception {
		IFile refTplFile = getFileFromTplName("0_columns.tpl");

		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setClassName(convertToTplClassName("0_columns.tpl"));
		layoutTpl.setBootstrapStyle(isBootstrapStyle());
		layoutTpl.setIs62(is62());

		evalModelWithFile(refTplFile, layoutTpl);
	}

	@Test
	public void evalTemplateFromFile_1_2_1_columns() throws Exception {
		evalTemplateFromFile("1_2_1_columns.tpl");
	}

	@Test
	public void evalTemplateFromFile_1_3_1_columns() throws Exception {
		evalTemplateFromFile("1_3_1_columns.tpl");
	}

	@Test
	public void evalTemplateFromFile_1_3_2_columns() throws Exception {
		evalTemplateFromFile("1_3_2_columns.tpl");
	}

	@Test
	public void evalTemplateFromFile_1_3_2_nest_columns() throws Exception {
		evalTemplateFromFile("1_3_2_nest_columns.tpl");
	}

	@Test
	public void evalTemplateFromFile_2_1_2_columns() throws Exception {
		evalTemplateFromFile("2_1_2_columns.tpl");
	}

	@Test
	public void evalTemplateFromFile_3_2_3_columns() throws Exception {
		evalTemplateFromFile("3_2_3_columns.tpl");
	}

	@Test
	public void evalTemplateFromModel_1_3_2_nest_columns() throws Exception {
		IFile refTplFile = getFileFromTplName("1_3_2_nest_columns.tpl");

		String className = convertToTplClassName("1_3_2_nest_columns.tpl");

		evalModelWithFile(refTplFile, createModel_132_nest(isBootstrapStyle(), className, is62()));
	}

	/**
	 * convert template file name to layout template class name
	 */
	protected String convertToTplClassName(String tplFileName) {

		// assume file name is "n_n_n_columns.*" and want "columns-n-n-n"

		return "columns-" + tplFileName.replaceAll("_columns\\..*", "").replaceAll("_", "-");
	}

	protected abstract LayoutTplElement createModel_132_nest(boolean bootstrapStyle, String className, boolean is62);

	protected void evalModelWithFile(IFile refTplFile, LayoutTplElement layoutTpl) {
		Assert.assertEquals(true, layoutTpl != null);

		String templateSource = LayoutTplUtil.getTemplateSource(layoutTpl);

		Assert.assertEquals(false, templateSource.isEmpty());

		String inputString = FileUtil.readContents(refTplFile.getLocation().toFile(), true).trim();

		inputString = inputString.replaceAll("\r", "").replaceAll("\\s", "");

		templateSource = templateSource.replaceAll("\r", "").replaceAll("\\s", "");

		Assert.assertEquals(true, inputString.equals(templateSource));
	}

	protected void evalTemplateFromFile(String tplName) throws Exception {
		IFile tplFile = getFileFromTplName(tplName);

		LayoutTplElement layoutTpl = LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile(
			tplFile, isBootstrapStyle(), is62());

		evalModelWithFile(tplFile, layoutTpl);
	}

	protected IFile getFileFromTplName(String tplName) throws Exception {
		Class<?> clazz = getClass();

		IFile templateFile = createFile(
			_project, getFilesPrefix() + tplName, clazz.getResourceAsStream(getFilesPrefix() + tplName));

		Assert.assertEquals(templateFile.getFullPath().lastSegment(), tplName);

		Assert.assertEquals(true, templateFile.exists());

		return templateFile;
	}

	protected abstract String getFilesPrefix();

	protected abstract boolean is62();

	protected abstract boolean isBootstrapStyle();

	private IProject _project;

}