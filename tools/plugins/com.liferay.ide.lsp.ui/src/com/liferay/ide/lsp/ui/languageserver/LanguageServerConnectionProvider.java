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

package com.liferay.ide.lsp.ui.languageserver;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.BladeCLIException;

import java.io.File;

import java.util.Arrays;
import java.util.Properties;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;

/**
 * @author Terry Jia
 */
public class LanguageServerConnectionProvider extends ProcessStreamConnectionProvider {

	public LanguageServerConnectionProvider() throws BladeCLIException {
		super(
			Arrays.asList("java", "-DliferayLanguageServerStandardIO=true", "-jar", _getLiferayPropertiesServerPath()),
			CoreUtil.getWorkspaceRootLocationString());
	}

	private static String _getLiferayPropertiesServerPath() {
		Properties properties = System.getProperties();

		File temp = new File(properties.getProperty("user.home"), ".liferay-ide");

		File liferayPropertiesServerJar = new File(temp, "liferay-properties-server-all.jar");

		if (liferayPropertiesServerJar.exists()) {
			return liferayPropertiesServerJar.getAbsolutePath();
		}

		return null;
	}

}