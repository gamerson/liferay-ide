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

package com.liferay.ide.project.core.samples.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.samples.NewSampleOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Terry Jia
 */
public class SampleProjectNameListener
	extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateLocation(op(event));
	}

	protected NewSampleOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewSampleOp.class);
	}

	private void _updateLocation(NewSampleOp op) {
		String currentProjectName = get(op.getProjectName());

		if (CoreUtil.isNullOrEmpty(currentProjectName)) {
			return;
		}

		boolean useDefaultLocation = get(op.getUseDefaultLocation());

		if (useDefaultLocation) {
			Path newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());

			boolean hasGradleWorkspace = false;

			try {
				hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to check LiferayWorkspace project.");
			}

			if (hasGradleWorkspace) {
				IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

				IPath workspaceLocation = liferayWorkspaceProject.getLocation();

				String category = get(op.getCategory());

				if (category == null) {
					category = "";
				}

				newLocationBase = PathBridge.create(workspaceLocation.append(category));
			}

			if (newLocationBase != null) {
				op.setLocation(newLocationBase);
			}
		}
	}

}