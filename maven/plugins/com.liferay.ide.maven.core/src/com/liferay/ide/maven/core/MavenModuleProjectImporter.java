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

import com.liferay.ide.core.AbstractLiferayProjectImporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.MavenProjectInfo;

/**
 * @author Andy Wu
 */
public class MavenModuleProjectImporter extends AbstractLiferayProjectImporter
{
    private void addConfiguration( String name, String value, Xpp3Dom configuration )
    {
        Xpp3Dom conf = new Xpp3Dom( name );
        conf.setValue( value );
        configuration.addChild( conf );
    }

    private void addDependency( List<Dependency> dependencyList, Dependency addDependency )
    {
        String addGroupId = addDependency.getGroupId();
        String addArtifactId = addDependency.getArtifactId();
        String addVersion = addDependency.getVersion();

        boolean existed = false;

        for( Dependency dependency : dependencyList )
        {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String version = dependency.getVersion();

            if( addGroupId.equals( groupId ) && addArtifactId.equals( artifactId ) && addVersion.equals( version ) )
            {
                existed = true;
                break;
            }
        }

        if( !existed )
        {
            dependencyList.add( addDependency );
        }
    }

    @Override
    public IStatus canImport( String location )
    {
        IStatus retval = null;

        File pom = new File( location, "pom.xml" );

        if( pom.exists() )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public List<IProject> importProjects( String location, IProgressMonitor monitor ) throws CoreException
    {
        List<IProject> projects = new ArrayList<>();

        try
        {
            List<IMavenProjectImportResult> results = MavenUtil.importProject( location, monitor );

            for( IMavenProjectImportResult result : results )
            {
                projects.add( result.getProject() );
            }
        }
        catch( InterruptedException e )
        {
        }

        return projects;
    }

    @Override
    public void updateBuildFile( String location ) throws Exception
    {
        List<MavenProjectInfo> projects = MavenUtil.scanMavenProjects( location, new NullProgressMonitor() );

        for( MavenProjectInfo mavenProjectInfo : projects )
        {
            updatePomFile( mavenProjectInfo.getPomFile() );
        }
    }

    private void updatePomFile( File pomFile ) throws Exception
    {
        try
        {
            MavenXpp3Reader mavenreader = new MavenXpp3Reader();
            FileReader reader = new FileReader( pomFile.getAbsolutePath() );

            Model model = mavenreader.read( reader );

            String[][] dependenciesConvertMap = new String[5][];

            dependenciesConvertMap[0] = new String[] { "portal-service", "com.liferay.portal.kernel", "2.6.0" };
            dependenciesConvertMap[1] = new String[] { "util-java", "com.liferay.util.java", "2.2.2" };
            dependenciesConvertMap[2] = new String[] { "util-bridges", "com.liferay.util.bridges", "2.0.0" };
            dependenciesConvertMap[3] = new String[] { "util-taglib", "com.liferay.util.taglib", "2.0.0" };
            dependenciesConvertMap[4] = new String[] { "util-slf4j", "com.liferay.util.slf4j", "1.0.0" };

            // remove all profiles
            model.setProfiles( null );

            // convert dependencies
            List<Dependency> existedDependencies = model.getDependencies();

            List<Dependency> newDependencies = new ArrayList<Dependency>();

            for( Dependency existedDependency : existedDependencies )
            {
                String artifaceId = existedDependency.getArtifactId();

                for( String[] str : dependenciesConvertMap )
                {
                    if( artifaceId.equals( str[0] ) )
                    {
                        existedDependency.setArtifactId( str[1] );
                        existedDependency.setVersion( str[2] );

                        continue;
                    }
                }

                newDependencies.add( existedDependency );
            }

            Dependency portalKernel = new Dependency();
            portalKernel.setGroupId( "com.liferay.portal" );
            portalKernel.setArtifactId( "com.liferay.portal.kernel" );
            portalKernel.setVersion( "2.6.0" );

            Dependency utilJava = new Dependency();
            utilJava.setGroupId( "com.liferay.portal" );
            utilJava.setArtifactId( "com.liferay.util.java" );
            utilJava.setVersion( "2.2.2" );

            Dependency bndAnnotation = new Dependency();
            bndAnnotation.setGroupId( "biz.aQute.bnd" );
            bndAnnotation.setArtifactId( "biz.aQute.bnd.annotation" );
            bndAnnotation.setVersion( "3.2.0" );

            String packaging = model.getPackaging();

            if( packaging.equals( "war" ) )
            {
                addDependency( newDependencies, portalKernel );
                addDependency( newDependencies, utilJava );
                addDependency( newDependencies, bndAnnotation );
            }
            else if( packaging.equals( "jar" ) )
            {
                addDependency( newDependencies, portalKernel );
                addDependency( newDependencies, bndAnnotation );
            }

            model.setDependencies( newDependencies );

            List<Plugin> newPlugins = new ArrayList<Plugin>();

            // remove liferay-maven-plugin build
            Build build = model.getBuild();

            if( build != null )
            {
                List<Plugin> plugins = build.getPlugins();

                for( Plugin plugin : plugins )
                {
                    if( plugin.getArtifactId().equals( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID ) )
                    {
                        continue;
                    }

                    newPlugins.add( plugin );
                }

                if( packaging.equals( "war" ) )
                {
                    Plugin sbPlugin = new Plugin();
                    sbPlugin.setGroupId( "com.liferay" );
                    sbPlugin.setArtifactId( ILiferayMavenConstants.SERVICE_BUILDER_PLUGIN_ARTIFACT_ID );
                    sbPlugin.setVersion( "1.0.142" );

                    Xpp3Dom sbConfiguration = new Xpp3Dom( "configuration" );

                    addConfiguration( "apiDirName", "../" + model.getArtifactId() + "-service/src/main/java",
                        sbConfiguration );
                    addConfiguration( "autoNamespaceTables", "true", sbConfiguration );
                    addConfiguration( "buildNumberIncrement", "true", sbConfiguration );
                    addConfiguration( "hbmFileName", "src/main/resources/META-INF/portlet-hbm.xml", sbConfiguration );
                    addConfiguration( "implDirName", "src/main/java", sbConfiguration );
                    addConfiguration( "inputFileName", "src/main/webapp/WEB-INF/service.xml", sbConfiguration );
                    addConfiguration( "modelHintsFileName", "src/main/resources/META-INF/portlet-model-hints.xml",
                        sbConfiguration );
                    addConfiguration( "osgiModule", "false", sbConfiguration );
                    addConfiguration( "pluginName", model.getArtifactId(), sbConfiguration );
                    addConfiguration( "propsUtil", "com.liferay.util.service.ServiceProps", sbConfiguration );
                    addConfiguration( "resourcesDirName", "src/main/resources", sbConfiguration );
                    addConfiguration( "springNamespaces", "beans", sbConfiguration );
                    addConfiguration( "springFileName", "src/main/resources/META-INF/portlet-spring.xml",
                        sbConfiguration );
                    addConfiguration( "sqlDirName", "src/main/webapp/WEB-INF/sql", sbConfiguration );
                    addConfiguration( "sqlFileName", "tables.sql", sbConfiguration );

                    sbPlugin.setConfiguration( sbConfiguration );

                    newPlugins.add( sbPlugin );

                    /*
                     * Plugin buildCssPlugin = new Plugin(); buildCssPlugin.setGroupId( "com.liferay" );
                     * buildCssPlugin.setArtifactId( "com.liferay.css.builder" ); buildCssPlugin.setVersion( "1.0.17" );
                     * List<PluginExecution> executions = new ArrayList<PluginExecution>(); PluginExecution
                     * buildCssexecution = new PluginExecution(); buildCssexecution.setId( "default-build-css" );
                     * buildCssexecution.setPhase( "generate-sources" ); List<String> goals = new ArrayList<String>();
                     * goals.add( "build-css" ); buildCssexecution.setGoals( goals ); executions.add( buildCssexecution
                     * ); buildCssPlugin.setExecutions( executions ); Xpp3Dom buildCssConfiguration = new Xpp3Dom(
                     * "configuration" ); addConfiguration( "portalCommonDirName", "/", buildCssConfiguration );
                     * addConfiguration( "docrootDirName", "src/main/webapp", buildCssConfiguration );
                     * buildCssPlugin.setConfiguration( buildCssConfiguration ); newPlugins.add( buildCssPlugin );
                     * Plugin jarPlugin = new Plugin(); jarPlugin.setGroupId( "org.apache.maven.plugins" );
                     * jarPlugin.setArtifactId( "maven-war-plugin" ); jarPlugin.setVersion( "3.0.0" ); Xpp3Dom
                     * jarConfiguration = new Xpp3Dom( "configuration" );
                     */
                    // addConfiguration( "packagingExcludes", "**/.sass-cache/", jarConfiguration );
                    // jarPlugin.setConfiguration( jarConfiguration );

                    // newPlugins.add( jarPlugin );
                }

                build.setPlugins( newPlugins );
            }

            try(FileOutputStream out = new FileOutputStream( pomFile ))
            {
                MavenXpp3Writer writer = new MavenXpp3Writer();

                writer.write( new FileOutputStream( pomFile ), model );
            }
            catch( Exception e )
            {
                LiferayMavenCore.logError( "Error writing maven pom file", e );
                throw e;
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Error updating maven pom file", e );
            throw e;
        }
    }
}
