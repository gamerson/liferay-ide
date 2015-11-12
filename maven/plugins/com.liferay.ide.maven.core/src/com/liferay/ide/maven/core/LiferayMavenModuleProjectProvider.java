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

import com.liferay.blade.api.ProjectBuild;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.LiferayModuleProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.server.util.ComponentUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.settings.Profile;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenModuleProjectProvider extends LiferayModuleProjectProvider
{

    public LiferayMavenModuleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus doCreateNewProject(
        final NewLiferayModuleProjectOp op, IProgressMonitor monitor, ElementList<ProjectName> projectNames )
        throws CoreException
    {
        IStatus retval = null;

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String projectType = op.getProjectTemplate().content().toString();

        final List<IProject> newProjects = new ArrayList<IProject>();


        IStatus createStatus = createOSGIBundleProject(
                location.toFile(), location.toFile(), projectType, ProjectBuild.maven.toString(), projectName,
                projectName, projectName );

        if ( createStatus.isOK() )
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();

            try
            {
                IPath pomPath = location.append( projectName ).append( "pom.xml" );

                if ( pomPath != null && pomPath.toFile().exists() )
                {
                    File pomFile = new File( location.append( projectName ).append( "pom.xml" ).toPortableString() );
                    MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
                    final ResolverConfiguration resolverConfig = new ResolverConfiguration();
                    final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();

                    Model model = mavenModelManager.readMavenModel( pomFile );
                    MavenProjectInfo projectInfo = new MavenProjectInfo( pomFile.getName(), pomFile, model, null );
                    setBasedirRename( projectInfo );

                    projectInfos.add( projectInfo );

                    ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration( resolverConfig );


                    final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
                    final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

                    projectConfigurationManager.importProjects( projectInfos, importConfiguration, monitor );

                    IProject project = workspace.getRoot().getProject( projectName );

                    if ( project.exists() )
                    {
                        newProjects.add( project );

                        if( !CoreUtil.isNullOrEmpty( newProjects ) )
                        {
                            for( IProject iProject : newProjects )
                            {
                                projectNames.insert().setName( iProject.getName() );
                            }
                        }

                        if( CoreUtil.isNullOrEmpty( newProjects ) )
                        {
                            retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
                        }
                        else
                        {
                            for( final IProject iProject : newProjects )
                            {
                                try
                                {
                                    projectConfigurationManager.updateProjectConfiguration( new MavenUpdateRequest(
                                        iProject, mavenConfiguration.isOffline(), true ), monitor );
                                }
                                catch( Exception e )
                                {
                                    LiferayMavenCore.logError( "Unable to update configuration for " + project.getName(), e );
                                }
                            }
                        }

                    }
                    else
                    {
                        retval = LiferayMavenCore.createErrorStatus( "Unable to import this Project " );
                    }

                }
            }
            catch( IOException e1 )
            {
                retval = LiferayMavenCore.createErrorStatus( "Unable to create this Project " );
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    private void setBasedirRename( MavenProjectInfo projectInfo ) throws IOException
    {
        File workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
        File basedir = projectInfo.getPomFile().getParentFile().getCanonicalFile();

        projectInfo.setBasedirRename( basedir.getParentFile().equals( workspaceRoot )
            ? MavenProjectInfo.RENAME_REQUIRED : MavenProjectInfo.RENAME_NO );
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        List<T> retval = null;

        if( "profileIds".equals( key ) )
        {
            final List<T> profileIds = new ArrayList<T>();

            try
            {
                final List<Profile> profiles = MavenPlugin.getMaven().getSettings().getProfiles();

                for( final Profile profile : profiles )
                {
                    if( profile.getActivation() != null )
                    {
                        if( profile.getActivation().isActiveByDefault() )
                        {
                            continue;
                        }
                    }

                    profileIds.add( type.cast( profile.getId() ) );
                }

                if( params[0] != null && params[0] instanceof File )
                {
                    final File locationDir = (File) params[0];

                    File pomFile = new File( locationDir, IMavenConstants.POM_FILE_NAME );

                    if( !pomFile.exists() && locationDir.getParentFile().exists() )
                    {
                        // try one level up for when user is adding new module
                        pomFile = new File( locationDir.getParentFile(), IMavenConstants.POM_FILE_NAME );
                    }

                    if( pomFile.exists() )
                    {
                        final IMaven maven = MavenPlugin.getMaven();

                        Model model = maven.readModel( pomFile );

                        File parentDir = pomFile.getParentFile();

                        while( model != null )
                        {
                            for( org.apache.maven.model.Profile p : model.getProfiles() )
                            {
                                profileIds.add( type.cast( p.getId() ) );
                            }

                            parentDir = parentDir.getParentFile();

                            if( parentDir != null && parentDir.exists() )
                            {
                                try
                                {
                                    model = maven.readModel( new File( parentDir, IMavenConstants.POM_FILE_NAME ) );
                                }
                                catch( Exception e )
                                {
                                    model = null;
                                }
                            }
                        }
                    }
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError( e );
            }

            retval = profileIds;
        }
        else if( "liferayVersions".equals( key ) )
        {
            final List<T> possibleVersions = new ArrayList<T>();

            final RepositorySystem system = AetherUtil.newRepositorySystem();

            final RepositorySystemSession session = AetherUtil.newRepositorySystemSession( system );

            final String groupId = params[0].toString();
            final String artifactId = params[1].toString();

            final String coords = groupId + ":" + artifactId + ":[6,)";

            final Artifact artifact = new DefaultArtifact( coords );

            final VersionRangeRequest rangeRequest = new VersionRangeRequest();
            rangeRequest.setArtifact( artifact );
            rangeRequest.addRepository( AetherUtil.newCentralRepository() );
            rangeRequest.addRepository( AetherUtil.newLiferayRepository() );

            try
            {
                final VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );

                final List<Version> versions = rangeResult.getVersions();

                for( Version version : versions )
                {
                    final String val = version.toString();

                    if( !"6.2.0".equals( val ) )
                    {
                        possibleVersions.add( type.cast( val ) );
                    }
                }

                retval = possibleVersions;
            }
            catch( VersionRangeResolutionException e )
            {
            }
        }
        else if( "parentVersion".equals( key ) )
        {
            final List<T> version = new ArrayList<T>();

            final File locationDir = (File) params[0];

            final File parentPom = new File( locationDir, IMavenConstants.POM_FILE_NAME );

            if( parentPom.exists() )
            {
                try
                {
                    final IMaven maven = MavenPlugin.getMaven();

                    final Model model = maven.readModel( parentPom );

                    version.add( type.cast( model.getVersion() ) );

                    retval = version;
                }
                catch( CoreException e )
                {
                    LiferayMavenCore.logError( "unable to get parent version", e );
                }
            }
        }
        else if( "parentGroupId".equals( key ) )
        {
            final List<T> groupId = new ArrayList<T>();

            final File locationDir = (File) params[0];

            final File parentPom = new File( locationDir, IMavenConstants.POM_FILE_NAME );

            if( parentPom.exists() )
            {
                try
                {
                    final IMaven maven = MavenPlugin.getMaven();

                    final Model model = maven.readModel( parentPom );

                    groupId.add( type.cast( model.getGroupId() ) );

                    retval = groupId;
                }
                catch( CoreException e )
                {
                    LiferayMavenCore.logError( "unable to get parent groupId", e );
                }
            }
        }
        else if( "archetypeGAV".equals( key ) )
        {
            final String frameworkType = (String) params[0];

            final String value = LiferayMavenCore.getPreferenceString( "archetype-gav-" + frameworkType, "" );

            retval = Collections.singletonList( type.cast( value ) );
        }

        return retval;
    }

    @Override
    public ILiferayProject provide( Object adaptable )
    {
        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( MavenUtil.isMavenProject( project ) )
                {
                    if( LiferayNature.hasNature( project ) )
                    {
                        return new MavenBundlePluginProject( project );
                    }
                    else if( ComponentUtil.hasLiferayFacet( project ) )
                    {
                        return new FacetedMavenBundleProject( project );
                    }
                    else
                    {
                        // return dummy maven project that can't lookup docroot resources
                        return new LiferayMavenProject( project )
                        {

                            @Override
                            public IFile getDescriptorFile( String name )
                            {
                                return null;
                            }
                        };
                    }
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError(
                    "Unable to create ILiferayProject from maven project " + project.getName(), e );
            }
        }

        return null;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;
        // if the path is a folder and it has a pom.xml that is a package type of 'pom' then this is a valid location
        // if projectName is null or empty , don't need to check , just return
        if( CoreUtil.isNullOrEmpty( projectName ) )
            return retval;

        final File dir = path.toFile();

        if( dir.exists() )
        {
            final File pomFile = path.append( IMavenConstants.POM_FILE_NAME ).toFile();

            if( pomFile.exists() )
            {
                final IMaven maven = MavenPlugin.getMaven();

                try
                {
                    final Model result = maven.readModel( pomFile );

                    if( !"pom".equals( result.getPackaging() ) )
                    {
                        retval =
                            LiferayMavenCore.createErrorStatus( "\"" + pomFile.getParent() +
                                "\" contains a non-parent maven project." );
                    }
                    else
                    {
                        final String name = result.getName();

                        if( projectName.equals( name ) )
                        {
                            retval =
                                LiferayMavenCore.createErrorStatus( "The project name \"" + projectName +
                                    "\" can't be the same as the parent." );
                        }
                        else
                        {
                            final IPath newProjectPath = path.append( projectName );

                            retval = validateProjectLocation( projectName, newProjectPath );
                        }
                    }
                }
                catch( CoreException e )
                {
                    retval = LiferayMavenCore.createErrorStatus( "Invalid project location.", e );
                    LiferayMavenCore.log( retval );
                }
            }
            else
            {
                final File[] files = dir.listFiles();

                if( files.length > 0 )
                {
                    retval = LiferayMavenCore.createErrorStatus( "Project location is not empty or a parent pom." );
                }
            }
        }

        return retval;
    }

}
