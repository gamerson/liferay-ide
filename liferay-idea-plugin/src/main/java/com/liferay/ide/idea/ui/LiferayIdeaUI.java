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

package com.liferay.ide.idea.ui;

import com.intellij.openapi.util.IconLoader;

import java.io.File;

import javax.swing.Icon;

/**
 * @author Gregory Amerson
 */
public class LiferayIdeaUI {

	public static final Icon LIFERAY_ICON = IconLoader.getIcon("/icons/liferay.png");

	public static final File USER_BUNDLES_DIR = new File(
		new File(System.getProperty("user.home"), ".liferay-ide"), "bundles");

	static {
		USER_BUNDLES_DIR.mkdirs();
	}

}