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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

/**
 * @author Terry Jia
 */
public class LanguageServerConnectionProvider implements StreamConnectionProvider {

	public LanguageServerConnectionProvider() {
		List<String> commands = new ArrayList<>();

		try {
			commands.add("java");
			commands.add("-jar");

			IPath bladeCliPath = BladeCLI.getBladeCLIPath();

			commands.add(bladeCliPath.toOSString());

			commands.add("languageServer");

			IPath workingDir = CoreUtil.getWorkspaceRootLocation();

			_provider = new ProcessStreamConnectionProvider(commands, workingDir.toOSString()) {
			};
		}
		catch (BladeCLIException bclie) {
		}
	}

	@Override
	public InputStream getErrorStream() {
		return _provider.getErrorStream();
	}

	@Override
	public InputStream getInputStream() {
		return _provider.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return _provider.getOutputStream();
	}

	@Override
	public void start() throws IOException {
		_provider.start();
	}

	@Override
	public void stop() {
		_provider.stop();
	}

	private StreamConnectionProvider _provider;

}