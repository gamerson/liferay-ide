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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Simon Jiang
 */
public class SDKBuildPropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor
{

    private static final String ID_SDK_PROPERTIES_INVALID = "sdk-properties-invalid";


    private final static Pattern buildPropertiesPattern = Pattern.compile("build.[\\w|\\W.]*properties");

    protected void processPropertiesFile( IFile buildPropertiesFile ) throws CoreException
    {

        SDK sdk = SDKUtil.getWorkspaceSDK();

        if ( sdk != null )
        {
            IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

            ProjectUtil.clearMarkers(sdkProject, ID_SDK_PROPERTIES_INVALID, IMarker.PROBLEM);

            IStatus status = sdk.validate(true, false);

            if ( !status.isOK() )
            {
                IStatus[] statuses = status.getChildren();

                for( IStatus iStatus : statuses )
                {
                    ProjectUtil.setMarker(sdkProject, IMarker.PROBLEM, IMarker.SEVERITY_ERROR,
                        iStatus.getMessage(), sdkProject.getFullPath().toPortableString(),ID_SDK_PROPERTIES_INVALID);
                }

                return;
            }
            sdk.getBuildProperties( true, true );
        }

        for( final IProject project : CoreUtil.getAllProjects() )
        {
            if ( !CoreUtil.isLiferayProject ( project ) )
            {
                continue;
            }

            new WorkspaceJob( "Updating project setting base on new sdk properties for " + project.getName() )
            {
                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    IJavaProject javaProject = JavaCore.create( project );

                    IPath containerPath = null;

                    IClasspathEntry[] entries = javaProject.getRawClasspath();

                    for( IClasspathEntry entry : entries )
                    {
                        if( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
                        {
                            if( entry.getPath().segment( 0 ).equals( PluginClasspathContainerInitializer.ID ) ||
                                            entry.getPath().segment( 0 ).equals( SDKClasspathContainer.ID ))
                            {
                                containerPath = entry.getPath();

                                break;
                            }
                        }
                    }

                    if( containerPath != null )
                    {
                        IClasspathContainer classpathContainer = JavaCore.getClasspathContainer( containerPath, javaProject );

                        final String id = containerPath.segment( 0 );

                        if ( id.equals( PluginClasspathContainerInitializer.ID ) ||
                             id.equals( SDKClasspathContainer.ID ) )
                        {
                            ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer( id );
                            initializer.requestClasspathContainerUpdate( containerPath, javaProject, classpathContainer );
                        }
                    }

                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

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
        catch( Throwable e )
        {
           ProjectCore.logError( e );
        }
    }

    protected boolean shouldProcessResourceDelta( IResourceDelta delta )
    {
        final IPath fullPath = delta.getResource().getRawLocation();
        try
        {
            SDK sdk = SDKUtil.getWorkspaceSDK();

            if ( sdk != null && sdk.getLocation().isPrefixOf( fullPath ))
            {
                if( fullPath.lastSegment() != null &&  buildPropertiesPattern.matcher( fullPath.lastSegment() ).matches() )
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
                        public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
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
