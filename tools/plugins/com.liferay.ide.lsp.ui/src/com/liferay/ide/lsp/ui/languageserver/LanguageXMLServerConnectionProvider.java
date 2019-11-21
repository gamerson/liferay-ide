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

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;

import org.osgi.framework.Bundle;

/**
 * @author Seiphon Wang
 */
public class LanguageXMLServerConnectionProvider extends ProcessStreamConnectionProvider {

	public static final String LIB_FOLDER_NAME = "lib/";

	public LanguageXMLServerConnectionProvider() {
		super(_computeCommands(), _computeWorkingDir());
	}

	private static String _computeCamelLanguageServerJarPath() {
		String languageServerJarPath = "";

		LiferayLSPUIPlugin lspUI = LiferayLSPUIPlugin.getDefault();

		Bundle bundle = lspUI.getBundle();

		try {
			URL url = FileLocator.resolve(bundle.getEntry(LIB_FOLDER_NAME + "org.eclipse.lsp4xml-uber.jar"));

			File file = new File(url.toURI());

			if (Platform.OS_WIN32.equals(Platform.getOS())) {
				languageServerJarPath = "\"" + file.getAbsolutePath() + "\"";
			}
			else {
				languageServerJarPath = file.getAbsolutePath();
			}
		}
		catch (IOException | URISyntaxException e) {
		}

		return languageServerJarPath;
	}

	private static List<String> _computeCommands() {
		List<String> commands = new ArrayList<>();

		commands.add("java");

		commands.add("-classpath");

		commands.add(
			_computeCamelLanguageServerJarPath() + ";" + _computeExtentionPluginJarPath() + ";" +
				_computeExtentionServiceJarPath());

		commands.add("org.eclipse.lsp4xml.XMLServerLauncher");

		if (_isDebugEnabled()) {
			commands.addAll(_debugArguments());
		}

		return commands;
	}

	private static String _computeExtentionPluginJarPath() {
		String extentionPluginJarPath = "";

		LiferayLSPUIPlugin lspUI = LiferayLSPUIPlugin.getDefault();

		Bundle bundle = lspUI.getBundle();

		try {
			URL url = FileLocator.resolve(bundle.getEntry(LIB_FOLDER_NAME + "liferay-xmls-server-all.jar"));

			File file = new File(url.toURI());

			if (Platform.OS_WIN32.equals(Platform.getOS())) {
				extentionPluginJarPath = "\"" + file.getAbsolutePath() + "\"";
			}
			else {
				extentionPluginJarPath = file.getAbsolutePath();
			}
		}
		catch (IOException | URISyntaxException e) {
		}

		return extentionPluginJarPath;
	}

	private static String _computeExtentionServiceJarPath() {
		String extentionPluginJarPath = "";

		LiferayLSPUIPlugin lspUI = LiferayLSPUIPlugin.getDefault();

		Bundle bundle = lspUI.getBundle();

		try {
			URL url = FileLocator.resolve(bundle.getEntry(LIB_FOLDER_NAME + "services-0.1-SNAPSHOT.jar"));

			File file = new File(url.toURI());

			if (Platform.OS_WIN32.equals(Platform.getOS())) {
				extentionPluginJarPath = "\"" + file.getAbsolutePath() + "\"";
			}
			else {
				extentionPluginJarPath = file.getAbsolutePath();
			}
		}
		catch (IOException | URISyntaxException e) {
		}

		return extentionPluginJarPath;
	}

	private static String _computeWorkingDir() {
		return System.getProperty("user.dir");
	}

	private static List<String> _debugArguments() {
		return Arrays.asList("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5000");
	}

	private static boolean _isDebugEnabled() {
		return Boolean.parseBoolean(System.getProperty(_DEBUG_FLAG, "true"));
	}

	private static final String _DEBUG_FLAG = "debugLSPServer";

}