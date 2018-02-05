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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.swtbot.page.AbstractWidget;
import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.ComboBox;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class ValidationAction extends UIAction {

	public static ValidationAction getInstance(SWTWorkbenchBot bot) {
		if (_validationAction == null) {
			_validationAction = new ValidationAction(bot);
		}

		return _validationAction;
	}

	private ValidationAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void assertCheckedTrue(CheckBox checkBox) {
		Assert.assertTrue(checkBox.isChecked());
	}

	public void assertEquals(String[] expects, ComboBox comboBox) {
		String[] items = comboBox.items();

		Assert.assertEquals(expects.length, items.length);

		for (int i = 0; i < items.length; i++) {
			Assert.assertEquals(expects[i], items[i]);
		}
	}

	public void assertEquals(String expect, String actual) {
		Assert.assertEquals(expect, actual);
	}

	public void assertEquals(String expect, AbstractWidget widget) {
		String actual = widget.getText();

		Assert.assertEquals(expect, actual);
	}

	public void assertEnabledTrue(AbstractWidget widget) {
		Assert.assertTrue(widget.isEnabled());
	}

	public void assertEnabledFalse(AbstractWidget widget) {
		Assert.assertFalse(widget.isEnabled());
	}

	private static ValidationAction _validationAction;

}