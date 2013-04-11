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

import static com.liferay.ide.maven.core.ILiferayMavenConstants.*;
import static com.liferay.ide.project.core.facet.IPluginFacetConstants.LIFERAY_EXT_FACET_ID;
import static com.liferay.ide.project.core.facet.IPluginFacetConstants.LIFERAY_HOOK_FACET_ID;
import static com.liferay.ide.project.core.facet.IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID;
import static com.liferay.ide.project.core.facet.IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID;
import static com.liferay.ide.project.core.facet.IPluginFacetConstants.LIFERAY_THEME_FACET_ID;

import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;

/**
 * @author Gregory Amerson
 */
public class LiferayMavenUtil
{

   
    public static Plugin getLiferayMavenPlugin( MavenProject mavenProject )
    {
        Plugin retval = null;

        if( mavenProject != null )
        {
            retval = mavenProject.getPlugin( LIFERAY_MAVEN_PLUGIN_KEY );
        }

        return retval;
    }

    public static Xpp3Dom getLiferayMavenPluginConfig( MavenProject mavenProject )
    {
        Xpp3Dom retval = null;

        if( mavenProject != null )
        {
            final Plugin plugin = mavenProject.getPlugin( LIFERAY_MAVEN_PLUGIN_KEY );

            if( plugin != null )
            {
                retval = (Xpp3Dom) plugin.getConfiguration();
            }
        }

        return retval;
    }

    public static String getLiferayMavenPluginConfig( MavenProject mavenProject, String childElement )
    {
        String retval = null;

        Xpp3Dom liferayMavenPluginConfig = getLiferayMavenPluginConfig( mavenProject );

        if( liferayMavenPluginConfig != null )
        {
            final Xpp3Dom childNode = liferayMavenPluginConfig.getChild( childElement );

            if( childNode != null )
            {
                retval = childNode.getValue();
            }
        }

        return retval;
    }

    public static List<Dependency> hookPluginDependencies( String portalVersion, Model model )
    {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        // portal-service
        Dependency dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( PORTAL_SERVICE_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        // util-java
        dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( UTIL_JAVA_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        return dependencies;

    }

    public static List<Dependency> portletPluginDependencies( String portalVersion, Model model )
    {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        // portal-service
        Dependency dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( PORTAL_SERVICE_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        // util-java
        dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( UTIL_JAVA_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        // util-bridges
        dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( UTIL_BRIDGES_ARTIFACT_ID );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependency.setScope( PROVIDED_SCOPE );
        dependencies.add( dependency );

        // util-taglib
        dependency = new Dependency();
        dependency.setGroupId( LIFERAY_GROUP_ID );
        dependency.setArtifactId( UTIL_TAGLIB_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${liferay.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        return dependencies;
    }

    // TODO Portlet JSF/ICEFACES/RICHFACES/LIFERAYFACES/PRIMEFACES dependencies

    public static List<Dependency> javaeeDependencies( Model model )
    {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        // jstl.jar
        Dependency dependency = new Dependency();
        dependency.setGroupId( JSP_JSTL_GROUP_ID );
        dependency.setArtifactId( JSTL_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${jstl.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        model.addProperty( "jstl.version", JSTL_VERSION ); //$NON-NLS-1$ 

        // jsp-api
        dependency = new Dependency();
        dependency.setGroupId( JSP_API_GROUP_ID );
        dependency.setArtifactId( JSP_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${jsp.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        model.addProperty( "jsp.api.version", JSP_API_VERSION ); //$NON-NLS-1$ 

        // servlet-api
        dependency = new Dependency();
        dependency.setGroupId( SERVLET_API_GROUP_ID );
        dependency.setArtifactId( SERVLET_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${servlet.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        model.addProperty( "servlet.api.version", SERVLET_API_VERSION ); //$NON-NLS-1$

        // Portlet
        dependency = new Dependency();
        dependency.setGroupId( PORTLET_API_GROUP_ID );
        dependency.setArtifactId( PORTLET_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${portlet.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );

        model.addProperty( "portlet.api.version", PORTLET_API_VERSION ); //$NON-NLS-1$

        return dependencies;
    }

    public static Build addLiferayMavenPlugin( Model model, IProject project, Properties liferayProperties )
    {
        Build build = model.getBuild();

        if( build == null )
        {
            build = new Build();
        }

        Plugin plugin =
            build.getPluginsAsMap().get(
                getQualifiedArtifactId( LIFERAY_MAVEN_PLUGINS_GROUP_ID, MAVEN_GROUP_ARTIFACT_SEPERATOR +
                    LIFERAY_MAVEN_PLUGIN_KEY ) );

        if( plugin == null )
        {
            build.flushPluginMap();

            plugin = new Plugin();
            plugin.setGroupId( LIFERAY_MAVEN_PLUGINS_GROUP_ID );
            plugin.setArtifactId( LIFERAY_MAVEN_PLUGIN_KEY );
            plugin.setVersion( "${liferay.version}" ); //$NON-NLS-1$

            Map<String, String> configuration = new HashMap<String, String>();
            configuration.put( PLUGIN_CONFIG_LIFERAY_VERSION, "${liferay.version}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR, "${appserver.deploy.dir}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR, "${appserver.lib.global.dir}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR, "${appserver.portal.lib.dir}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR, "${appserver.portal.dir}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR, "${appserver.portal.tld.dir}" ); //$NON-NLS-1$
            configuration.put( PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR, "${liferay.auto.deploy.dir}" ); //$NON-NLS-1$

            if( ProjectUtil.hasFacet( project, LIFERAY_PORTLET_FACET_ID ) )
            {
                configuration.put( PLUGIN_CONFIG_PLUGIN_TYPE, DEFAULT_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, LIFERAY_EXT_FACET_ID ) )
            {
                configuration.put( ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, EXT_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, LIFERAY_HOOK_FACET_ID ) )
            {
                configuration.put( ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, HOOK_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, LIFERAY_LAYOUTTPL_FACET_ID ) )
            {
                configuration.put( ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, LAYOUTTPL_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, LIFERAY_THEME_FACET_ID ) )
            {
                configuration.put( PLUGIN_CONFIG_PLUGIN_TYPE, THEME_PLUGIN_TYPE );
                configuration.put( PARENT_THEME, "${liferay.theme.parent}" ); //$NON-NLS-1$ 
                model.addProperty( MAVEN_PROP_LIFERAY_THEME_PARENT, "${liferay.theme.parent}" ); //$NON-NLS-1$ 
                configuration.put( THEME_TYPE, "${liferay.theme.type}" ); //$NON-NLS-1$ 
                model.addProperty( MAVEN_PROP_LIFERAY_THEME_TYPE, "${liferay.theme.type}" ); //$NON-NLS-1$2$
            }

            plugin = LiferayMavenUtil.configurePlugin( plugin, configuration );
            build.addPlugin( plugin );
        }
        else
        {
            return build;
        }

        return build;
    }

    public static Plugin configurePlugin( Plugin plugin, Map<String, String> configuration )
    {

        Xpp3Dom pluginConfig = (Xpp3Dom) plugin.getConfiguration();
        if( pluginConfig == null )
        {
            pluginConfig = new Xpp3Dom( MAVEN_PLUGIN_CONFIG_KEY );
            plugin.setConfiguration( pluginConfig );
        }

        Set<String> keys = configuration.keySet();
        for( String key : keys )
        {
            String configValue = configuration.get( key );
            Xpp3Dom configKey = pluginConfig.getChild( key );
            if( configKey == null )
            {
                configKey = new Xpp3Dom( key );
                pluginConfig.addChild( configKey );
            }
            configKey.setValue( configValue );
        }

        return plugin;
    }

    public static Object getQualifiedArtifactId( String groupId, String artifactId )
    {

        return groupId + ":" + artifactId; //$NON-NLS-1$
    }

    public static void addLiferayMavenProperties( Properties liferayProperties, Model model )
    {
        model.addProperty( MAVEN_PROP_LIFERAY_VERSION, liferayProperties.getProperty( PLUGIN_CONFIG_LIFERAY_VERSION ) );
        model.addProperty(
            MAVEN_PROP_LIFERAY_AUTO_DEPLOY_DIR, liferayProperties.getProperty( PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR ) );
        model.addProperty(
            MAVEN_PROP_APPSERVER_DEPLOY_DIR, liferayProperties.getProperty( PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR ) );
        model.addProperty(
            MAVEN_PROP_APPSERVER_LIB_GLOBAL_DIR,
            liferayProperties.getProperty( PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR ) );
        model.addProperty(
            MAVEN_PROP_APPSERVER_PORTAL_LIB_DIR,
            liferayProperties.getProperty( PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR ) );
        model.addProperty(
            MAVEN_PROP_APPSERVER_PORTAL_DIR, liferayProperties.getProperty( PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR ) );
        model.addProperty(
            MAVEN_PROP_APPSERVER_PORTAL_TLD_DIR,
            liferayProperties.getProperty( PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR ) );
    }
}
