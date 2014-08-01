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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.drivers.models.TemplateDescriptor;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class CreateDBConnectAction extends AbstractServerRunningAction
{

    private final String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile"; //$NON-NLS-1$
    private final String connectionDesc = "Mysql Connection Profile"; //$NON-NLS-1$

    public CreateDBConnectAction()
    {
        super();
    }

    protected LiferayTomcatServer getLiferayServer()
    {
        return (LiferayTomcatServer) selectedServer.loadAdapter( ILiferayTomcatServer.class, null );
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    @SuppressWarnings( "resource" )
    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final LiferayTomcatServer portalServer = getLiferayServer();
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( portalServer.getServer() );

            IPath bundlePath = liferayRuntime.getAppServerDir().removeLastSegments( 1 );
            IPath setupWizardPath = bundlePath.append( "portal-setup-wizard.properties" ); //$NON-NLS-1$
            if( setupWizardPath.toFile().exists() )
            {
                try
                {
                    PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();
                    pluginPackageProperties.load( setupWizardPath.toFile() );
                    final String driverName =
                        (String) pluginPackageProperties.getProperty( "jdbc.default.driverClassName" ); //$NON-NLS-1$
                    if( driverName != null )
                    {
                        final String runTimeName = liferayRuntime.getRuntime().getName();
                        final String userName = (String) pluginPackageProperties.getProperty( "jdbc.default.username" ); //$NON-NLS-1$
                        final String dbConnectionURL =
                            (String) pluginPackageProperties.getProperty( "jdbc.default.url" ); //$NON-NLS-1$
                        final String password = (String) pluginPackageProperties.getProperty( "jdbc.default.password" ); //$NON-NLS-1$

                        final URL[] runtimeLibs = getLiferayRuntimeLibs( liferayRuntime );

                        final Class<?> classRef = new URLClassLoader( runtimeLibs ).loadClass( driverName );
                        if( classRef != null )
                        {
                            final String libPath =
                                classRef.getProtectionDomain().getCodeSource().getLocation().getPath();
                            final String jarPath = new File( libPath ).getAbsolutePath();
                            new Job( Msgs.addDBConnnection )
                            {

                                @Override
                                protected IStatus run( IProgressMonitor monitor )
                                {
                                    IConnectionProfile mysqlProfile =
                                        ProfileManager.getInstance().getProfileByName( runTimeName );
                                    if( mysqlProfile == null )
                                    {
                                        mysqlProfile =
                                            new ConnectionProfile( runTimeName, connectionDesc, providerId, null, true );
                                        Properties properties = new Properties();
                                        properties.setProperty(
                                            IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, driverName );
                                        properties.setProperty(
                                            IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, "lportal" ); //$NON-NLS-1$
                                        properties.setProperty(
                                            IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password );
                                        properties.setProperty(
                                            IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, "true" ); //$NON-NLS-1$
                                        properties.setProperty(
                                            IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, userName );
                                        properties.setProperty(
                                            IDriverMgmtConstants.PROP_DEFN_TYPE,
                                            "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate" ); //$NON-NLS-1$
                                        properties.setProperty(
                                            IJDBCDriverDefinitionConstants.URL_PROP_ID, dbConnectionURL );
                                        properties.setProperty(
                                            ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, runTimeName );
                                        properties.setProperty( IDriverMgmtConstants.PROP_DEFN_JARLIST, jarPath );
                                        mysqlProfile.setBaseProperties( properties );

                                        try
                                        {
                                            addDriverInstance( mysqlProfile );
                                        }
                                        catch( ConnectionProfileException e )
                                        {
                                            LiferayServerCore.logError( Msgs.addProfileError, e );
                                        }

                                    }
                                    return Status.OK_STATUS;
                                }

                            }.schedule();
                        }
                    }
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( Msgs.noDBConnectDriver, e );
                }
            }
        }
    }

    private URL[] getLiferayRuntimeLibs( final ILiferayRuntime liferayRuntime ) throws Exception
    {

        final IPath[] extraLibs = liferayRuntime.getUserLibs();
        final IPath globalDir = liferayRuntime.getAppServerLibGlobalDir();
        final IPath portalDir = liferayRuntime.getAppServerPortalDir();
        final List<URL> libUrlList = new ArrayList<URL>();

        if( portalDir != null )
        {
            final File libDir = portalDir.append( "WEB-INF/lib" ).toFile(); //$NON-NLS-1$

            addLibs( libDir, libUrlList );
        }

        if( globalDir != null )
        {
            final File libDir = globalDir.append( "lib" ).toFile(); //$NON-NLS-1$

            addLibs( libDir, libUrlList );

            final File extLibDir = globalDir.append( "lib/ext" ).toFile(); //$NON-NLS-1$

            addLibs( extLibDir, libUrlList );
        }

        if( !CoreUtil.isNullOrEmpty( extraLibs ) )
        {
            for( IPath url : extraLibs )
            {
                libUrlList.add( new File( url.toOSString() ).toURI().toURL() );
            }
        }

        final URL[] urls = libUrlList.toArray( new URL[libUrlList.size()] );

        return urls;
    }

    private void addLibs( final File libDir, List<URL> libUrlList ) throws MalformedURLException
    {
        if( libDir.exists() )
        {
            final File[] libs = libDir.listFiles( new FilenameFilter()
            {

                public boolean accept( File dir, String fileName )
                {
                    return fileName.toLowerCase().endsWith( ".jar" ); //$NON-NLS-1$
                }
            } );

            if( !CoreUtil.isNullOrEmpty( libs ) )
            {
                for( File portaLib : libs )
                {
                    libUrlList.add( portaLib.toURI().toURL() );
                }
            }
        }
    }

    private int addDriverInstance( IConnectionProfile currentConnectionProfile ) throws ConnectionProfileException
    {
        ProfileManager.getInstance().addProfile( currentConnectionProfile );
        Properties baseProperties = currentConnectionProfile.getBaseProperties();
        final String jarList = baseProperties.getProperty( IDriverMgmtConstants.PROP_DEFN_JARLIST, null );
        final String driverDefinitionID =
            baseProperties.getProperty( ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, null );
        final String driverTemplateID = baseProperties.getProperty( IDriverMgmtConstants.PROP_DEFN_TYPE, null );
        final TemplateDescriptor templateDescriptor = TemplateDescriptor.getDriverTemplateDescriptor( driverTemplateID );

        final DriverInstance driverInstance = DriverManager.getInstance().getDriverInstanceByID( driverDefinitionID );
        // Check to see if the jarList specified in the file is the same as
        // the one in the workspace's driver definition that has same name
        if( ( ( driverInstance == null ) && ( jarList != null ) ) ||
            ( ( driverInstance != null ) && ( jarList != null ) && !( jarList.equals( driverInstance.getJarList() ) ) ) )
        {
            // Check to see if there is an existing driver definition that
            // can be used
            final DriverInstance[] driverInstances =
                DriverManager.getInstance().getDriverInstancesByTemplate( driverTemplateID );
            String existingDriverDefinitionID = null;
            for( int driverIndex = 0; driverIndex < driverInstances.length; driverIndex++ )
            {
                if( jarList.equals( driverInstances[driverIndex].getJarList() ) )
                {
                    // if jarList is the same then re-use the driver
                    // definition
                    existingDriverDefinitionID = driverInstances[driverIndex].getId();
                    break;
                }
            }
            if( existingDriverDefinitionID != null )
            {
                baseProperties.setProperty(
                    ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, existingDriverDefinitionID );
                currentConnectionProfile.setProperties( currentConnectionProfile.getProviderId(), baseProperties );
            }
            else
            {
                // Create new driver definition and assign to connection
                // profile
                String driverDefinitionNameBase = currentConnectionProfile.getName();
                if( templateDescriptor != null )
                {
                    driverDefinitionNameBase = templateDescriptor.getDefaultDefinitionName();
                }
                final String driverClass =
                    baseProperties.getProperty( IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID ).toString();
                final String uniqueDriverInstanceName = generateUniqueDriverDefinitionName( driverDefinitionNameBase );
                final DriverInstance newDriverInstance =
                    DriverManager.getInstance().createNewDriverInstance(
                        driverTemplateID, uniqueDriverInstanceName, jarList, driverClass );
                if( newDriverInstance != null )
                {
                    baseProperties.setProperty(
                        ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, newDriverInstance.getId() );
                    currentConnectionProfile.setProperties( currentConnectionProfile.getProviderId(), baseProperties );
                }
            }
        }
        return Status.OK;
    }

    private String generateUniqueDriverDefinitionName( final String driverDefinitionNameBase )
    {
        int index = 1;
        String testName = driverDefinitionNameBase + String.valueOf( index );
        while( DriverManager.getInstance().getDriverInstanceByName( testName ) != null )
        {
            index++;
            testName = driverDefinitionNameBase + String.valueOf( index );
        }
        return testName;
    }

    private static class Msgs extends NLS
    {

        public static String noDBConnectDriver;
        public static String addDBConnnection;
        public static String addProfileError;

        static
        {
            initializeMessages( CreateDBConnectAction.class.getName(), Msgs.class );
        }
    }
}
