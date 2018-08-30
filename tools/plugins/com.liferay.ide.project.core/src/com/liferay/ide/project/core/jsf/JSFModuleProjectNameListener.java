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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class JSFModuleProjectNameListener extends FilteredListener<PropertyContentEvent> {

	public static void updateLocation(NewLiferayJSFModuleProjectOp op) {
		String currentProjectName = SapphireUtil.getContent(op.getProjectName());

		Path newLocationBase = null;

		if ((currentProjectName == null) || CoreUtil.isNullOrEmpty(currentProjectName.trim())) {
			return;
		}

		boolean useDefaultLocation = SapphireUtil.getContent(op.getUseDefaultLocation());

		if (useDefaultLocation) {
			newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
		}
		else {
			Path currentProjectLocation = SapphireUtil.getContent(op.getLocation());

			boolean hasLiferayWorkspace = false;

			if (currentProjectLocation != null) {
				hasLiferayWorkspace = LiferayWorkspaceUtil.isValidWorkspaceLocation(
					currentProjectLocation.toOSString());
			}

			if (hasLiferayWorkspace) {
				File workspaceDir = LiferayWorkspaceUtil.getWorkspaceDir(currentProjectLocation.toFile());

				if (FileUtil.notExists(workspaceDir)) {
					return;
				}

				String[] folders = LiferayWorkspaceUtil.getLiferayWorkspaceProjectWarsDirs(
					workspaceDir.getAbsolutePath());

				if (folders != null) {
					boolean appendWarFolder = false;

					IPath projectLocation = PathBridge.create(currentProjectLocation);

					for (String folder : folders) {
						if (StringUtil.endsWith(projectLocation.lastSegment(), folder)) {
							appendWarFolder = true;

							break;
						}
					}

					if (appendWarFolder) {
						newLocationBase = PathBridge.create(projectLocation);
					}
					else {
						newLocationBase = PathBridge.create(projectLocation.append(folders[0]));
					}
				}
				else {
					newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
				}
			}
		}

		if (newLocationBase != null) {
			op.setLocation(newLocationBase);
		}
	}

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		updateLocation(op(event));
	}

	protected NewLiferayJSFModuleProjectOp op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewLiferayJSFModuleProjectOp.class);
	}

}