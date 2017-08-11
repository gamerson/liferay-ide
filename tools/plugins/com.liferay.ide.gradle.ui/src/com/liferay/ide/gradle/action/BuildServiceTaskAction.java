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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleUtil;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Lovett Li
 * @author Terry Jia
 * @author Andy Wu
 */
public class BuildServiceTaskAction extends GradleTaskAction
{

    @Override
    protected String getGradleTask()
    {
        return "buildService";
    }

    protected void afterTask()
    {
        boolean refresh = false;

        IProject[] projects = CoreUtil.getClasspathProjects( _project );

        for( IProject project : projects )
        {
            List<IFolder> folders = CoreUtil.getSourceFolders( JavaCore.create( project ) );

            if( folders.size() == 0 )
            {
                refresh = true;
            }
            else
            {
                try
                {
                    project.refreshLocal( IResource.DEPTH_INFINITE, null );
                }
                catch( CoreException e )
                {
                }
            }
        }

        List<IFolder> folders = CoreUtil.getSourceFolders( JavaCore.create( _project ) );

        if( folders.size() == 0 || refresh )
        {
            // refresh this project will also transmit to refresh -api project
            GradleUtil.refreshGradleProject( _project );
        }
        else
        {
            try
            {
                _project.refreshLocal( IResource.DEPTH_INFINITE, null );
            }
            catch( CoreException e )
            {
            }
        }
    }
}
