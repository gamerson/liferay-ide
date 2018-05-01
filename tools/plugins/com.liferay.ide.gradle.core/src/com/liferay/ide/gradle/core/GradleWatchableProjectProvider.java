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

import java.io.IOException;
import java.util.List;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Version;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayWorkspaceNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.ProjectCore;
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

		boolean watchable = false;

		IFile buildFile = project.getFile("build.gradle");

		boolean inLiferayWorkspace = LiferayWorkspaceUtil.inLiferayWorkspace(project);

		ScopedPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), ProjectCore.PLUGIN_ID);

		if (inLiferayWorkspace || LiferayWorkspaceUtil.isValidWorkspace(project)) {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			buildFile = workspaceProject.getFile("settings.gradle");

			store = new ScopedPreferenceStore(new ProjectScope(workspaceProject), ProjectCore.PLUGIN_ID);
		}

		try {
			if (FileUtil.exists(buildFile)) {
				GradleDependencyUpdater updater = new GradleDependencyUpdater(buildFile);

				List<GradleDependency> dependencies = updater.getAllBuildDependencies();

				for (GradleDependency dependency : dependencies) {
					String group = dependency.getGroup();
					String name = dependency.getName();
					Version version = new Version("0");
					String dependencyVersion = dependency.getVersion();

					if (dependencyVersion != null && !dependencyVersion.equals("") ) { 
						version = new Version(dependencyVersion);
					}

					if ("com.liferay".equals(group) && "com.liferay.gradle.plugins".equals(name)
							&& CoreUtil.compareVersions(version, new Version("3.11.0")) >= 0) {
						watchable = true;

						break;
					}

					if ("com.liferay".equals(group) && "com.liferay.gradle.plugins.workspace".equals(name)
							&& CoreUtil.compareVersions(version, new Version("1.9.2")) >= 0) {
						watchable = true;

						break;
					}
				}
			}
		}
		catch (MultipleCompilationErrorsException e) {
		}
		catch (IOException e) {
		}

		ILiferayProject retval = null;

		_store(store, "watchable", watchable);

		if (watchable) {
			boolean contains = store.contains("enableWatch");

			if (LiferayWorkspaceNature.hasNature(project)) {
				retval = new WatchableLiferayWorkspaceProject(project);
			}
			else {
				if (!inLiferayWorkspace) {
					retval = new LiferayGradleWatchableProject(project);
				}
			}

			if (contains) {
				boolean enableWatch = store.getBoolean("enableWatch");

				if (!enableWatch) {
					retval = null;
				}
			}
		}
		else {
			_store(store, "enableWatch", false);
		}

		return retval;
	}

	public int getPriority() {
		return 20;
	}

	private void _store(ScopedPreferenceStore store, String key, boolean value) {
		store.setValue(key, Boolean.toString(value));

		try {
			store.save();
		}
		catch (Exception e) {
		}
	}

}
