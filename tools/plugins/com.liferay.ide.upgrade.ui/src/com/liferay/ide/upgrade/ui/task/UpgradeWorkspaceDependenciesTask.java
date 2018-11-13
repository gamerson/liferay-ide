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

package com.liferay.ide.upgrade.ui.task;

import org.eclipse.core.runtime.IProgressMonitor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(property = {
	"task.priority=10", "task.title=Upgrade Workspace Dependencies",
	"task.description=Upgrade Workspace Dependencies Description"
},
	service = UpgradeTask.class)
public class UpgradeWorkspaceDependenciesTask implements UpgradeTask {

	@Override
	public void execute(IProgressMonitor progressMonitor) {

		// TODO

	}

}