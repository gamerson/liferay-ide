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

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.eclipse.m2e.jdt.MavenJdtPlugin;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class LiferayMavenProject extends BaseLiferayProject  implements IMavenProject
{

    public LiferayMavenProject( IProject project )
    {
        super( project );
    }

    @Override
	public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( getProject(), new NullProgressMonitor() );

        if( facade != null )
        {
            if( IProjectBuilder.class.equals( adapterType ) )
            {
                final IProjectBuilder projectBuilder = new MavenProjectBuilder( getProject() );

                return adapterType.cast( projectBuilder );
            }
            else if( IRemoteServerPublisher.class.equals( adapterType ) )
            {
                final IRemoteServerPublisher remoteServerPublisher =
                    new MavenProjectRemoteServerPublisher( getProject() );

                return adapterType.cast( remoteServerPublisher );
            }
        }

        return null;
    }

    @Override
	public IPath getLibraryPath( String filename )
    {
        final IPath[] libs = getUserLibs();

        if( ! CoreUtil.isNullOrEmpty( libs ) )
        {
            for( IPath lib : libs )
            {
                if( lib.removeFileExtension().lastSegment().startsWith( filename ) )
                {
                    return lib;
                }
            }
        }

        return null;
    }

    public String getLiferayMavenPluginVersion()
    {
        String retval = null;

        final IMavenProjectFacade projectFacade = MavenPlugin.getMavenProjectRegistry().getProject( getProject() );

        if( projectFacade != null )
        {
            try
            {
                final NullProgressMonitor npm = new NullProgressMonitor();

                final MavenProject mavenProject = projectFacade.getMavenProject( npm );

                if( mavenProject != null )
                {
                    final Plugin liferayMavenPlugin =
                        MavenUtil.getPlugin( projectFacade, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, npm );

                    retval = liferayMavenPlugin.getVersion();
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
	public String getProperty( String key, String defaultValue )
    {
        String retval = defaultValue;

        if( ( "theme.type".equals( key ) || "theme.parent".equals( key ) ) &&
            ProjectUtil.isThemeProject( getProject() ) )
        {
            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject() );

            if( projectFacade != null )
            {
                final MavenProject mavenProject = projectFacade.getMavenProject();

                if( "theme.type".equals( key ) )
                {
                    retval =
                        MavenUtil.getLiferayMavenPluginConfig(
                            mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_THEME_TYPE );
                }
                else
                {
                    retval =
                        MavenUtil.getLiferayMavenPluginConfig(
                            mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_PARENT_THEME );
                }
            }
        }

        return retval;
    }

    @Override
	public IFolder getSourceFolder( String classification )
    {
        IFolder retval = super.getSourceFolder( classification );

        final List<IFolder> sourceFolders = CoreUtil.getSourceFolders( JavaCore.create( getProject() ) );

        for( IFolder folder : sourceFolders )
        {
            if( folder.getName().equals( classification ) )
            {
                retval = folder;

                break;
            }
        }

        return retval;
    }

    @Override
	public IPath[] getUserLibs()
    {
        final List<IPath> libs = new ArrayList<IPath>();

        final IClasspathManager buildPathManager = MavenJdtPlugin.getDefault().getBuildpathManager();

        try
        {
            final IClasspathEntry[] classpath =
                buildPathManager.getClasspath(
                    getProject(), IClasspathManager.CLASSPATH_RUNTIME, true, new NullProgressMonitor() );

            for( IClasspathEntry entry : classpath )
            {
                libs.add( entry.getPath() );
            }
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( "Unable to get maven classpath.", e ); //$NON-NLS-1$
        }

        return libs.toArray( new IPath[0] );
    }

}
