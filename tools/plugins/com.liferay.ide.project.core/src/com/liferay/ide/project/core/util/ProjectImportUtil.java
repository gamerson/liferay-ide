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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ProjectImportUtil {

	public static String getConfigFileLocation(String configFile) {
		StringBuilder sb = new StringBuilder("WEB-INF/");

		sb.append(configFile);

		return sb.toString();
	}

	public static IStatus validatePath(String currentPath) {
		if (!Path.EMPTY.isValidPath(currentPath)) {
			return ProjectCore.createErrorStatus("\"" + currentPath + "\" is not a valid path.");
		}

		IPath osPath = Path.fromOSString(currentPath);

		File file = osPath.toFile();

		if (!file.isAbsolute()) {
			return ProjectCore.createErrorStatus("\"" + currentPath + "\" is not an absolute path.");
		}

		if (FileUtil.notExists(osPath)) {
			return ProjectCore.createErrorStatus("Directory does not exist.");
		}

		return Status.OK_STATUS;
	}

}