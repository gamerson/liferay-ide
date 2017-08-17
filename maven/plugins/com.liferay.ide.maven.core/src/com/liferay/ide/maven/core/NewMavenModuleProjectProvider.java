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

import com.liferay.ide.core.util.ASTUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.io.File;
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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewMavenModuleProjectProvider extends LiferayMavenProjectProvider
    implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>
{

    @Override
    public IStatus createNewProject( NewLiferayModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        IPath location = PathBridge.create( op.getLocation().content() );

        final String groupId = op.getGroupId().content();
        final String projectName = op.getProjectName().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getArtifactVersion().content();
        final String javaPackage = op.getPackageName().content();
        final String className = op.getComponentName().content();
        final String serviceName = op.getServiceName().content();
        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        final String archetypeArtifactId = op.getArchetype().content();

        final Archetype archetype = new Archetype();

        final String[] gav = archetypeArtifactId.split( ":" );

        final String archetypeVersion = gav[gav.length - 1];

        archetype.setGroupId( gav[0] );
        archetype.setArtifactId( gav[1] );
        archetype.setVersion( archetypeVersion );

        final Properties properties = new Properties();

        if( archetype.getArtifactId().endsWith( "service.builder" ) )
        {
            String apiPath = ":" + artifactId + "-api";

            properties.put( "apiPath", apiPath );
        }

        properties.put( "buildType", "maven" );
        properties.put( "package", javaPackage );
        properties.put( "className", className == null ? "" : className );
        properties.put( "projectType", "standalone" );
        properties.put( "serviceClass", serviceName == null ? "" : serviceName );
        properties.put( "serviceWrapperClass", serviceName == null ? "" : serviceName );
        properties.put( "contributorType", artifactId );
        properties.put( "author", "liferay" );

        for( PropertyKey propertyKey : op.getPropertyKeys() )
        {
            String key = propertyKey.getName().content();
            String value = propertyKey.getValue().content();

            properties.put( key, value );
        }

        if( serviceName != null )
        {
            properties.put( "service", serviceName );
        }

        IPath projectLocation = location;

        final String lastSegment = location.lastSegment();

        if( location != null && location.segmentCount() > 0 )
        {
            if( !lastSegment.equals( projectName ) )
            {
                projectLocation = location.append( projectName );
            }
        }

        final List<String> moduleProperties = new ArrayList<String>();

        for( PropertyKey propertyKey : propertyKeys )
        {
            moduleProperties.add(
                propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
        }

        final IPath projectPath = projectLocation;

        new Job( "creating project" )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    final List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
                        location, archetype, groupId, artifactId, version, javaPackage, properties,
                        new ProjectImportConfiguration( new ResolverConfiguration() ), monitor );

                    try
                    {
                        ASTUtil.addProperties( projectPath, moduleProperties );
                    }
                    catch( Exception e1 )
                    {
                        LiferayMavenCore.logError( e1 );;
                    }

                    ElementList<ProjectName> projectNames = op.getProjectNames();

                    if( newProjects == null || newProjects.size() == 0 )
                    {
                        return LiferayMavenCore.createErrorStatus( "Unable to create project from archetype." );
                    }
                    else
                    {
                        for( IProject newProject : newProjects )
                        {
                            projectNames.insert().setName( newProject.getName() );

                            String[] gradleFiles = new String[] { "build.gradle", "settings.gradle" };

                            for( String path : gradleFiles )
                            {
                                IFile gradleFile = newProject.getFile( path );

                                if( gradleFile.exists() )
                                {
                                    gradleFile.delete( true, monitor );
                                }
                            }
                        }
                    }

                    CoreUtil.getProject( projectName ).refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
                catch( CoreException e )
                {
                    return LiferayMavenCore.createErrorStatus( "Unable to create project", e );
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

    public void deleteGradleFiles( File folder )
    {
        File[] files = folder.listFiles();

        for( File file : files )
        {
            if( file.isDirectory() )
            {
                deleteGradleFiles( file );
            }
            else if( file.getName().equals( "build.gradle" ) || file.getName().equals( "settings.gradle" ) )
            {
                file.delete();
            }
        }
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        if( "archetypeGAV".equals( key ) && type.equals( String.class ) && params.length == 1 )
        {
            List<T> retval = new ArrayList<>();

            String templateName = params[0].toString();

            String gav = LiferayMavenCore.getPreferenceString(
                LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PREFIX + templateName, "" );

            if( CoreUtil.empty( gav ) )
            {
                gav = "com.liferay:com.liferay.project.templates." + templateName.replace( "-", "." ) + ":1.0.0";
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
