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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOpMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class MavenModuleFragmentProjectProvider extends LiferayMavenProjectProvider
    implements NewLiferayProjectProvider<NewModuleFragmentOp>
{

    @Override
    public IStatus createNewProject( NewModuleFragmentOp op, IProgressMonitor monitor ) throws CoreException
    {
        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        String[] bsnAndVersion = NewModuleFragmentOpMethods.getBsnAndVersion( op );
        String hostBundleSymbolicName = bsnAndVersion[0];
        String hostBundleVersion = bsnAndVersion[1];

        final String groupId = op.getGroupId().content();
        final String artifactId = projectName;
        final String version = op.getArtifactVersion().content();
        final String javaPackage = "";

        final String archetypeArtifactId = getData( "archetypeGAV", String.class, "" ).get( 0 );
        final Archetype archetype = new Archetype();
        final String[] gav = archetypeArtifactId.split( ":" );

        final String archetypeVersion = gav[gav.length - 1];

        archetype.setGroupId( gav[0] );
        archetype.setArtifactId( gav[1] );
        archetype.setVersion( archetypeVersion );

        final Properties properties = new Properties();

        properties.put( "package", artifactId );
        properties.put( "hostBundleSymbolicName", hostBundleSymbolicName );
        properties.put( "hostBundleVersion", hostBundleVersion );
        properties.put( "buildType", "maven" );
        properties.put( "projectType", "standalone" );

        new Job( "creating module fragment project" )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {

                    final IProjectConfigurationManager projectConfigurationManager =
                        MavenPlugin.getProjectConfigurationManager();

                    final List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
                        location, archetype, groupId, artifactId, version, javaPackage, properties,
                        new ProjectImportConfiguration( new ResolverConfiguration() ), monitor );

                    NewModuleFragmentOpMethods.copyOverrideFiles( op );

                    if( newProjects == null || newProjects.size() == 0 )
                    {
                        return LiferayMavenCore.createErrorStatus(
                            "Unable to create fragment project from archetype." );
                    }
                    else
                    {
                        for( IProject newProject : newProjects )
                        {
                            String[] gradleFiles = new String[] { "build.gradle", "settings.gradle" };

                            for( String path : gradleFiles )
                            {
                                IFile gradleFile = newProject.getFile( path );

                                if( gradleFile.exists() )
                                {
                                    gradleFile.delete( true, monitor );
                                }
                            }

                            newProject.refreshLocal( IResource.DEPTH_INFINITE, null );
                        }
                    }

                }
                catch( CoreException e )
                {
                    return LiferayMavenCore.createErrorStatus( "Unable to create module fragment project", e );
                }

                return Status.OK_STATUS;
            }

            @Override
            public boolean belongsTo( Object family )
            {
                return family != null && family.toString().equals( LiferayMavenCore.JobFamilyId );
            }
        }.schedule();

        return Status.OK_STATUS;
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        if( "archetypeGAV".equals( key ) && type.equals( String.class ) && params.length == 1 )
        {
            List<T> retval = new ArrayList<>();

            String gav =
                LiferayMavenCore.getPreferenceString( LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_FRAGMENT, "" );

            if( CoreUtil.empty( gav ) )
            {
                gav = "com.liferay:com.liferay.project.templates.fragment" + ":1.0.1";
            }

            retval.add( type.cast( gav ) );

            return retval;
        }

        return super.getData( key, type, params );
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        return Status.OK_STATUS;
    }

}
