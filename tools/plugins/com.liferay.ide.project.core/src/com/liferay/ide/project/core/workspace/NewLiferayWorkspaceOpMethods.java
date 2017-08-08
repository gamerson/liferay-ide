
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

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceOpMethods
{

    public static final Status execute( final NewLiferayWorkspaceOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay Workspace project...", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayWorkspaceOp> provider = op.getProjectProvider().content( true );

            final IStatus status = provider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            if( !retval.ok() )
            {
                return retval;
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay Workspace project."; //$NON-NLS-1$

            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg, e );
        }

        if( retval.ok() )
        {
            updateBuildPrefs( op );
        }

        return retval;
    }

    private static void updateBuildPrefs( final NewLiferayWorkspaceOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put( ProjectCore.PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text() );

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default workspace build type."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );
        }
    }
}
