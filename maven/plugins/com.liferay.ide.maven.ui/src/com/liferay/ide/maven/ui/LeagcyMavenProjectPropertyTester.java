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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Simon Jiang
 */
public class LeagcyMavenProjectPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject) {
			IProject project = (IProject)receiver;

			try {
				IMavenProjectFacade mavenProjectFacade = MavenUtil.getProjectFacade(project);

				if (mavenProjectFacade == null) {
					return false;
				}

				MavenProject mavenProject = mavenProjectFacade.getMavenProject(new NullProgressMonitor());

				if (mavenProject == null) {
					return false;
				}

				List<Plugin> buildPlugins = mavenProject.getBuildPlugins();

				for (Plugin plugin : buildPlugins) {
					if (ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(plugin.getGroupId())) {
						return true;
					}
				}

				return false;
			}
			catch (Exception e) {
			}
		}

		return false;
	}

}