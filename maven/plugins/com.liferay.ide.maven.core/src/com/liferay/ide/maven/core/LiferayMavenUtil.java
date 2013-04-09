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

import static com.liferay.ide.maven.core.ILiferayMavenConstants.JSP_API_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.JSP_API_GROUP_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.JSTL_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.LIFERAY_GROUP_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PORTAL_SERVICE_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PORTLET_API_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PORTLET_API_GROUP_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.PROVIDED_SCOPE;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.SERVLET_API_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.SERVLET_API_GROUP_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.UTIL_BRIDGES_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.UTIL_JAVA_ARTIFACT_ID;
import static com.liferay.ide.maven.core.ILiferayMavenConstants.UTIL_TAGLIB_ARTIFACT_ID;

import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<Dependency> liferayDependencies(Model model)
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
        
        model.addProperty( "liferay.version", "${liferay.version}" ); //$NON-NLS-1$ //$NON-NLS-2$

        //jstl.jar
        dependency = new Dependency();
        dependency.setGroupId( JSP_API_GROUP_ID );
        dependency.setArtifactId( JSTL_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${jstl.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );
        
        model.addProperty( "jstl.version", "${jstl.version}" ); //$NON-NLS-1$ //$NON-NLS-2$

        //jsp-api
        dependency = new Dependency();
        dependency.setGroupId( JSP_API_GROUP_ID );
        dependency.setArtifactId( JSP_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${jsp.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );
        
        model.addProperty( "jsp.api.version", "${jsp.api.version}" ); //$NON-NLS-1$ //$NON-NLS-2$

        // servlet-api
        dependency = new Dependency();
        dependency.setGroupId( SERVLET_API_GROUP_ID );
        dependency.setArtifactId( SERVLET_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${servlet.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );
        
        model.addProperty( "servlet.api.version", "${servlet.api.version}" ); //$NON-NLS-1$ //$NON-NLS-2$

        // Portlet
        dependency = new Dependency();
        dependency.setGroupId( PORTLET_API_GROUP_ID );
        dependency.setArtifactId( PORTLET_API_ARTIFACT_ID );
        dependency.setScope( PROVIDED_SCOPE );
        dependency.setVersion( "${portlet.api.version}" ); //$NON-NLS-1$
        dependencies.add( dependency );
        
        model.addProperty( "portlet.api.version", "${portlet.api.version}" ); //$NON-NLS-1$ //$NON-NLS-2$

        return dependencies;
    }

    /**
     * @param project
     * @return
     */
    public static Build addLiferayMavenPlugin( Model model, IProject project )
    {
        Build build = model.getBuild();

        if( build == null )
        {
            build = new Build();
        }

        Plugin plugin =
            build.getPluginsAsMap().get(
                LiferayMavenUtil.getQualifiedArtifactId(
                    ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID,
                    ":" + ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ID ) ); //$NON-NLS-1$

        if( plugin == null )
        {
            build.flushPluginMap();

            plugin = new Plugin();
            plugin.setGroupId( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID );
            plugin.setArtifactId( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ID );
            plugin.setVersion( "${liferay.version}" ); //$NON-NLS-1$
           

            Map<String, String> configuration = new HashMap<String, String>();
            configuration.put( ILiferayMavenConstants.PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR, "${liferay.auto.deploy.dir}" ); //$NON-NLS-1$
            model.addProperty( "liferay.auto.deploy.dir", "${liferay.auto.deploy.dir}" ); //$NON-NLS-1$ //$NON-NLS-2$

            if( ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID ) )
            {
                configuration.put(
                    ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, ILiferayMavenConstants.MAVEN_PORTLET_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_EXT_FACET_ID ) )
            {
                configuration.put(
                    ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, ILiferayMavenConstants.MAVEN_EXT_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_HOOK_FACET_ID ) )
            {
                configuration.put(
                    ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, ILiferayMavenConstants.MAVEN_HOOK_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID ) )
            {
                configuration.put(
                    ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, ILiferayMavenConstants.MAVEN_LAYOUT_PLUGIN_TYPE );
            }
            else if( ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_THEME_FACET_ID ) )
            {
                configuration.put(
                    ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE, ILiferayMavenConstants.MAVEN_THEME_PLUGIN_TYPE );
                configuration.put( ILiferayMavenConstants.PARENT_THEME, "${liferay.theme.parent}" ); //$NON-NLS-1$ 
                model.addProperty( "liferay.theme.parent", "${liferay.theme.parent}" ); //$NON-NLS-1$ //$NON-NLS-2$
                configuration.put( ILiferayMavenConstants.THEME_TYPE, "${liferay.theme.type}" ); //$NON-NLS-1$ 
                model.addProperty( "liferay.theme.type", "${liferay.theme.type}" ); //$NON-NLS-1$ //$NON-NLS-2$
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

    /**
     * @param plugin
     * @param configuration
     */
    public static Plugin configurePlugin( Plugin plugin, Map<String, String> configuration )
    {

        Xpp3Dom pluginConfig = (Xpp3Dom) plugin.getConfiguration();
        if( pluginConfig == null )
        {
            pluginConfig = new Xpp3Dom( ILiferayMavenConstants.MAVEN_PLUGIN_CONFIG_KEY );
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

    /**
     * @param groupId
     * @param artifactId
     * @return
     */
    public static Object getQualifiedArtifactId( String groupId, String artifactId )
    {

        return groupId + ":" + artifactId; //$NON-NLS-1$
    }

}
