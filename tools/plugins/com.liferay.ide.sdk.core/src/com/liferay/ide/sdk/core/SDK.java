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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.core.XMLMemento;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class SDK
{
    private static final String ALWAYS = "always"; //$NON-NLS-1$

    private static final String MSG_MANAGED_BY_LIFERAY_IDE =
        "Managed by Liferay IDE (remove this comment to prevent future updates)"; //$NON-NLS-1$

    private static final String PROJECT_FILE_PATTERN =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<projectDescription>\n\t<name>{0}</name>\n\t<comment></comment>\n\t<projects></projects>\n\t<buildSpec></buildSpec>\n\t<natures></natures>\n</projectDescription>\n"; //$NON-NLS-1$

    public static String createXMLNameValuePair( String name, String value )
    {
        return name + "=\"" + value + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static List<String> SUPPORT_SERVER_TYPE = Arrays.asList( new String[]{ "tomcat", "jboss", "glassfish", "jetty"} );

    @SuppressWarnings( "deprecation" )
    protected static IEclipsePreferences getPrefStore()
    {
        return new InstanceScope().getNode( SDKCorePlugin.PREFERENCE_ID );
    }

    // private String runtime;

    protected boolean contributed;

    protected boolean defaultSDK;

    protected IPath location;

    protected String name;

    protected String version;

    public SDK()
    {
    }

    public SDK( IPath location )
    {
        this.location = location;
    }

    public void addProjectFile()
    {
        if( hasProjectFile() )
        {
            return;
        }

        if( this.location.toFile().exists() )
        {
            // check for existing project file
            File projectFile = this.location.append( ".project" ).toFile(); //$NON-NLS-1$

            if( !projectFile.exists() )
            {
                try
                {
                    FileUtil.writeFileFromStream(
                        projectFile,
                        new ByteArrayInputStream( MessageFormat.format(
                            PROJECT_FILE_PATTERN, new Object[] { this.name } ).getBytes( "UTF-8" ) ) ); //$NON-NLS-1$
                }
                catch( Exception e )
                {
                    SDKCorePlugin.logError( e );
                }
            }
        }
    }

    public IStatus buildLanguage(
        IProject project, IFile langFile, Map<String, String> overrideProperties,
        Map<String, String> appServerProperties, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String langDirLocation = langFile.getParent().getRawLocation().toOSString();

            String langFileName = langFile.getFullPath().removeFileExtension().lastSegment();

            properties.put( ISDKConstants.PROPERTY_LANG_DIR, langDirLocation );
            properties.put( ISDKConstants.PROPERTY_LANG_FILE, langFileName );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();

            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_LANG_CMD, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus buildService(
        IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties,
        Map<String, String> appServerProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String serviceFile = serviceXmlFile.getRawLocation().toOSString();

            properties.put( ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString() );
            properties.put( ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_SERVICE, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus buildWSDD(
        IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties,
        Map<String, String> appServerProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String serviceFile = serviceXmlFile.getRawLocation().toOSString();

            properties.put( ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString() );
            properties.put( ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_WSDD, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus cleanAppServer(
        IProject project, IPath bundleZipLocation, String appServerDir, Map<String, String> appServerProperties,
        IProgressMonitor monitor )
    {
        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            IPath workPath = new Path( appServerDir ).removeLastSegments( 2 );

            properties.put( ISDKConstants.PROPERTY_APP_ZIP_NAME, bundleZipLocation.toOSString() );
            properties.put( ISDKConstants.PROPERTY_EXT_WORK_DIR, workPath.toOSString() );

            IStatus status = runTarget( project, properties, "clean-app-server", true, monitor ); //$NON-NLS-1$

            if( !status.isOK() )
            {
                return status;
            }
        }
        catch( Exception ex )
        {
            return SDKCorePlugin.createErrorStatus( ex );
        }

        return Status.OK_STATUS;
    }

    public IStatus compileThemePlugin(
        IProject project, Map<String, String> overrideProperties, Map<String, String> appServerProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_COMPILE, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public SDK copy()
    {
        SDK copy = new SDK( this.getLocation() );

        copy.setContributed( isContributed() );
        copy.setDefault( isDefault() );
        copy.setName( getName() );
        copy.setVersion( getVersion() );

        return copy;
    }

    public IPath createNewProject(
        String projectName, ArrayList<String> arguments, String type, String workingDir, IProgressMonitor monitor ) throws CoreException
    {
        CreateHelper createHelper = new CreateHelper( this, monitor );

        final IPath pluginFolder = getLocation().append( getPluginFolder( type ) );

        final IPath newPath = pluginFolder.append( projectName + getPluginSuffix( type ) );

        String createScript = ISDKConstants.CREATE_BAT;

        if( !CoreUtil.isWindows() )
        {
            createScript = ISDKConstants.CREATE_SH;
        }

        final IPath createFilePath = pluginFolder.append( createScript );

        final File createFile = createFilePath.toFile();

        String originalCreateConetent = "";

        if( !CoreUtil.isWindows() && createFile.exists() )
        {
            originalCreateConetent = FileUtil.readContents( createFile, true );

            if( originalCreateConetent.contains( "DisplayName=\\\"$2\\\"" ) )
            {
                String createContent = originalCreateConetent.replace( "DisplayName=\\\"$2\\\"", "DisplayName=\"$2\"" );

                try
                {
                    FileUtil.writeFile(
                        createFile, new ByteArrayInputStream( createContent.toString().getBytes( "UTF-8" ) ), null );
                }
                catch( Exception e )
                {
                    SDKCorePlugin.logError( e );
                }
            }
        }

        createHelper.runTarget( createFilePath, arguments, workingDir );

        if( !newPath.toFile().exists() )
        {
            throw new CoreException( SDKCorePlugin.createErrorStatus( "Create script did not complete successfully." ) );
        }

        if( !CoreUtil.isNullOrEmpty( originalCreateConetent ) )
        {
            try
            {
                FileUtil.writeFile(
                    createFile, new ByteArrayInputStream( originalCreateConetent.toString().getBytes( "UTF-8" ) ), null );
            }
            catch( Exception e )
            {
                SDKCorePlugin.logError( e );
            }
        }

        return newPath;
    }

    public IPath createNewExtProject( String extName, String extDisplayName, Map<String, String> appServerProperties,
        boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );

            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            properties.putAll( appServerProperties );

            properties.put( ISDKConstants.PROPERTY_EXT_NAME, extName );
            properties.put( ISDKConstants.PROPERTY_EXT_DISPLAY_NAME, extDisplayName );

            // create a space for new portlet template to get built
            IPath tempPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );
            // if (!newPortletPath.toFile().mkdirs()) {
            // throw new
            // CoreException(SDKPlugin.createErrorStatus("Unable to create directory in state location"));
            // }

            properties.put( ISDKConstants.PROPERTY_EXT_PARENT_DIR, tempPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.EXT_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return tempPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewHookProject(
        String hookName, String hookDisplayName, Map<String, String> appServerProperties, boolean separateJRE,
        String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_HOOK_NAME, hookName );
            properties.put( ISDKConstants.PROPERTY_HOOK_DISPLAY_NAME, hookDisplayName );

            // create a space for new portlet template to get built
            IPath newHookPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_HOOK_PARENT_DIR, newHookPath.toOSString() );

            final IPath buildLocation = getLocation().append( ISDKConstants.HOOK_PLUGIN_ANT_BUILD );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newHookPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewLayoutTplProject( String layoutTplName, String layoutTplDisplayName,
        Map<String, String> appServerProperties, boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            properties.putAll( appServerProperties );

            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_NAME, layoutTplName );
            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_DISPLAY_NAME, layoutTplDisplayName );

            // create a space for new layouttpm template to get built
            IPath newLayoutTplPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_PARENT_DIR, newLayoutTplPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget(
                getLocation().append( ISDKConstants.LAYOUTTPL_PLUGIN_ANT_BUILD ), ISDKConstants.TARGET_CREATE,
                properties, separateJRE, workingDir );

            return newLayoutTplPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewPortletProject( String portletName, String portletDisplayName, String portletFramework,
        Map<String, String> appServerProperties,  boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            properties.put( ISDKConstants.PROPERTY_PORTLET_NAME, portletName );
            properties.put( ISDKConstants.PROPERTY_PORTLET_DISPLAY_NAME, portletDisplayName );
            properties.put( ISDKConstants.PROPERTY_PORTLET_FRAMEWORK, portletFramework );

            // create a space for new portlet template to get built
            IPath newPortletPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_PORTLET_PARENT_DIR, newPortletPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.PORTLET_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newPortletPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewThemeProject(
        String themeName, String themeDisplayName, boolean separateJRE, String workingDir, String baseDir,
        IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_THEME_NAME, themeName );
            properties.put( ISDKConstants.PROPERTY_THEME_DISPLAY_NAME, themeDisplayName );

            // create a space for new portlet template to get built
            IPath tempPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_THEME_PARENT_DIR, tempPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.THEME_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return tempPath;
        }
        catch( CoreException e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewWebProject(
        String webName, String webDisplayName, Map<String, String> appServerProperties, boolean separateJRE,
        String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_WEB_NAME, webName );
            properties.put( ISDKConstants.PROPERTY_WEB_DISPLAY_NAME, webDisplayName );

            // create a space for new web template to get built
            IPath newWebPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_WEB_PARENT_DIR, newWebPath.toOSString() );

            final IPath buildLocation = getLocation().append( ISDKConstants.WEB_PLUGIN_ANT_BUILD );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newWebPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

//    This code is not used since IDE-810 and it may cause the new identical sdk checking state covers the old one's.
//    @Override
//    public boolean equals( Object obj )
//    {
//        return obj instanceof SDK && getName() != null && getName().equals( ( (SDK) obj ).getName() ) &&
//            getLocation() != null && getLocation().equals( ( (SDK) obj ).getLocation() );
//    }

    public IStatus directDeploy(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        Map<String, String> appServerProperties, IProgressMonitor monitor )
    {

        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );

            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_DIRECT_DEPLOY, properties, separateJRE, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;

    }

    public IPath[] getAntLibraries()
    {
        List<IPath> antLibs = new ArrayList<IPath>();

        for( String antLib : ISDKConstants.ANT_LIBRARIES )
        {
            antLibs.add( getLocation().append( antLib ) );
        }

        return antLibs.toArray( new IPath[0] );
    }

    private String getDefaultWorkingDir( final IPath buildFile )
    {
        return buildFile.removeLastSegments( 1 ).toOSString();
    }

    public IPath getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public IPath getPortletTemplatePath()
    {
        return getLocation().append( ISDKConstants.PORTLET_PLUGIN_ZIP_PATH );
    }

    public String getVersion()
    {
        if( version == null )
        {
            IPath sdkLocation = getLocation().makeAbsolute();

            if( !sdkLocation.isEmpty() )
            {
                try
                {
                    version = SDKUtil.readSDKVersion( sdkLocation.toOSString() );

                    if( version.equals( ILiferayConstants.V611.toString() ) )
                    {
                        Properties buildProperties = getProperties( sdkLocation.append( "build.properties" ).toFile() ); //$NON-NLS-1$

                        if( hasAppServerSpecificProps( buildProperties ) )
                        {
                            version = ILiferayConstants.V612.toString();
                        }
                    }

                    if( version.equals( ILiferayConstants.V6120.toString() ) )
                    {
                        Properties buildProperties = getProperties( sdkLocation.append( "build.properties" ).toFile() ); //$NON-NLS-1$

                        if( hasAppServerSpecificProps( buildProperties ) )
                        {
                            version = ILiferayConstants.V6130.toString();
                        }
                    }
                }
                catch( Exception e )
                {
                    SDKCorePlugin.logError( "Could not detect the sdk version.", e); //$NON-NLS-1$
                }
            }
        }

        return version;
    }

    private String getPluginFolder( String type )
    {
        if( "ext".equals( type ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER;
        }
        else if( "portlet".equals( type ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER;
        }
        else if( "hook".equals( type ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER;
        }
        else if( "layouttpl".equals( type ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER;
        }
        else if( "theme".equals( type ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER;
        }
        else if( "web".equals( type ) )
        {
            return ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER;
        }
        else
        {
            return "";
        }
    }

    private String getPluginSuffix( String type )
    {
        if( "ext".equals( type ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "portlet".equals( type ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "hook".equals( type ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "layouttpl".equals( type ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "theme".equals( type ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "web".equals( type ) )
        {
            return ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;
        }
        else
        {
            return "";
        }
    }

    private boolean hasAppServerSpecificProps( Properties props )
    {
        Enumeration<?> names = props.propertyNames();

        while( names.hasMoreElements() )
        {
            String name = names.nextElement().toString();

            if( name.matches( "app.server.tomcat.*" ) ) //$NON-NLS-1$
            {
                return true;
            }
        }

        return false;
    }

    private Properties getProperties( File file )
    {
        Properties properties = new Properties();

        try
        {
            InputStream propertiesInput = new FileInputStream( file );
            properties.load( propertiesInput );
            propertiesInput.close();
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return properties;
    }

    public Map<String, String> getProperties() 
    {
        Map<String, String> appServerProperties = null;
        try
        {
            Project project = new Project();
            project.setBaseDir( new File(getLocation().toPortableString()) );

            Property envTask = new Property();
            envTask.setProject( project );
            envTask.setEnvironment( "env" );

            envTask.execute();

            final String userName = project.getProperty( "env.USERNAME" ).toString();
            final String cpName = project.getProperty( "env.USERNAME" ).toString();
            File userBuildProperties = new File(getLocation().append( "build." + userName +".properties" ).toPortableString());
            File cpBuildProperties = new File(getLocation().append( "build." + cpName +".properties" ).toPortableString());

            if ( userBuildProperties.exists() || cpBuildProperties.exists())
            {
                loadProperties(project, project.getProperty( "env.USERNAME" ).toString());
                loadProperties(project, project.getProperty( "env.COMPUTERNAME" ).toString());
            }

            Property propertyTask = new Property();
            propertyTask.setProject( project );
            propertyTask.setFile( new File(getLocation().append( "build.properties" ).toPortableString()) );
            propertyTask.execute();

            final Map<String,String> propertyCopyList = new HashMap<String,String>();
            propertyCopyList.put( "app.server." + project.getProperty( "app.server.type") + ".dir",  "app.server.dir"  );
            propertyCopyList.put( "app.server." + project.getProperty( "app.server.type") + ".deploy.dir",  "app.server.deploy.dir"  );
            propertyCopyList.put( "app.server." + project.getProperty( "app.server.type") + ".lib.global.dir",  "app.server.lib.global.dir"  );
            propertyCopyList.put( "app.server." + project.getProperty( "app.server.type") + ".portal.dir",  "app.server.portal.dir"  );

            for( Iterator<String> iterator = propertyCopyList.keySet().iterator(); iterator.hasNext(); )
            {
                AntPropertyCopy propertyCopyTask = new AntPropertyCopy();
                propertyCopyTask.setOverride( true );
                propertyCopyTask.setProject( project );
                String from = (String)iterator.next();
                String to = propertyCopyList.get( from );
                propertyCopyTask.setFrom( from );
                propertyCopyTask.setName( to );
                propertyCopyTask.execute();
            }

            appServerProperties = new HashMap<String,String>();

            appServerProperties.put( "app.server.type", project.getProperty("app.server.type")  );
            appServerProperties.put( "app.server.dir", project.getProperty("app.server.dir") );
            appServerProperties.put( "app.server.deploy.dir", project.getProperty("app.server.deploy.dir") );
            appServerProperties.put( "app.server.lib.global.dir",project.getProperty("app.server.lib.global.dir") );
            appServerProperties.put( "app.server.parent.dir", project.getProperty("app.server.parent.dir") );
            appServerProperties.put( "app.server.portal.dir", project.getProperty("app.server.portal.dir") );
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }
        return appServerProperties;
    }

    public boolean hasProjectFile()
    {
        return this.location != null && this.location.append( ".project" ).toFile().exists(); //$NON-NLS-1$
    }

    public boolean isContributed()
    {
        return contributed;
    }

    public boolean isDefault()
    {
        return defaultSDK;
    }

    public boolean isValid()
    {
        IPath sdkLocation = getLocation();

        if( sdkLocation == null )
        {
            return false;
        }

        if( !SDKUtil.isSDKSupported( sdkLocation.toOSString() ) )
        {
            return false;
        }

        if( !SDKUtil.isValidSDKLocation( sdkLocation.toOSString() ) )
        {
            return false;
        }

        return true;
    }

    public void loadFromMemento( XMLMemento sdkElement )
    {
        setName( sdkElement.getString( "name" ) ); //$NON-NLS-1$
        setLocation( Path.fromPortableString( sdkElement.getString( "location" ) ) ); //$NON-NLS-1$
        // setRuntime(sdkElement.getString("runtime"));
    }


    private void loadProperties(Project project, final String keyName)
    {
        Property loadPropetiesTask = new Property();
        loadPropetiesTask.setProject( project );
        loadPropetiesTask.setFile( new File(getLocation().append( "build." + keyName +".properties" ).toPortableString()) );
        loadPropetiesTask.execute();
    }

    public void openSDKInEclipse()
    {
        if( !hasProjectFile() )
        {
            addProjectFile();
        }

        IProject sdkProject = CoreUtil.getProject( getName() );

        if( sdkProject == null || ( !sdkProject.exists() ) )
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();

            IProjectDescription description = workspace.newProjectDescription( getName() );
            description.setLocationURI( getLocation().toFile().toURI() );

            IProgressMonitor npm = new NullProgressMonitor();

            try
            {
                sdkProject.create( description, npm );
                sdkProject.open( npm );
            }
            catch( Exception e )
            {
                SDKCorePlugin.logError( e );
            }
        }
    } 

    protected void persistAppServerProperties( Map<String, String> properties ) throws FileNotFoundException,
        IOException, ConfigurationException
    {
        IPath loc = getLocation();

        // check for build.<username>.properties

        String userName = System.getProperty( "user.name" ); //$NON-NLS-1$

        File userBuildFile = loc.append( "build." + userName + ".properties" ).toFile(); //$NON-NLS-1$ //$NON-NLS-2$

        if( userBuildFile.exists() )
        {
            /*
             * the build file exists so we need to check the following conditions 1. if the header in the comment
             * contains the text written by a previous SDK operation then we can write it again 2. if the file was not
             * previously written by us we will need to prompt the user with permission yes/no to update the
             * build.<username>.properties file
             */

            PropertiesConfiguration propsConfig = new PropertiesConfiguration( userBuildFile );

            String header = propsConfig.getHeader();

            boolean shouldUpdateBuildFile = false;

            if( header != null && header.contains( MSG_MANAGED_BY_LIFERAY_IDE ) )
            {
                shouldUpdateBuildFile = true;
            }
            else
            {
                String overwrite = getPrefStore().get( SDKCorePlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE, ALWAYS );

                if( ALWAYS.equals( overwrite ) )
                {
                    shouldUpdateBuildFile = true;
                }
                else
                {
                    shouldUpdateBuildFile = false;
                }
            }

            if( shouldUpdateBuildFile )
            {
                for( String key : properties.keySet() )
                {
                    propsConfig.setProperty( key, properties.get( key ) );
                }

                propsConfig.setHeader( MSG_MANAGED_BY_LIFERAY_IDE );
                propsConfig.save( userBuildFile );
            }

        }
        else
        {
            Properties props = new Properties();

            props.putAll( properties );

            props.store( new FileOutputStream( userBuildFile ), MSG_MANAGED_BY_LIFERAY_IDE );
        }
    }

    public IStatus runCommand(  IProject project,
                                IFile buildXmlFile,
                                String command,
                                Map<String, String> overrideProperties,
                                Map<String, String> appServerProperties,
                                IProgressMonitor monitor )
    {
        final SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = buildXmlFile.getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, command, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    protected IStatus runTarget(
        IProject project, Map<String, String> properties, String target, boolean separateJRE, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, target, properties, separateJRE, workingDir );
        }
        catch( CoreException e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public void saveToMemento( XMLMemento child )
    {
        child.putString( "name", getName() ); //$NON-NLS-1$
        child.putString( "location", getLocation().toPortableString() ); //$NON-NLS-1$
        child.putString( "version", getVersion() ); //$NON-NLS-1$
        // child.putString("runtime", getRuntime() != null ? getRuntime() : "");
    }

    public void setContributed( boolean contributed )
    {
        this.contributed = contributed;
    }

    public void setDefault( boolean defaultSDK )
    {
        this.defaultSDK = defaultSDK;
    }

    public void setLocation( IPath location )
    {
        this.location = location;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return this.getName() + ( isDefault() ? " [default]" : "");  //$NON-NLS-1$//$NON-NLS-2$
    }

    public String toXmlString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append( "<sdk " ); //$NON-NLS-1$
        builder.append( createXMLNameValuePair( "name", getName() ) ); //$NON-NLS-1$
        builder.append( createXMLNameValuePair( "location", getLocation().toPortableString() ) ); //$NON-NLS-1$
        // builder.append(createXMLNameValuePair("runtime", getRuntime() != null
        // ? getRuntime() : ""));
        builder.append( createXMLNameValuePair( "version", getVersion() ) ); //$NON-NLS-1$
        builder.append( "/>" ); //$NON-NLS-1$

        return builder.toString();
    }

    public MultiStatus validate()
    {
        MultiStatus status = new MultiStatus( SDKCorePlugin.PLUGIN_ID, 0, "sdk is invalid", null );

        boolean validLocation = SDKUtil.isValidSDKLocation( getLocation().toOSString() );

        boolean buildXmlExists = getLocation().append( "build.xml" ).toFile().exists(); //$NON-NLS-1$

        if( !validLocation )
        {
            status.add( SDKCorePlugin.createErrorStatus( Msgs.SDKLocationInvalid ) ); 
            return status;
        }

        if( !buildXmlExists )
        {
            status.add( SDKCorePlugin.createErrorStatus( Msgs.buildXmlFileNotExist ) );
            return status;
        }

        Map<String,String> sdkProperties = getProperties();

        if ( sdkProperties == null )
        {
            status.add( SDKCorePlugin.createErrorStatus( "Could not find any sdk settting." ) );
            return status;
        }

        for( String properyKey : sdkProperties.keySet() )
        {
            final String propertyValue = sdkProperties.get( properyKey );

            if ( propertyValue == null )
            {
                status.add( SDKCorePlugin.createErrorStatus( properyKey + " is null." ) );
                return status;
            }
            else
            {
                switch (properyKey)
                {
                    case "app.server.type":
                    {
                        if ( !SUPPORT_SERVER_TYPE.contains( propertyValue ) )
                        {
                            status.add( SDKCorePlugin.createErrorStatus( "The " + properyKey + "(" + propertyValue + ") server is not supported by Liferay." ) );
                            return status;
                        }
                        break;
                    }
                    case "app.server.dir":
                    case "app.server.deploy.dir":
                    case "app.server.lib.global.dir":
                    case "app.server.parent.dir":
                    case "app.server.portal.dir":
                    {
                        IPath propertyPath = new Path(propertyValue);

                        if ( !propertyPath.isAbsolute() )
                        {
                            status.add( SDKCorePlugin.createErrorStatus( "The " + properyKey + "(" + propertyValue + ") is not absolute path." ) );
                            return status;
                        }

                        if ( !propertyPath.toFile().exists() )
                        {
                            status.add( SDKCorePlugin.createErrorStatus( "The " + properyKey + "(" + propertyValue + ") is not exsit." ) );
                            return status;
                        }
                        break;
                    }
                    default:
                    {
                        status.add( Status.OK_STATUS );
                        return status;
                    }
                }
            }
        }
        status.add( Status.OK_STATUS );
        return status;
    }

    public IStatus war(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        Map<String, String> appServerProperties, IProgressMonitor monitor )
    {
        return war( project, overrideProperties, separateJRE, appServerProperties, null, monitor );
    }

    public IStatus war(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        Map<String, String> appServerProperties, String[] vmargs, IProgressMonitor monitor )
    {
        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );
            antHelper.setVMArgs( vmargs );

            //persistAppServerProperties( appServerProperties );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_WAR, properties, separateJRE, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String buildXmlFileNotExist;
//        public static String pluginsSDK;
//        public static String rememberAnswer;
        public static String SDKLocationInvalid;
//        public static String userBuildPropertiesFileNotUpdated;

        static
        {
            initializeMessages( SDK.class.getName(), Msgs.class );
        }
    }
}
