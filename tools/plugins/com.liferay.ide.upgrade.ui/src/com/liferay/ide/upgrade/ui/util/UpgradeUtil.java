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

package com.liferay.ide.upgrade.ui.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class UpgradeUtil {

	public static boolean isAlreadyImported(IPath path) {
		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IContainer[] containers = workspaceRoot.findContainersForLocationURI(FileUtil.toURI(path));

		long projectCount = Stream.of(
			containers
		).filter(
			container -> container instanceof IProject
		).count();

		if (projectCount > 0) {
			return true;
		}

		return false;
	}

}