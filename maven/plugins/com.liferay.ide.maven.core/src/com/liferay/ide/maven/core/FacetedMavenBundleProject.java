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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.IBundleProject;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Gregory Amerson
 */
public class FacetedMavenBundleProject extends FacetedMavenProject implements IBundleProject
{

    private final MavenBundlePluginProject bundleProject;

    public FacetedMavenBundleProject( IProject project )
    {
        super( project );

        this.bundleProject = new MavenBundlePluginProject( project );
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        return this.bundleProject.filterResource( resourcePath );
    }

    @Override
    public String getBundleShape()
    {
        return "war";
    }

    @Override
    public IPath getOutputBundle( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IPath outputJar = null;

        if( buildIfNeeded )
        {
            final MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder( this.getProject() );

            final List<String> goals = Arrays.asList( "package" );

            mavenProjectBuilder.execGoals( goals, monitor );
        }

        // we are going to try to get the output jar even if the package failed.
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
        final MavenProject mavenProject = projectFacade.getMavenProject( monitor );

        final String targetName = mavenProject.getBuild().getFinalName() + ".war";

        final IFolder targetFolder = getProject().getFolder( "target" );

        if( targetFolder.exists() )
        {
            //targetFolder.refreshLocal( IResource.DEPTH_ONE, monitor );
            final IPath targetFile = targetFolder.getRawLocation().append( targetName );

            if( targetFile.toFile().exists() )
            {
                outputJar = targetFile;
            }
        }

        if( outputJar == null || !outputJar.toFile().exists() )
        {
            throw new CoreException(
                LiferayMavenCore.createErrorStatus(
                    "Unable to get output bundle for project " + getProject().getName() ) );
        }

        return outputJar;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        return this.bundleProject.getSymbolicName();
    }

    @Override
    public boolean isFragmentBundle()
    {
        return false;
    }

    @Override
    public boolean isThemeBundle()
    {
        IProject project = getProject();
        try
        {
            IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( project, new NullProgressMonitor() );

            if( projectFacade != null )
            {
                MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );

                List<Plugin> buildPlugins = mavenProject.getBuildPlugins();

                for( Plugin plugin : buildPlugins )
                {
                    String themeBuilderId = plugin.getArtifactId();

                    if( themeBuilderId.equals( "com.liferay.portal.tools.theme.builder" ) )
                    {
                        return true;
                    }
                }
            }
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( e );
        }

        return false;
    }

}
