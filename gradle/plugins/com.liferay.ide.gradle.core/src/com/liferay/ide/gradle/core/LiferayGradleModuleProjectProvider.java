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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.gradleware.tooling.toolingclient.BuildLaunchRequest;
import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.gradleware.tooling.toolingclient.LaunchableConfig;
import com.gradleware.tooling.toolingmodel.repository.FixedRequestAttributes;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.core.LiferayModuleProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.model.ProjectName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.file.FileUtils;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.util.progress.DelegatingProgressListener;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectJob;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.gradle.tooling.CancellationToken;
import org.gradle.tooling.ProgressListener;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleModuleProjectProvider extends LiferayModuleProjectProvider
{

    public LiferayGradleModuleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( LiferayNature.hasNature( project ) && GradleProjectNature.INSTANCE.isPresentOn( project ) )
                {
                    return new LiferayGradleProject( project );
                }
            }
            catch( Exception e )
            {
                // ignore errors
            }
        }

        return retval;
    }

    private static final class NewGradleProjectInitializer implements AsyncHandler
    {

        private final FixedRequestAttributes fixedAttributes;
        private final Optional<List<ProgressListener>> listeners;

        private NewGradleProjectInitializer( ProjectImportConfiguration configuration )
        {
            this.fixedAttributes = configuration.toFixedAttributes();
            this.listeners = Optional.absent();
        }

        private NewGradleProjectInitializer( ProjectImportConfiguration configuration, List<ProgressListener> listeners )
        {
            this.fixedAttributes = configuration.toFixedAttributes();
            this.listeners = Optional.of( (List<ProgressListener>) ImmutableList.copyOf( listeners ) );
        }

        @Override
        public void run( IProgressMonitor monitor, CancellationToken token )
        {
            monitor.beginTask( "Init Gradle project", IProgressMonitor.UNKNOWN );
            try
            {
                File projectDir = this.fixedAttributes.getProjectDir().getAbsoluteFile();
                if( projectDir.exists() )
                {
                    // prepare the request
                    final ImmutableList<String> GRADLE_INIT_TASK_CMD_LINE =
                        ImmutableList.of( "init", "--type", "java-library" );
                    List<String> tasks = GRADLE_INIT_TASK_CMD_LINE;
                    GradleDistribution gradleDistribution = this.fixedAttributes.getGradleDistribution();
                    File gradleUserHome =
                        FileUtils.getAbsoluteFile( this.fixedAttributes.getGradleUserHome() ).orNull();
                    File javaHome = FileUtils.getAbsoluteFile( this.fixedAttributes.getJavaHome() ).orNull();
                    List<String> jvmArguments = this.fixedAttributes.getJvmArguments();
                    List<String> arguments = this.fixedAttributes.getArguments();
                    ProgressListener[] progressListeners =
                        this.listeners.isPresent()
                            ? this.listeners.get().toArray( new ProgressListener[this.listeners.get().size()] )
                            : new ProgressListener[] { new DelegatingProgressListener( monitor ) };

                    // configure the request
                    BuildLaunchRequest request =
                        CorePlugin.toolingClient().newBuildLaunchRequest( LaunchableConfig.forTasks( tasks ) );
                    request.projectDir( projectDir );
                    request.gradleDistribution( gradleDistribution );
                    request.gradleUserHomeDir( gradleUserHome );
                    request.javaHomeDir( javaHome );
                    request.jvmArguments( jvmArguments.toArray( new String[jvmArguments.size()] ) );
                    request.arguments( arguments.toArray( new String[arguments.size()] ) );
                    request.progressListeners( progressListeners );
                    request.cancellationToken( token );

                    // launch the build
                    request.executeAndWait();
                }
            }
            finally
            {
                monitor.done();
            }
        }
    }

    @Override
    public IStatus doCreateNewProject(
        NewLiferayModuleProjectOp op, IProgressMonitor monitor, ElementList<ProjectName> projectNames )
        throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String projectType = op.getProjectTemplate().content().toString();

        IStatus createStatus =
            createOSGIBundleProject(
                location.toFile(), location.toFile(), projectType, ProjectBuild.gradle.toString(), projectName,
                projectName, projectName );

        if( createStatus.isOK() )
        {
            ProjectImportConfiguration configuration = new ProjectImportConfiguration();
            GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );
            configuration.setGradleDistribution( from );
            configuration.setProjectDir( location.append( projectName ).toFile() );
            configuration.setApplyWorkingSets( false );
            configuration.setWorkingSets( new ArrayList<String>() );
            new SynchronizeGradleProjectJob(
                configuration.toFixedAttributes(), configuration.getWorkingSets().getValue(),
                new NewGradleProjectInitializer( configuration ) ).schedule();
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;
        return retval;
    }

}
