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

package com.liferay.ide.server.core.gogo;

import com.liferay.ide.core.IBundleProject;

import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */

public class CommandFactory {

	private IBundleProject _bundleProject;

	public CommandFactory(IBundleProject project) {

		_bundleProject = project;
	}

	public BundleDeployCommand createCommand(IPath outputJar) {

		switch (_bundleProject.getBundleShape()) {
		case "war":
			return new WarDeployCommand(_bundleProject, outputJar);
		case "jar":
			return new JarDeployCommand(_bundleProject, outputJar);
		}
		return null;
	}
}
