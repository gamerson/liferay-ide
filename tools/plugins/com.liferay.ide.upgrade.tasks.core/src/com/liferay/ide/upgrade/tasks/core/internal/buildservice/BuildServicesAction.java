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

package com.liferay.ide.upgrade.tasks.core.internal.buildservice;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionPerformedEvent;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;
import com.liferay.ide.upgrade.tasks.core.buildservice.BuildServicesActionKeys;
import com.liferay.ide.upgrade.tasks.core.buildservice.RebuildServicesStepKeys;
import com.liferay.ide.upgrade.tasks.core.internal.UpgradeTasksCorePlugin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@Component(
	property = {
		"description=" + BuildServicesActionKeys.DESCRIPTION, "id=build_services", "order=2",
		"stepId=" + RebuildServicesStepKeys.ID, "title=" + BuildServicesActionKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class BuildServicesAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Service Builder Projects", false, ResourceSelection.SERVICE_BUILDER_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		Stream<IProject> stream = projects.stream();

		stream.map(
			p -> LiferayCore.create(ILiferayProject.class, p)
		).filter(
			Objects::nonNull
		).map(
			liferayProject -> liferayProject.adapt(IProjectBuilder.class)
		).forEach(
			projectBuilder -> {
				try {
					projectBuilder.buildService(progressMonitor);
				}
				catch (CoreException ce) {
					UpgradeTasksCorePlugin.logError("Error building service", ce);
				}
			}
		);

		_upgradePlanner.dispatch(new UpgradeTaskStepActionPerformedEvent(this, projects));

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}