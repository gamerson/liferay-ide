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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.LiferayWorkspaceNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceUtil
{

    public static boolean isValidWorkspaceLocation( String location )
    {
        boolean retval = false;

        File workspaceDir = new File( location );

        File buildGradle = new File( workspaceDir, "build.gradle" );
        File settingsGradle = new File( workspaceDir, "settings.gradle" );

        // TODO need more check
        retval = buildGradle.exists() && settingsGradle.exists();

        return retval;
    }

    //For import liferay workspace wizard
    public static void clearWorkspace( String location )
    {
        File projectFile = new File( location, ".project" );

        if( projectFile.exists() )
        {
            projectFile.delete();
        }

        File classpathFile = new File( location, ".classpath" );

        if( classpathFile.exists() )
            classpathFile.delete();

        File settings = new File( location, ".settings" );

        if( settings.exists() && settings.isDirectory() )
        {
            FileUtil.deleteDir( settings, true );
        }
    }

    public static boolean hasLiferayWorkspace()
    {
        IProject[] projects = CoreUtil.getAllProjects();

        LiferayWorkspaceNature liferayWorkspaceNature = new LiferayWorkspaceNature();

        for( IProject project : projects )
        {
            if( liferayWorkspaceNature.hasNature( project ) )
            {
                return true;
            }
        }

        return false;
    }

}
