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

package com.liferay.ide.maven.core.internal.converters;

import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.maven.core.LiferayMavenUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant;

/**
 * @author kamesh.sampath
 */
public class LiferayProjectConverter extends AbstractProjectConversionParticipant
{

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant#accept(org.eclipse.core.resources
     * .IProject)
     */
    @Override
    public boolean accept( IProject project ) throws CoreException
    {

        return project != null && ProjectUtil.isLiferayFacetedProject( project );

    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant#convert(org.eclipse.core.resources
     * .IProject, org.apache.maven.model.Model, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void convert( IProject iProject, Model model, IProgressMonitor monitor ) throws CoreException
    {
        if( iProject != null && ProjectUtil.isLiferayFacetedProject( iProject ) )
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( iProject );
            ILiferayProject liferayProject = LiferayCore.create( iProject );
            Properties liferayProperties = getLiferayProperties( liferayRuntime, liferayProject );

            // Add the dependencies only if its a portlet/hook/ext
            if( ProjectUtil.isExtProject( iProject ) || ProjectUtil.isPortletProject( iProject ) ||
                ProjectUtil.isHookProject( iProject ) )
            {

                addDependencies( liferayProperties.getProperty( PLUGIN_CONFIG_LIFERAY_VERSION ), model );
            }

            /*
             * This will add the common Liferya maven plugin properrties like liferay.version, autoDeploydir etc.,
             */
            LiferayMavenUtil.addLiferayMavenProperties( liferayProperties, model );

            Build build = LiferayMavenUtil.addLiferayMavenPlugin( model, iProject, liferayProperties );

            model.setBuild( build );

            removeStaleClasspathEntires( iProject );
        }

    }

    private void removeStaleClasspathEntires( IProject iProject )
    {
        // TODO Auto-generated method stub
        
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

    private void addDependencies( String portalVersion, Model model )
    {
        List<Dependency> existingDependencies = model.getDependencies();
        List<Dependency> liferayProjectDependencies = LiferayMavenUtil.liferayDependencies( portalVersion, model );
        liferayProjectDependencies.removeAll( existingDependencies );
        model.setDependencies( liferayProjectDependencies );
    }

}
