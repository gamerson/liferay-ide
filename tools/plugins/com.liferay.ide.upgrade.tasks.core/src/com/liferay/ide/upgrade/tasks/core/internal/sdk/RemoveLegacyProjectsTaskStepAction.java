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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.io.File;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=remove_legacy_projects", "order=1", "stepId=remove_legacy_projects", "title=Remove Legacy Projects"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class RemoveLegacyProjectsTaskStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		List<IProject> projects = _resourceSelection.selectProjects("select projects", true);

		IProject project = projects.get(0);

		IPath projectLocation = project.getLocation();

		IPath sdkLoaction = projectLocation.append("plugins-sdk");

		for (String path : _needToDeletePaths) {
			IPath needToDeletePath = sdkLoaction.append(path);

			File file = needToDeletePath.toFile();

			if (file.exists()) {
				FileUtil.deleteDir(file, true);
			}
		}

		return Status.OK_STATUS;
	}

	private final String[] _needToDeletePaths = {"shared/portal-http-service", "webs/resources-importer-web"};

	@Reference
	private ResourceSelection _resourceSelection;

}