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

import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 */
public class GradleProjectBuilder extends AbstractProjectBuilder
{

    private IFile gradleBuildFile;

    public GradleProjectBuilder( IProject project )
    {
        super( project );

        gradleBuildFile = project.getFile( "build.gradle" );
    }

    @Override
    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        return runGradleTask( "buildLang", monitor );
    }

    @Override
    public IStatus buildService( IProgressMonitor monitor ) throws CoreException
    {
        return runGradleTask( "buildService", monitor );
    }

    @Override
    public IStatus buildWSDD( IProgressMonitor monitor ) throws CoreException
    {
        // TODO Waiting for IDE-2850
        return null;
    }

    private IStatus runGradleTask( String task, IProgressMonitor monitor )
    {
        IStatus status = Status.OK_STATUS;

        if( gradleBuildFile.exists() )
        {
            try
            {
                monitor.beginTask( task, 100 );

                GradleUtil.runGradleTask( getProject(), task, monitor );

                monitor.worked( 80 );

                getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );

                monitor.worked( 10 );
            }
            catch( Exception e )
            {
                status = GradleCore.createErrorStatus( "Error running Gradle goal " + task, e );
            }
        }
        else
        {
            status = GradleCore.createErrorStatus( "No build.gradle file" );
        }

        return status;
    }

    @Override
    public void updateProjectDependency( IProject project, String group, String name, String version )
        throws CoreException
    {
        try
        {
            if( gradleBuildFile.exists() )
            {
                GradleDependencyUpdater updater = new GradleDependencyUpdater( gradleBuildFile.getLocation().toFile() );
                List<GradleDependency> existDependencies = updater.getAllDependencies();
                GradleDependency gd = new GradleDependency( group, name, version );

                if( !existDependencies.contains( gd ) )
                {
                    updater.insertDependency( gd );
                    Files.write(
                        gradleBuildFile.getLocation().toFile().toPath(), updater.getGradleFileContents(),
                        StandardCharsets.UTF_8 );
                    Set<IProject> set = new HashSet<>();
                    set.add( project );
                    CorePlugin.gradleWorkspaceManager().getCompositeBuild( set ).synchronize(
                        NewProjectHandler.IMPORT_AND_MERGE );
                }
            }
        }
        catch( IOException ie)
        {
           GradleCore.logError( "failed update dependency for project "  + project.getName(), ie );
        }   
    }

}
