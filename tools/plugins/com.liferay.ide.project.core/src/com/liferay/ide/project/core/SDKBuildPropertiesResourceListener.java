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
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Simon Jiang
 */
public class SDKBuildPropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor
{
    private final static Pattern buildPropertiesPattern = Pattern.compile("build.[\\w|\\W.]*properties");

    public static boolean isLiferayProject( IProject project )
    {
        boolean retval = false;

        try
        {
            IFacetedProject facetedProject = ProjectFacetsManager.create( project );

            if( facetedProject != null )
            {
                for( IProjectFacetVersion facet : facetedProject.getProjectFacets() )
                {
                    IProjectFacet projectFacet = facet.getProjectFacet();

                    if( projectFacet.getId().startsWith( "liferay." ) ) //$NON-NLS-1$
                    {
                        retval = true;

                        break;
                    }
                }
            }
        }
        catch( CoreException e )
        {
        }

        return retval;
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

    protected void processPropertiesFile( IFile buildPropertiesFile ) throws CoreException
    {
        SDK sdk = SDKUtil.getWorkspaceSDK();

        if ( sdk != null)
        {
            sdk.getBuildProperties( true );
            IStatus status = sdk.validate();

            if ( !status.isOK() )
            {
                return;
            }

        }

        for( final IProject project : CoreUtil.getAllProjects() )
        {
            if ( !isLiferayProject ( project ) )
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

                            processPropertiesFile( (IFile) resource );

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
