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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.Problem;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MarkDoneAction extends ProblemAction
{

    public MarkDoneAction()
    {
        this( new DummySelectionProvider() );
    }

    public MarkDoneAction( ISelectionProvider provider )
    {
        super( provider, "Mark done" );
    }

    public void run( final Problem problem, final ISelectionProvider provider )
    {
        super.run( problem, provider );

        problem.setStatus( Problem.STATUS_RESOLVED );

        MigrationUtil.updateMigrationProblemToStore( problem );

        new Job( "Re-run find breaking changes for this file" )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    MigrateProjectHandler migrateHandler = new MigrateProjectHandler();

                    Path path = new Path( problem.getFile().getPath() );
                    IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();
                    String projectName = "";
                    for( IProject project : projects )
                    {
                        if( problem.getFile().getPath().replaceAll( "\\\\", "/" ).startsWith(
                            project.getLocation().toString() ) )
                        {
                            projectName = project.getName();
                            break;
                        }
                    }

                    if( !projectName.equals( "" ) )
                    {
                        migrateHandler.findMigrationProblems( new Path[] { path }, new String[] { projectName } );
                    }
                }
                catch( Exception e )
                {
                }

                return Status.OK_STATUS;
            }

        }.schedule();
    }

    @Override
    protected IStatus runWithMarker( Problem problem, IMarker marker )
    {
        IStatus retval = Status.OK_STATUS;

        try
        {
            if( marker.exists() )
            {
                marker.delete();
                // marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_INFO );
                // marker.setAttribute( "migrationProblem.resolved", true );
            }

            problem.setStatus( Problem.STATUS_RESOLVED );
        }
        catch( CoreException e )
        {
            retval = e.getStatus();
        }

        return retval;
    }

}
