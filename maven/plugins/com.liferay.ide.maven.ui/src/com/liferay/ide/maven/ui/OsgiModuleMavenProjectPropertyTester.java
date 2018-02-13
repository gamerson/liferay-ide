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

package com.liferay.ide.maven.ui;

import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.MavenUtil;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Simon Jiang
 */
public class OsgiModuleMavenProjectPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean hasOsgiMavenPlguinLib = false;

		if (receiver instanceof IProject) {
			try {
				IProject project = (IProject)receiver;

				IMavenProjectFacade mavenProjectFacade = MavenUtil.getProjectFacade(project);

				if (mavenProjectFacade == null) {
					return false;
				}

				MavenProject mavenProject = mavenProjectFacade.getMavenProject(new NullProgressMonitor());

				if (mavenProject == null) {
					return false;
				}

				MavenProject parentProject = _getParentProject(mavenProject);

				List<Plugin> parentBuildPlugins = parentProject.getBuildPlugins();

				List<Plugin> buildPlugins = mavenProject.getBuildPlugins();

				for (Plugin plugin : buildPlugins) {
					if (!mavenProject.equals(parentProject) && parentBuildPlugins.contains(plugin)) {
						continue;
					}

					if (ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(plugin.getGroupId()) &&
						!ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(plugin.getGroupId())) {

						hasOsgiMavenPlguinLib = true;

						break;
					}
				}

				if (hasOsgiMavenPlguinLib) {
					return true;
				}

				return false;
			}
			catch (CoreException ce) {
				LiferayMavenUI.logError(ce);
			}
		}

		return false;
	}

	private MavenProject _getParentProject(MavenProject project) {
		MavenProject parentProject = project.getParent();

		if (parentProject != null) {
			return _getParentProject(parentProject);
		}

		return project;
	}

}