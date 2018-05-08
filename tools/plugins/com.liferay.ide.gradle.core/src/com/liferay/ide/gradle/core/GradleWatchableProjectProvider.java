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

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayWorkspaceNature;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class GradleWatchableProjectProvider extends AbstractLiferayProjectProvider {

	public GradleWatchableProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		if (!(adaptable instanceof IProject)) {
			return null;
		}

		IProject project = (IProject)adaptable;

		if (!GradleProjectNature.isPresentOn(project)) {
			return null;
		}

		IFile buildFile = project.getFile("build.gradle");

		boolean inLiferayWorkspace = LiferayWorkspaceUtil.inLiferayWorkspace(project);

		if (inLiferayWorkspace || LiferayWorkspaceUtil.isValidWorkspace(project)) {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			buildFile = workspaceProject.getFile("settings.gradle");
		}

		boolean watchable = GradleUtil.isWatchable(buildFile);

		ILiferayProject retval = null;

		if (watchable) {
			if (LiferayWorkspaceNature.hasNature(project)) {
				retval = new WatchableLiferayWorkspaceProject(project);
			}
			else {
				if (!inLiferayWorkspace) {
					retval = new LiferayGradleWatchableProject(project);
				}
			}
		}

		return retval;
	}

	public int getPriority() {
		return 20;
	}

}
