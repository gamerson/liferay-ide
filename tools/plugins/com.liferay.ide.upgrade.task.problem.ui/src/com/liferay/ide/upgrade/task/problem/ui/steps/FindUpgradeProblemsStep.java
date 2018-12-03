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

package com.liferay.ide.upgrade.task.problem.ui.steps;

import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.JavaProjectsUpgradeTaskStep;
import com.liferay.ide.upgrade.task.problem.ui.MigrateProjectHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/FindUpgradeProblemsStep.properties", service = UpgradeTaskStep.class)
public class FindUpgradeProblemsStep extends JavaProjectsUpgradeTaskStep {

	public IStatus execute(IProject[] projects, IProgressMonitor progressMonitor) {

		// TODO need to run finding upgrade changes by Upgrade Plan

		IPath[] paths = Stream.of(
			projects
		).map(
			project -> project.getLocation()
		).collect(
			Collectors.toList()
		).toArray(
			new IPath[0]
		);

		String[] projectNames = Stream.of(
			projects
		).map(
			project -> project.getName()
		).collect(
			Collectors.toList()
		).toArray(
			new String[0]
		);

		MigrateProjectHandler.findMigrationProblems(paths, projectNames, "7.1");

		return Status.OK_STATUS;
	}

}