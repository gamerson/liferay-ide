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

package com.liferay.ide.ui.liferay.page.editor;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.hamcrest.Matcher;

import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.Editor;
import com.liferay.ide.ui.swtbot.page.Radio;
import com.liferay.ide.ui.swtbot.page.SWTBotHyperlink;
import com.liferay.ide.ui.swtbot.page.Table;
import com.liferay.ide.ui.swtbot.page.Text;

/**
 * @author Terry Jia
 */
public class ServerEditor extends Editor {

	public ServerEditor(SWTWorkbenchBot bot, String editorName) {
		super(bot, editorName);
	}

	public Radio getCustomLaunchSettings() {
		return new Radio(getPart().bot(), "Custom Launch Settings");
	}

	public Radio getDefaultLaunchSettings() {
		return new Radio(getPart().bot(), "Default Launch Settings");
	}

	public Text getHttpPort() {
		return new Text(getPart().bot(), "Http Port:");
	}

	public CheckBox getUseDeveloperMode() {	
		return new CheckBox(getPart().bot(), "Use developer mode");
	}

	public Table getServerPortTable() {
		return new Table(getPart().bot(), 0);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public SWTBotHyperlink getHyperlink() {
		Matcher matcher = allOf(widgetOfType(Hyperlink.class), withMnemonic("Restore Default Setting"));
		return new SWTBotHyperlink((Hyperlink) bot.widget(matcher, 0), matcher);
	}
}