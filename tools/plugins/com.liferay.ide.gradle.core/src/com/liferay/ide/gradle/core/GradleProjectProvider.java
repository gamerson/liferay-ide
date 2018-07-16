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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class GradleProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayProjectProvider<NewLiferayModuleProjectOp> {

	public GradleProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		Value<String> projectName = op.getProjectName();

		String projectNameString = projectName.content();

		Value<Path> locationValue = op.getLocation();

		IPath location = PathBridge.create(locationValue.content());

		Value<String> className = op.getComponentName();

		Value<String> liferayVersion = op.getLiferayVersion();

		Value<String> serviceName = op.getServiceName();

		Value<String> packageName = op.getPackageName();

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		List<String> properties = new ArrayList<>();

		for (PropertyKey propertyKey : propertyKeys) {
			Value<String> name = propertyKey.getName();
			Value<String> value = propertyKey.getValue();

			properties.add(name.content(true) + "=" + value.content(true));
		}

		File targetDir = location.toFile();

		targetDir.mkdirs();

		Value<String> projectTemplateName = op.getProjectTemplateName();

		String projectTemplateNameString = projectTemplateName.content();

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(targetDir.getAbsolutePath());
		sb.append("\" ");
		sb.append("-v ");
		sb.append(liferayVersion.content());
		sb.append(" ");
		sb.append("-t ");
		sb.append(projectTemplateNameString);
		sb.append(" ");

		if (className.content() != null) {
			sb.append("-c ");
			sb.append(className.content());
			sb.append(" ");
		}

		if (serviceName.content() != null) {
			sb.append("-s ");
			sb.append(serviceName.content());
			sb.append(" ");
		}

		if (packageName.content() != null) {
			sb.append("-p ");
			sb.append(packageName.content());
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(projectNameString);
		sb.append("\" ");

		try {
			BladeCLI.execute(sb.toString());

			ElementList<ProjectName> projectNames = op.getProjectNames();

			ProjectName pn = projectNames.insert();

			pn.setName(projectNameString);

			if (projectTemplateNameString.equals("service-builder")) {
				pn = projectNames.insert();

				pn.setName(projectNameString + "-api");

				pn = projectNames.insert();

				pn.setName(projectNameString + "-service");
			}

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0) && !lastSegment.equals(projectNameString)) {
				projectLocation = location.append(projectNameString);
			}

			boolean hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();

			Value<Boolean> useDefaultLocationValue = op.getUseDefaultLocation();

			boolean useDefaultLocation = useDefaultLocationValue.content(true);

			boolean inWorkspacePath = false;

			IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			if (hasGradleWorkspace && (liferayWorkspaceProject != null) && !useDefaultLocation) {
				IPath workspaceLocation = liferayWorkspaceProject.getLocation();

				if (workspaceLocation != null) {
					inWorkspacePath = workspaceLocation.isPrefixOf(projectLocation);
				}
			}

			if ((hasGradleWorkspace && useDefaultLocation) || inWorkspacePath) {
				GradleUtil.refreshProject(liferayWorkspaceProject);
			}
			else {
				CoreUtil.openProject(projectNameString, projectLocation, monitor);

				GradleUtil.sychronizeProject(projectLocation, monitor);
			}
		}
		catch (Exception e) {
			retval = GradleCore.createErrorStatus("Can't create module project: " + e.getMessage(), e);
		}

		return retval;
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		ILiferayProject retval = null;

		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				if (!LiferayWorkspaceUtil.isValidWorkspace(project) && LiferayNature.hasNature(project) &&
					GradleProjectNature.isPresentOn(project)) {

					if (ProjectUtil.isFacetedGradleBundleProject(project)) {
						return new FacetedGradleBundleProject(project);
					}
					else {
						return new LiferayGradleProject(project);
					}
				}
			}
			catch (Exception e) {

				// ignore errors

			}
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		if (path != null) {
			if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(path.toOSString())) {
				retval = GradleCore.createErrorStatus(" Can't set WorkspaceProject root folder as project directory. ");
			}
		}

		return retval;
	}

}