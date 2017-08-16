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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.server.util.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class LiferayGradleWorkspaceProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>
{

    public LiferayGradleWorkspaceProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus createNewProject( NewLiferayWorkspaceOp op, IProgressMonitor monitor ) throws CoreException
    {
        IPath location = PathBridge.create( op.getLocation().content() );
        String wsName = op.getWorkspaceName().toString();

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + location.append( wsName ).toFile().getAbsolutePath() + "\"" );
        sb.append( " " );
        sb.append( "init" );

        try
        {
            BladeCLI.execute( sb.toString() );
        }
        catch( BladeCLIException e )
        {
            return ProjectCore.createErrorStatus( e );
        }

        String workspaceLocation = location.append( wsName ).toPortableString();
        boolean isInitBundle = op.getProvisionLiferayBundle().content();
        String bundleUrl = op.getBundleUrl().content( false );
        String serverName = op.getServerName().content();

        return importProject( workspaceLocation, serverName, monitor, isInitBundle, bundleUrl );
    }

    @Override
    public IStatus importProject(
        String location, String serverName, IProgressMonitor monitor, boolean initBundle, String bundleUrl )
    {
        try
        {
            new Job( "creating liferay workspace" )
            {

                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    try
                    {
                        GradleUtil.importGradleProject( new File( location ), monitor );
                    }
                    catch( CoreException e )
                    {
                        return GradleCore.createErrorStatus( "Unable to create liferay workspace", e );
                    }

                    return Status.OK_STATUS;
                }

            }.schedule();

            if( initBundle )
            {
                IPath path = new Path( location );

                IProject project = CoreUtil.getProject( path.lastSegment() );

                if( bundleUrl != null )
                {
                    final IFile gradlePropertiesFile = project.getFile( "gradle.properties" );

                    String content = FileUtil.readContents( gradlePropertiesFile.getContents() );

                    String bundleUrlProp = "liferay.workspace.bundle.url=" + bundleUrl;

                    String separator = System.getProperty( "line.separator", "\n" );

                    String newContent = content + separator + bundleUrlProp;

                    gradlePropertiesFile.setContents(
                        new ByteArrayInputStream( newContent.getBytes() ), IResource.FORCE, monitor );
                }

                new Job( "init liferay bundle" )
                {

                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            GradleUtil.runGradleTask( new File( location ), "initBundle", monitor );

                            IPath bundlesLocation = null;

                            bundlesLocation = LiferayWorkspaceUtil.getHomeLocation( location );

                            if( bundlesLocation.toFile().exists() )
                            {
                                ServerUtil.addPortalRuntimeAndServer( serverName, bundlesLocation, monitor );
                            }

                            if( project != null && project.isOpen() )
                            {
                                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                            }

                        }
                        catch( CoreException e )
                        {
                            return GradleCore.createErrorStatus( "Unable to download liferay bundle", e );
                        }

                        return Status.OK_STATUS;
                    }

                }.schedule();
            }
        }
        catch( Exception e )
        {
            return GradleCore.createErrorStatus( "import Liferay workspace project error", e );
        }

        return Status.OK_STATUS;
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( LiferayWorkspaceUtil.isValidWorkspace( project ) )
            {
                return new LiferayWorkspaceProject( project );
            }
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        // TODO validation gradle project location

        return retval;
    }
}
