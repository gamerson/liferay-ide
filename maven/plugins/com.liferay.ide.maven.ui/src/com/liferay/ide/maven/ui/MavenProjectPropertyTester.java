/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.maven.ui;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.MavenUtil;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class MavenProjectPropertyTester extends PropertyTester
{

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
		if (receiver instanceof IProject)
		{
			try
			{
				IProject project = (IProject) receiver;
				boolean isMavenProject = MavenUtil.isMavenProject(project);

				if (isMavenProject == true)
				{
					final IFile pomResource = project.getFile(IMavenConstants.POM_FILE_NAME);

					MavenProject mavenProject = MavenPlugin.getMaven().
							readProject(pomResource.getLocation().toFile(), new NullProgressMonitor());

					final Plugin liferayMavenplugin = mavenProject.getPlugin(ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY);

					if (liferayMavenplugin == null)
					{
						return false;
					}

					return true;
				}

				return false;
			}
			catch (CoreException e)
			{
				// don't log error
			}
		}

        return false;
    }
}
