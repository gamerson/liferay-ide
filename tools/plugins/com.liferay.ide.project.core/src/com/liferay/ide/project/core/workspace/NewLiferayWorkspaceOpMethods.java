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

import org.eclipse.buildship.core.event.Event;
import org.eclipse.buildship.core.event.EventListener;
import org.eclipse.buildship.core.workspace.ProjectCreatedEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import com.liferay.ide.core.LiferayWorkspaceNature;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.util.ProjectImportUtil;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceOpMethods implements EventListener
{
    public static NewLiferayWorkspaceOpMethods instance ;

    public static String currentProject ;

    public static NewLiferayWorkspaceOpMethods getInstance()
    {
        if( instance == null )
        {
            instance = new NewLiferayWorkspaceOpMethods();
        }

        return instance;
    }

    public static final Status execute( final NewLiferayWorkspaceOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay Workspace project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = Status.createOkStatus();

        final Path projectLocation = op.getLocation().content();
        updateLocation( op, projectLocation );

        try
        {
            createWorkspace( op, monitor );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay Workspace project."; //$NON-NLS-1$

            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    private static void createWorkspace( final NewLiferayWorkspaceOp op, final IProgressMonitor pm ) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "-b " );
        // quote with quotation marks to deal with whitespace in command line
        sb.append( "\"" + op.getLocation() + "\" " );
        sb.append( "init" );

        BladeCLI.execute( sb.toString() );
        ProjectImportUtil.importGradleProject( op.getLocation().content().toFile() );
        currentProject = op.getLocation().content().lastSegment();
    }

    public static void updateLocation( final NewLiferayWorkspaceOp op, final Path baseLocation )
    {
        final String projectName = op.getWorkspaceName().content();

        if ( baseLocation == null)
        {
            return ;
        }

        final String lastSegment = baseLocation.lastSegment();

        if ( baseLocation!= null && baseLocation.segmentCount()>0)
        {
            if ( lastSegment.equals( projectName ))
            {
                return;
            }
        }

        final Path newLocation = baseLocation.append( projectName );

        op.setLocation( newLocation );
    }

    @Override
    public void onEvent( Event event )
    {
        if( event instanceof ProjectCreatedEvent )
        {
            final IProgressMonitor npm = new NullProgressMonitor();
            final ProjectCreatedEvent projectCreatedEvent = (ProjectCreatedEvent) event;

            final IProject project = projectCreatedEvent.getProject();

            LiferayWorkspaceNature liferayWorkspaceNature = new LiferayWorkspaceNature();

            if( currentProject.equals( project.getName() ) && !liferayWorkspaceNature.hasNature( project ))
            {
                try
                {
                    liferayWorkspaceNature.addLiferayNature( project, npm );
                }
                catch( Exception e )
                {
                    final String msg = "Error adding Liferay Workspace nature.";
                    ProjectCore.logError( msg, e );
                }
            }
        }
    }
}
