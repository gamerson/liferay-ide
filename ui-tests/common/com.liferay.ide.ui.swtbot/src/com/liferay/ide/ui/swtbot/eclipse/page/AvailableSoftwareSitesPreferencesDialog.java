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

package com.liferay.ide.ui.swtbot.eclipse.page;

import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.Table;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
public class AvailableSoftwareSitesPreferencesDialog extends Dialog {

	public AvailableSoftwareSitesPreferencesDialog(SWTBot bot) {
		super(bot);
	}

	public Table getSites() {
		return new Table(getShell().bot(), 0);
	}

}