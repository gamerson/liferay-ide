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

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.ValidationService;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LayoutTplTestsBootstrap extends LayoutTplCoreTests {

	@Test
	public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception {
		IFile refTplFile = getFileFromTplName("1_3_2_nest_changed_columns.tpl");

		String className = convertToTplClassName("1_3_2_nest_changed_columns.tpl");

		LayoutTplElement layoutTpl = createModel_132_nest(isBootstrapStyle(), className, is62());

		PortletLayoutElement row1 = (PortletLayoutElement)layoutTpl.getPortletLayouts().get(0);
		PortletLayoutElement row2 = (PortletLayoutElement)layoutTpl.getPortletLayouts().get(1);
		PortletLayoutElement row3 = (PortletLayoutElement)layoutTpl.getPortletLayouts().get(2);

		PortletColumnElement element = row3.getPortletColumns().get(0);

		PortletLayoutElement row311 = element.getPortletLayouts().get(0);

		element = row3.getPortletColumns().get(0);

		PortletLayoutElement row312 = element.getPortletLayouts().get(1);

		element = row312.getPortletColumns().get(1);

		PortletLayoutElement row31221 = element.getPortletLayouts().get(0);

		row1.getPortletColumns().remove(row1.getPortletColumns().get(0));

		layoutTpl.getPortletLayouts().remove(row1);

		PortletColumnElement insertedColumn = row311.getPortletColumns().insert();

		insertedColumn.setWeight(3);

		element = row311.getPortletColumns().get(0);

		element.setWeight(9);

		row2.getPortletColumns().remove(row2.getPortletColumns().get(0));

		element = row2.getPortletColumns().get(0);

		element.setWeight(8);

		insertedColumn = row31221.getPortletColumns().insert();

		insertedColumn.setWeight(2);

		element = row31221.getPortletColumns().get(0);

		element.setWeight(10);

		evalModelWithFile(refTplFile, layoutTpl);
	}

	@Test
	public void testPorteltColumnWeightValidationService() {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(true);

		PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column = row.getPortletColumns().insert();

		ValidationService validationService = column.getWeight().service(ValidationService.class);

		column.setWeight(0);

		Assert.assertEquals(
			"The weight value is invalid, should be in (0, 12]", validationService.validation().message());

		column.setWeight(-1);
		Assert.assertEquals(
			"The weight value is invalid, should be in (0, 12]", validationService.validation().message());

		column.setWeight(13);
		Assert.assertEquals(
			"The weight value is invalid, should be in (0, 12]", validationService.validation().message());

		column.setWeight(6);
		Assert.assertEquals("ok", validationService.validation().message());
	}

	@Test
	public void testPortletColumnFullWeightDefaultValueService() throws Exception {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(true);

		PortletLayoutElement e = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column = e.getPortletColumns().insert();

		Value<Integer> w = column.getFullWeight();

		Assert.assertEquals(12, w.content(true).intValue());
	}

	@Test
	public void testPortletColumnsValidationSerive() {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(true);

		PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column = columns.insert();

		ValidationService validationService = columns.service(ValidationService.class);

		Assert.assertEquals("ok", validationService.validation().message());

		column.setWeight(0);
		Assert.assertEquals("The sum of weight of columns should be: 12", validationService.validation().message());

		column.setWeight(-1);
		Assert.assertEquals("The sum of weight of columns should be: 12", validationService.validation().message());

		column.setWeight(6);
		Assert.assertEquals("The sum of weight of columns should be: 12", validationService.validation().message());

		column.setWeight(13);
		Assert.assertEquals("The sum of weight of columns should be: 12", validationService.validation().message());

		column.setWeight(12);
		Assert.assertEquals("ok", validationService.validation().message());
	}

	@Test
	public void testPortletColumnWeightInitialValueService() throws Exception {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(true);

		PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		columns.insert();
		columns.insert();
		columns.insert();
		columns.insert();

		Value<Integer> weight = columns.get(0).getWeight();

		Assert.assertEquals(6, weight.content().intValue());

		weight = columns.get(1).getWeight();

		Assert.assertEquals(3, weight.content().intValue());

		weight = columns.get(2).getWeight();

		Assert.assertEquals(2, weight.content().intValue());

		weight = columns.get(3).getWeight();

		Assert.assertEquals(1, weight.content().intValue());

		columns.get(0).setWeight(2);
		columns.get(1).setWeight(2);
		columns.get(2).setWeight(2);
		columns.get(3).setWeight(2);

		columns.insert();

		weight = columns.get(4).getWeight();

		Assert.assertEquals(4, weight.content().intValue());
	}

	@Test
	public void testPortletLayoutClassNameDefaultValueService() throws Exception {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(true);
		layoutTpl.setIs62(true);

		PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

		Assert.assertEquals("portlet-layout row-fluid", row.getClassName().content(true));
	}

	protected LayoutTplElement createModel_132_nest(boolean bootstrapStyle, String className, boolean is62) {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(bootstrapStyle);
		layoutTpl.setClassName(className);
		layoutTpl.setIs62(is62);

		PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column11 = row1.getPortletColumns().insert();

		column11.setWeight(12);

		PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column21 = row2.getPortletColumns().insert();

		column21.setWeight(4);

		PortletColumnElement column22 = row2.getPortletColumns().insert();

		column22.setWeight(4);

		PortletColumnElement column23 = row2.getPortletColumns().insert();

		column23.setWeight(4);

		PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column31 = row3.getPortletColumns().insert();

		column31.setWeight(8);

		PortletLayoutElement row311 = column31.getPortletLayouts().insert();

		PortletColumnElement column3111 = row311.getPortletColumns().insert();

		column3111.setWeight(12);

		PortletLayoutElement row312 = column31.getPortletLayouts().insert();

		PortletColumnElement column3121 = row312.getPortletColumns().insert();

		column3121.setWeight(6);

		PortletColumnElement column3122 = row312.getPortletColumns().insert();

		column3122.setWeight(6);

		PortletLayoutElement row31221 = column3122.getPortletLayouts().insert();

		PortletColumnElement column312211 = row31221.getPortletColumns().insert();

		column312211.setWeight(12);

		PortletColumnElement column32 = row3.getPortletColumns().insert();

		column32.setWeight(4);

		return layoutTpl;
	}

	// test sum of column weights

	@Override
	protected String getFilesPrefix() {
		return "bootstrap/files/";
	}

	@Override
	protected boolean is62() {
		return true;
	}

	@Override
	protected boolean isBootstrapStyle() {
		return true;
	}

}