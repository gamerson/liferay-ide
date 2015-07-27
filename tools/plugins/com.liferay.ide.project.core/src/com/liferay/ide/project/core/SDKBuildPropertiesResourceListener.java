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
import com.liferay.ide.project.core.util.ClasspathUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Simon Jiang
 */
public class SDKBuildPropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor
{

    private static final String ID_SDK_PROPERTIES_INVALID = "sdk-properties-invalid";


    private final static Pattern PATTERN_BUILD_PROPERTIES  = Pattern.compile("build.[\\w|\\W.]*properties");

    protected void processPropertiesFile(final IFile buildPropertiesFile ) throws CoreException
    {

        final SDK sdk = SDKUtil.getWorkspaceSDK();

        if ( sdk != null )
        {
            final IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

            MarkerUtil.clearMarkers(sdkProject, IMarker.PROBLEM, ID_SDK_PROPERTIES_INVALID);

            final IStatus sdkValid = sdk.validate( true );

            if ( sdkValid.isOK() )
            {
                for( final IProject project : CoreUtil.getAllProjects() )
                {
                    if ( SDKUtil.isSDKProject( project ) && sdk.getLocation().isPrefixOf( project.getLocation() ))
                    {
                        Job job = new WorkspaceJob( "Updating project setting base on new sdk properties for " + project.getName() )
                        {
                            @Override
                            public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException
                            {
                                ClasspathUtil.updateRequestContainer(project);
                                return Status.OK_STATUS;
                            }
                        };
                        job.setRule( project );
                        job.schedule();
                    }
                }
            }
            else
            {
                final IStatus[] sdkValids = sdkValid.getChildren();

                for( final IStatus iStatus : sdkValids )
                {
                    MarkerUtil.setMarker(sdkProject, IMarker.PROBLEM, IMarker.SEVERITY_ERROR,
                        iStatus.getMessage(), sdkProject.getFullPath().toPortableString(),ID_SDK_PROPERTIES_INVALID);
                }

                return;
            }
        }
    }

    @Override
    public void resourceChanged( final IResourceChangeEvent event )
    {
        if( event == null )
        {
            return;
        }

        try
        {
            final SDK sdk = SDKUtil.getWorkspaceSDK();
            String[] customerPropertyNames = sdk.getCustomerPropertyNames();

            IResourceDelta delta = event.getDelta();
            if ( delta != null )
            {
                for( IResourceDelta child : delta.getAffectedChildren() )
                {
                    for( String propertyName : customerPropertyNames )
                    {
                        IPath deltaPath = new Path( "build." + propertyName +".properties" );
                        IResourceDelta changeDelta = child.findMember( deltaPath );
                        if ( changeDelta != null)
                        {
                            changeDelta.accept( this );
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

    protected boolean shouldProcessResourceDelta( final IResourceDelta delta )
    {
        IResource deltaResource = delta.getResource();

        if ( deltaResource == null || !deltaResource.exists() )
        {
            return false;
        }

        final IPath fullPath = deltaResource.getRawLocation();
        try
        {
            final SDK sdk = SDKUtil.getWorkspaceSDK();

            if ( sdk != null && sdk.getLocation().isPrefixOf( fullPath ))
            {
                if( fullPath.lastSegment() != null &&  PATTERN_BUILD_PROPERTIES .matcher( fullPath.lastSegment() ).matches() )
                {
                    final File propertiesFile = fullPath.toFile();

                    if( propertiesFile != null && propertiesFile.exists() )
                    {
                        return true;
                    }
                }
            }
            else
            {
                return false;
            }

        }
        catch( CoreException e )
        {
            return false;
        }

        return false;
    }


    @Override
    public boolean visit( final IResourceDelta delta ) throws CoreException
    {
        switch( delta.getResource().getType() )
        {
            case IResource.ROOT:
            case IResource.PROJECT:
            case IResource.FOLDER:
                return true;

            case IResource.FILE:
            {
                if( shouldProcessResourceDelta( delta ) )
                {
                    Job job = new WorkspaceJob( "Processing SDK build properties file" )
                    {
                        @Override
                        public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException
                        {
                            final IResource resource = delta.getResource();
                            try
                            {
                                processPropertiesFile( (IFile) resource );
                            }
                            catch(CoreException e)
                            {
                                ProjectCore.logError( e );
                            }

                            return Status.OK_STATUS;
                        }
                    };

                    job.setRule( CoreUtil.getWorkspaceRoot() );
                    job.schedule();
                }

                return false;
            }
        }

        return false;
    }

}
