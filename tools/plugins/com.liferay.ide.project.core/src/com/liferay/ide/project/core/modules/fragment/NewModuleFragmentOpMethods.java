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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Terry Jia
 */
public class NewModuleFragmentOpMethods
{

    public static final Status execute( final NewModuleFragmentOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module fragment project (this process may take several minutes)", 100 );

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewModuleFragmentOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            if( retval.ok() )
            {
                updateBuildPrefs( op );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module fragment project.";
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    private static void updateBuildPrefs( final NewModuleFragmentOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put(
                ProjectCore.PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text() );

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default project build type.";
            ProjectCore.logError( msg, e );
        }
    }

    public static String getMavenParentPomGroupId( NewModuleFragmentOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( groupId ) )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewModuleFragmentOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( version ) )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

}
