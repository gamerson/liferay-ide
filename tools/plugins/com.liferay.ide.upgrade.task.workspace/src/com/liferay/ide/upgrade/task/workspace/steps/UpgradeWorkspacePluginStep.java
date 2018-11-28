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

package com.liferay.ide.upgrade.task.workspace.steps;

import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.WorkspaceUpgradeTaskStep;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/UpgradeWorkspacePluginStep.properties", service = UpgradeTaskStep.class)
public class UpgradeWorkspacePluginStep extends WorkspaceUpgradeTaskStep {

	@Override
	protected void execute(IProject project, IProgressMonitor progressMonitor) {

		// TODO

	}

}