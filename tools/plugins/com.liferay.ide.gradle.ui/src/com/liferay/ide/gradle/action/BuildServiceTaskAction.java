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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.eclipse.EclipseProject;

/**
 * @author Lovett Li
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class BuildServiceTaskAction extends GradleTaskAction {

	public BuildServiceTaskAction() {
		_serviceBuildProjects = new ArrayList<>();
	}

	public boolean getServiceParentProject(IProject checkProject) {
		if (checkProject != null) {
			List<IFile> serviceXmlFiles = new SearchFilesVisitor().searchFiles(checkProject, "service.xml");

			if (serviceXmlFiles.size() == 1) {
				IPath servicePath = serviceXmlFiles.get(0).getFullPath();

				IPath serviceProjectLocation = servicePath.removeLastSegments(1);
				IProject serviceProject = CoreUtil.getProject(servicePath.segment(servicePath.segmentCount() - 2));

				if ((servicePath.segmentCount() == 2) && checkProject.equals(serviceProject)) {
					_serviceBuildProjects.add(_getParentProject(checkProject));

					return true;
				}
				else {
					if (servicePath.segmentCount() == 3) {
						String paretnProjectName = serviceProjectLocation.segment(
							serviceProjectLocation.segmentCount() - 2);

						IProject sbProject = CoreUtil.getProject(paretnProjectName);

						if (checkProject.equals(sbProject)) {
							project = serviceProject;
							_serviceBuildProjects.add(sbProject);

							return true;
						}
					}
					else {
						return true;
					}
				}
			}
			else if (serviceXmlFiles.size() > 1) {
				Stream<IFile> stream = serviceXmlFiles.stream();

				stream.forEach(
					_serviceXmlPath -> {
						IPath servicePath = _serviceXmlPath.getFullPath();

						int segmentCounts = servicePath.segmentCount();

						if (segmentCounts > 3) {
							IPath prefixPath = servicePath.removeLastSegments(3);

							servicePath = servicePath.makeRelativeTo(prefixPath);
						}

						if (servicePath.segmentCount() == 3) {
							IPath serviceProjectLocation = servicePath.removeLastSegments(1);

							String paretnProjectName = serviceProjectLocation.segment(
								serviceProjectLocation.segmentCount() - 2);

							IProject sbProject = CoreUtil.getProject(paretnProjectName);

							if (sbProject.exists()) {
								_serviceBuildProjects.add(sbProject);
							}
						}
					});

				return true;
			}
			else if (ListUtil.isEmpty(serviceXmlFiles)) {
				return getServiceParentProject(_getParentProject(checkProject));
			}
		}
		else {
			return false;
		}

		return false;
	}

	protected void afterTask() {
		Stream<IProject> stream = _serviceBuildProjects.stream();

		stream.forEach(_gradleProject -> GradleUtil.refreshGradleProject(_gradleProject));
	}

	@Override
	protected String getGradleTask() {
		return "buildService";
	}

	@Override
	protected void setEnableTaskAction(IAction action) {
		_serviceBuildProjects.clear();
		boolean enabled = getServiceParentProject(project);

		action.setEnabled(enabled);
	}

	private IProject _getParentProject(IProject checkProject) {
		ProjectConnection connection = null;

		GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(
			checkProject.getLocation().toFile());

		connection = connector.connect();

		ModelBuilder<EclipseProject> modelBuilder = connection.model(EclipseProject.class);

		EclipseProject eclipseProject = modelBuilder.get();

		EclipseProject serviceParentEclipseProject = eclipseProject.getParent();

		if (serviceParentEclipseProject != null) {
			return CoreUtil.getProject(serviceParentEclipseProject.getProjectDirectory());
		}

		return null;
	}

	private List<IProject> _serviceBuildProjects;

}