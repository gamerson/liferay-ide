/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION;
import static com.liferay.ide.maven.core.LiferayMavenUtil.hookPluginDependencies;
import static com.liferay.ide.maven.core.LiferayMavenUtil.javaeeDependencies;
import static com.liferay.ide.maven.core.LiferayMavenUtil.portletPluginDependencies;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.HookClasspathContainer;
import com.liferay.ide.project.core.PortletClasspathContainer;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant;

/**
 * @author Kamesh Sampath
 */
public class LiferayProjectConverter extends AbstractProjectConversionParticipant
{

    @Override
    public boolean accept( IProject project ) throws CoreException
    {
        return ProjectUtil.isLiferayFacetedProject( project );
    }

    @Override
    public void convert( IProject project, Model model, IProgressMonitor monitor ) throws CoreException
    {
        if( ProjectUtil.isLiferayFacetedProject( project ) )
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );
            ILiferayProject liferayProject = LiferayCore.create( project );
            Properties liferayProperties = getLiferayProperties( liferayRuntime, liferayProject );

            // Add the dependencies only if its a portlet/hook/ext
            if( ProjectUtil.isExtProject( project ) || ProjectUtil.isPortletProject( project ) ||
                ProjectUtil.isHookProject( project ) )
            {
                addDependencies( project, liferayProperties.getProperty( PLUGIN_CONFIG_LIFERAY_VERSION ), model );
            }

            /*
             * This will add the common Liferya maven plugin properrties like liferay.version, autoDeploydir etc.,
             */
            LiferayMavenUtil.addLiferayMavenProperties( liferayProperties, model );

            Build build = LiferayMavenUtil.addLiferayMavenPlugin( model, project, liferayProperties );

            if( build != null )
            {
                model.setBuild( build );
            }

            removeStaleClasspathEntires( project, liferayRuntime.getRuntime().getName() );
        }

    }

    private void addDependencies( IProject project, String portalVersion, Model model )
    {
        Set<Dependency> projectDependencySet = new HashSet<Dependency>();

        List<Dependency> existingDependencies = model.getDependencies();
        projectDependencySet.addAll( existingDependencies );
        projectDependencySet.addAll( javaeeDependencies( model ) );

        if( ProjectUtil.isPortletProject( project ) )
        {
            projectDependencySet.addAll( portletPluginDependencies( portalVersion, model ) );
        }
        else if( ProjectUtil.isHookProject( project ) )
        {
            projectDependencySet.addAll( hookPluginDependencies( portalVersion, model ) );
        }
        else if( ProjectUtil.isExtProject( project ) )
        {
            // TODO add new classpath container, currently not supported
        }

        List<Dependency> projectDependencies = new ArrayList<Dependency>();
        projectDependencies.addAll( projectDependencySet );
        model.setDependencies( projectDependencies );
    }

    private Properties getLiferayProperties( ILiferayRuntime liferayRuntime, ILiferayProject liferayProject )
    {
        Properties liferayProperties = new Properties();
        String portalVersion = liferayProject.getPortalVersion();
        liferayProperties.put( PLUGIN_CONFIG_LIFERAY_VERSION, portalVersion );
        String autoDeployDir = liferayRuntime.getRuntimeLocation().append( ".." ).append( "deploy" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
        liferayProperties.put( PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR, autoDeployDir );
        String appServerPortalDir = liferayRuntime.getAppServerPortalDir().toOSString();
        liferayProperties.put( PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR, appServerPortalDir );
        String appServerDeployDir = liferayRuntime.getAppServerDeployDir().toOSString();
        liferayProperties.put( PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR, appServerDeployDir );
        String appServerGlobalLibDir = liferayRuntime.getAppServerLibGlobalDir().toOSString();
        liferayProperties.put( PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR, appServerGlobalLibDir );
        String portalLibDir = liferayRuntime.getAppServerPortalDir().append( "WEB-INF" ).append( "lib" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
        liferayProperties.put( PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR, portalLibDir );
        String appServerPortalTldDir =
            liferayRuntime.getAppServerPortalDir().append( "WEB-INF" ).append( "tld" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
        liferayProperties.put( PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR, appServerPortalTldDir );
        return liferayProperties;
    }

    private void removeStaleClasspathEntires( final IProject project, String runtimeName )
    {
        IJavaProject javaProject = JavaCore.create( project );

        try
        {
            IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
            List<IClasspathEntry> newCpEntries = new LinkedList<IClasspathEntry>();

            for( IClasspathEntry classpathEntry : classpathEntries )
            {
                if( classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
                {
                    String containerLastSegment = classpathEntry.getPath().lastSegment();
                    String containerName = null;
                    if( ProjectUtil.isHookProject( project ) )
                    {
                        containerName = HookClasspathContainer.SEGMENT_PATH;
                    }
                    else if( ProjectUtil.isPortletProject( project ) )
                    {
                        containerName = PortletClasspathContainer.SEGMENT_PATH;
                    }

                    if( !( containerLastSegment != null && ( containerLastSegment.equals( containerName ) || containerLastSegment.equals( runtimeName ) ) ) )
                    {
                        newCpEntries.add( classpathEntry );
                    }
                }
                else
                {
                    newCpEntries.add( classpathEntry );
                }
            }

            javaProject.setRawClasspath(
                newCpEntries.toArray( new IClasspathEntry[newCpEntries.size()] ), new NullProgressMonitor() );
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( e );
        }

    }

}
