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

import java.io.File;
import java.io.IOException;
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
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IRuntime;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.OverrideFilePath;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

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
        IStatus retval = null;

        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        final String hostBundleName = op.getHostOsgiBundle().content();

        final IPath temp = ProjectCore.getDefault().getStateLocation().append(
            hostBundleName.substring( 0, hostBundleName.lastIndexOf( ".jar" ) ) );

        if( !temp.toFile().exists() )
        {
            final IRuntime runtime = ServerUtil.getRuntime( op.getLiferayRuntimeName().content() );

            final PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

            File hostBundle = portalBundle.getOSGiBundlesDir().append( "modules" ).append( hostBundleName ).toFile();

            if( !hostBundle.exists() )
            {
                hostBundle = ProjectCore.getDefault().getStateLocation().append( hostBundleName ).toFile();
            }

            try
            {
                ZipUtil.unzip( hostBundle, temp.toFile() );
            }
            catch( IOException e )
            {
                throw new CoreException( ProjectCore.createErrorStatus( e ) );
            }
        }

        String hostBundleSymbolicName = "";
        String hostBundleVersion = "";

        if( temp.toFile().exists() )
        {
            final File file = temp.append( "META-INF/MANIFEST.MF" ).toFile();
            final String[] contents = FileUtil.readLinesFromFile( file );

            for( String content : contents )
            {
                if( content.contains( "Bundle-SymbolicName:" ) )
                {
                    hostBundleSymbolicName = content.substring(
                        content.indexOf( "Bundle-SymbolicName:" ) + "Bundle-SymbolicName:".length() );
                }

                if( content.contains( "Bundle-Version:" ) )
                {
                    hostBundleVersion =
                        content.substring( content.indexOf( "Bundle-Version:" ) + "Bundle-Version:".length() ).trim();
                }
            }
        }

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

        properties.put( "hostBundleSymbolicName", hostBundleSymbolicName );
        properties.put( "hostBundleVersion", hostBundleVersion );
        properties.put( "buildType", "maven" );
        properties.put( "projectType", "standalone" );

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
            location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        final ElementList<OverrideFilePath> files = op.getOverrideFiles();

        if( CoreUtil.isNullOrEmpty( files ) )
        {
            retval = Status.OK_STATUS;

            return retval;
        }

        for( OverrideFilePath file : files )
        {
            File fragmentFile = temp.append( file.getValue().content() ).toFile();

            if( fragmentFile.exists() )
            {
                File folder = null;

                if( fragmentFile.getName().equals( "portlet.properties" ) )
                {
                    folder = location.append( projectName ).append( "src/main/java" ).toFile();

                    FileUtil.copyFileToDir( fragmentFile, "portlet-ext.properties", folder );
                }
                else
                {
                    String parent = fragmentFile.getParentFile().getPath();
                    parent = parent.replaceAll( "\\\\", "/" );
                    String metaInfResources = "META-INF/resources";

                    parent = parent.substring( parent.indexOf( metaInfResources ) + metaInfResources.length() );

                    IPath resources = location.append( projectName ).append( "src/main/resources/META-INF/resources" );

                    folder = resources.toFile();
                    folder.mkdirs();

                    if( !parent.equals( "resources" ) && !parent.equals( "" ) )
                    {
                        folder = resources.append( parent ).toFile();
                        folder.mkdirs();
                    }

                    FileUtil.copyFileToDir( fragmentFile, folder );
                }
            }
        }

        if( newProjects == null || newProjects.size() == 0 )
        {
            retval = LiferayMavenCore.createErrorStatus( "Unable to create project from archetype." );
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
            retval = Status.OK_STATUS;
        }

        return retval;
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
