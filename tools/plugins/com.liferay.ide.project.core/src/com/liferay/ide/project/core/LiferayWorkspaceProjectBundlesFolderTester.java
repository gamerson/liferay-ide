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

package com.liferay.ide.project.core;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */
public class LiferayWorkspaceProjectBundlesFolderTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IFolder) {
			IFolder bundlesFolder = (IFolder)receiver;

			IProject project = bundlesFolder.getProject();

			IWorkspaceProject liferayWorkspaceProject = LiferayCore.create(IWorkspaceProject.class, project);

			if (liferayWorkspaceProject == null) {
				return false;
			}

			IPath projectLocation = project.getLocation();

			String homeDir = LiferayWorkspaceUtil.getHomeDir(projectLocation.toOSString());

			if (CoreUtil.isNotNullOrEmpty(homeDir) && homeDir.equals(bundlesFolder.getName())) {
				return true;
			}

			return false;
		}

		return false;
	}

}