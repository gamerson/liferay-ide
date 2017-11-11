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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class FragmentProjectNameListener extends FilteredListener<PropertyContentEvent> {

	public static void updateLocation(NewModuleFragmentOp op) {
		String currentProjectName = op.getProjectName().content(true);

		if ((currentProjectName == null) || CoreUtil.isNullOrEmpty(currentProjectName.trim())) {
			return;
		}

		boolean useDefaultLocation = op.getUseDefaultLocation().content(true);

		if (useDefaultLocation) {
			Path newLocationBase = null;

			boolean hasLiferayWorkspace = false;
			boolean hasGradleWorkspace = false;
			boolean hasMavenWorkspace = false;

			try {
				hasLiferayWorkspace = LiferayWorkspaceUtil.hasWorkspace();
				hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
				hasMavenWorkspace = LiferayWorkspaceUtil.hasMavenWorkspace();
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to check LiferayWorkspace project.");
			}

			if (!hasLiferayWorkspace) {
				newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRoot().getLocation());
			}
			else {
				boolean gradleModule = false;
				boolean mavenModule = false;

				ILiferayProjectProvider iProvider = op.getProjectProvider().content();

				if (iProvider != null) {
					String shortName = iProvider.getShortName();

					if (!CoreUtil.empty(shortName) && shortName.startsWith("gradle")) {
						gradleModule = true;
					}
					else {
						mavenModule = true;
					}
				}

				if ((gradleModule && hasGradleWorkspace) || (mavenModule && hasMavenWorkspace)) {
					IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

					if (FileUtil.exists(liferayWorkspaceProject)) {
						String folder = LiferayWorkspaceUtil.getModulesDir(liferayWorkspaceProject);

						if (folder != null) {
							IPath appendPath = liferayWorkspaceProject.getLocation().append(folder);

							newLocationBase = PathBridge.create(appendPath);
						}
					}
				}
				else {
					newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRoot().getLocation());
				}
			}

			if (newLocationBase != null) {
				op.setLocation(newLocationBase);
			}
		}
	}

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		updateLocation(op(event));
	}

	protected NewModuleFragmentOp op(PropertyContentEvent event) {
		Element element = event.property().element();

		return element.nearest(NewModuleFragmentOp.class);
	}

}