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
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Simon Jiang
 */
public class LiferayWorkspaceSDKListener implements IResourceChangeListener, IResourceDeltaVisitor
{
    private final static Pattern PATTERN_BUILD_PROPERTIES  = Pattern.compile("build.[\\w|\\W.]*properties");
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
            IResourceDelta delta = event.getDelta();
            IPath deltaPath = new Path( "build.properties" );

            if ( delta != null )
            {
                for( IResourceDelta child : delta.getAffectedChildren() )
                {
                    IResourceDelta changeDelta = child.findMember( deltaPath );

                    if( changeDelta != null )
                    {
                        String propertiesLastSegment = changeDelta.getResource().getFullPath().lastSegment();
                        final Matcher propertiesMatcher = PATTERN_BUILD_PROPERTIES.matcher( propertiesLastSegment );

                        if( propertiesMatcher.matches() )
                        {
                            if( changeDelta.getKind() == IResourceDelta.ADDED || changeDelta.getKind() == IResourceDelta.REMOVED )
                            {
                                changeDelta.accept( this );
                            }
                        }
                    }
                }
            }
        }
        catch( Throwable e )
        {
           ProjectCore.logError( e );
        }
    }

    @Override
    public boolean visit( final IResourceDelta delta ) throws CoreException
    {
        switch( delta.getResource().getType() )
        {
            case IResource.ROOT:
            case IResource.FOLDER:
            case IResource.PROJECT:
                return true;

            case IResource.FILE:
            {
                Job job = new Job( "Checking workspace SDK" )
                {
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            boolean findSDK = false;
                            IProject[] projects = CoreUtil.getAllProjects();

                            for( IProject existProject : projects )
                            {
                                if ( SDKUtil.isValidSDKLocation( existProject.getLocation().toPortableString() ) )
                                {
                                    MarkerUtil.clearMarkers( existProject, IMarker.PROBLEM, ID_WORKSPACE_SDK_INVALID );

                                    if ( findSDK == false )
                                    {
                                        findSDK = true;
                                    }
                                    else
                                    {
                                        MarkerUtil.setMarker(existProject, IMarker.PROBLEM, IMarker.SEVERITY_ERROR,
                                            "Workspace has more than one SDK", existProject.getFullPath().toPortableString(),ID_WORKSPACE_SDK_INVALID);
                                    }
                                    existProject.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                                }
                            }
                        }
                        catch( CoreException e)
                        {
                            ProjectCore.logError( e );
                        }
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
                return false;
            }
        }
        return false;
    }
}