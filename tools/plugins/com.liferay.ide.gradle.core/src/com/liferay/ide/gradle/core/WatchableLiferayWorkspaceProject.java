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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.IWatchableProject;

import org.eclipse.core.resources.IProject;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class WatchableLiferayWorkspaceProject extends LiferayWorkspaceProject implements IWatchableProject {

	public WatchableLiferayWorkspaceProject(IProject project) {
		super(project);
	}

	@Override
	public void watch() {
	}

	@Override
	public void unwatch() {
	}


}