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

package com.liferay.ide.project.ui.languageserver;

import java.util.Arrays;

import org.eclipse.lsp4e.server.ProcessOverSocketStreamConnectionProvider;
import org.eclipse.wst.server.core.util.SocketUtil;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.BladeCLIException;

/**
 * @author Terry Jia
 */
public class LanguageServerConnectionProvider extends ProcessOverSocketStreamConnectionProvider {

	public LanguageServerConnectionProvider() throws BladeCLIException {
		super(
			Arrays.asList(
				"java", "-DliferayLanguageServerPort=" + _port, "-jar", "~/.liferay-ide/liferay-properties-server-all.jar"),
			CoreUtil.getWorkspaceRootLocationString(), _port);
	}

	private static int _port = SocketUtil.findUnusedPort(10000, 60000);

}