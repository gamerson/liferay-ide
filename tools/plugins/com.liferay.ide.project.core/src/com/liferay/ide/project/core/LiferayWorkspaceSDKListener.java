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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Simon Jiang
 */
public class LiferayWorkspaceSDKListener implements IResourceChangeListener, IResourceDeltaVisitor
{

    private static final String ID_WORKSPACE_SDK_INVALID = "workspace-sdk-invalid";

    @Override
    public void resourceChanged( IResourceChangeEvent event )
    {
        if( event == null )
        {
            return;
        }

        try
        {
            event.getDelta().accept( this );
        }
        catch( CoreException e )
        {
        }
    }

    @Override
    public boolean visit( final IResourceDelta delta ) throws CoreException
    {
        switch( delta.getResource().getType() )
        {
            case IResource.ROOT:
                return true;
            case IResource.FOLDER:
            case IResource.FILE:
                return false;

            case IResource.PROJECT:
            {
                if ( delta.getKind() != IResourceDelta.REMOVED )
                {
                    final IProject project = ( IProject )delta.getResource();

                    IProject[] projects = CoreUtil.getAllProjects();
                    for( IProject existProject : projects )
                    {
                        boolean hasMarker = ProjectUtil.findMakers( existProject, ID_WORKSPACE_SDK_INVALID, IMarker.PROBLEM);

                        if ( hasMarker )
                        {
                            return false;
                        }
                    }

                    if ( project != null )
                    {
                        if ( SDKUtil.isValidSDKLocation( project.getLocation().toPortableString() ) )
                        {
                            Job job = new WorkspaceJob( "Checking workspace SDK" )
                            {
                                @Override
                                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                                {
                                    IProject[] projects = CoreUtil.getAllProjects();
                                    for( IProject iProject : projects )
                                    {
                                        if ( SDKUtil.isValidSDKLocation( iProject.getLocation().toPortableString() ) )
                                        {
                                             if ( !iProject.getLocation().equals( project.getLocation() ))
                                             {
                                                 ProjectUtil.clearMarkers( project, ID_WORKSPACE_SDK_INVALID,  IMarker.PROBLEM );
                                                 ProjectUtil.setMarker(project, IMarker.PROBLEM, IMarker.SEVERITY_ERROR,
                                                      "Workspace has more than one SDK", project.getFullPath().toPortableString(),ID_WORKSPACE_SDK_INVALID);
                                             }
                                        }
                                    }
                                    return Status.OK_STATUS;
                                }
                            };
                            job.setRule( project );
                            job.schedule();
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}
