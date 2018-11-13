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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.migration.MigrateProjectHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(property = {
	"task.priority=0", "task.title=Find Upgrade Problems", "task.description=Find Upgrade Problems Description"
},
	service = UpgradeTask.class)
public class FindUpgradeProblemsTask implements UpgradeTask {

	@Override
	public void execute(IProgressMonitor progressMonitor) {
		MigrateProjectHandler migrateHandler = new MigrateProjectHandler();

		//TODO need to do more flexible

		IProject[] projects = CoreUtil.getAllProjects();

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

		migrateHandler.findMigrationProblems(paths, projectNames, "7.1");
	}

}