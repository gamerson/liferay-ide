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
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 */
public class LanguageServerConnectionProvider extends ProcessStreamConnectionProvider {

	public static final String PROPERTIES_LSP_JAR_FILE_NAME = "liferay-properties-server-all.jar";

	public LanguageServerConnectionProvider() {
		super(
			Arrays.asList(
				_computeJavaPath(), "-DliferayLanguageServerStandardIO=true", "-jar",
				_getLiferayPropertiesServerPath()),
			CoreUtil.getWorkspaceRootLocationString());
	}

	private static String _computeJavaPath() {
		String javaPath = "java";

		String paths = System.getenv("PATH");

		boolean existsInPath = Stream.of(
			paths.split(Pattern.quote(File.pathSeparator))
		).map(
			Paths::get
		).anyMatch(
			path -> Files.exists(path.resolve("java"))
		);

		if (!existsInPath) {
			String javaHome = System.getProperty("java.home");

			File file = new File(javaHome, "bin/java" + (CoreUtil.isWindows() ? ".exe" : ""));

			javaPath = file.getAbsolutePath();
		}

		return javaPath;
	}

	private static String _getLiferayPropertiesServerPath() {
		LiferayLSPUIPlugin lspUI = LiferayLSPUIPlugin.getDefault();

		Bundle bundle = lspUI.getBundle();

		File bladeJarBundleFile;

		try {
			bladeJarBundleFile = FileUtil.getFile(
				FileLocator.toFileURL(bundle.getEntry("lib/" + PROPERTIES_LSP_JAR_FILE_NAME)));

			Path path = new Path(bladeJarBundleFile.getCanonicalPath());

			return path.toOSString();
		}
		catch (IOException ioe) {
		}

		return "";
	}

}