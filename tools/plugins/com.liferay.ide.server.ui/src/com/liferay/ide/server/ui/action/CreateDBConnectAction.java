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
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class CreateDBConnectAction extends AbstractServerRunningAction
{

    private final static String PORTAL_EXT_PROPERTIES1 = "portal-setup-wizard.properties"; //$NON-NLS-1$
    private final static String PORTAL_SETUP_PROPERTIES = "portal-setup-wizard.properties"; //$NON-NLS-1$
    private final static String JDBC_DRIVER_CLASS_NAME = "jdbc.default.driverClassName"; //$NON-NLS-1$

    public CreateDBConnectAction()
    {
        super();
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    private Properties getDatabaseProperties( IPath bundlePath )
    {
        IPath bundleExtPath = bundlePath.append( PORTAL_EXT_PROPERTIES1 );
        Properties pluginPackageProperties = new Properties();
        try
        {
            if( bundleExtPath.toFile().exists() )
            {
                final InputStream extInputStream = new FileInputStream( bundleExtPath.toFile() );
                pluginPackageProperties.load( extInputStream );
                extInputStream.close();

                final String driverName = (String) pluginPackageProperties.getProperty( JDBC_DRIVER_CLASS_NAME );

                if( CoreUtil.isNullOrEmpty( driverName ) )
                {
                    IPath setupWizardPath = bundlePath.append( PORTAL_SETUP_PROPERTIES );
                    if( setupWizardPath.toFile().exists() )
                    {
                        final InputStream setupInputStream = new FileInputStream( setupWizardPath.toFile() );
                        pluginPackageProperties.load( setupInputStream );
                        setupInputStream.close();
                    }
                }
            }
            else
            {
                IPath setupWizardPath = bundlePath.append( PORTAL_SETUP_PROPERTIES );

                if( setupWizardPath.toFile().exists() )
                {
                    final InputStream setupInputStream = new FileInputStream( setupWizardPath.toFile() );
                    pluginPackageProperties.load( setupInputStream );
                    setupInputStream.close();
                }
            }
        }
        catch( Exception extException )
        {
            LiferayServerCore.logError( Msgs.noDatabasePropertyFile, extException );
        }
        return pluginPackageProperties;
    }

    @SuppressWarnings( "resource" )
    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( selectedServer );

            Properties pluginPackageProperties = getDatabaseProperties( liferayRuntime.getLiferayHome() );
            final String driverName =
                pluginPackageProperties.getProperty( JDBC_DRIVER_CLASS_NAME, "org.hsqldb.jdbcDriver" ); //$NON-NLS-1$ 

            final String connectionName = liferayRuntime.getRuntime().getName();
            final String userName = pluginPackageProperties.getProperty( "jdbc.default.username" ); //$NON-NLS-1$
            final String connectionUrl = pluginPackageProperties.getProperty( "jdbc.default.url" ); //$NON-NLS-1$
            final String password = pluginPackageProperties.getProperty( "jdbc.default.password" ); //$NON-NLS-1$

            try
            {
                final URL[] runtimeLibs = getLiferayRuntimeLibs( liferayRuntime );
                new Job( Msgs.addDBConnnection )
                {
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            final Class<?> classRef = new URLClassLoader( runtimeLibs ).loadClass( driverName );

                            if( classRef != null )
                            {
                                final String libPath =
                                    classRef.getProtectionDomain().getCodeSource().getLocation().getPath();
                                final String jarPath = java.net.URLDecoder.decode( libPath, "UTF-8" ); //$NON-NLS-1$
                                final String driverPath = new File( jarPath ).getAbsolutePath();

                                LiferayDatabaseConnection dbConnection =
                                    getLiferayDBConnection( driverName, userName, password, connectionUrl );

                                dbConnection.addDatabaseConnectionProfile( connectionName, driverPath );

                                UIUtil.async( new Runnable()
                                {
                                    public void run()
                                    {
                                        IViewPart dbView =
                                            UIUtil.showView( "org.eclipse.datatools.connectivity.DataSourceExplorerNavigator" ); //$NON-NLS-1$
                                        dbView.setFocus();
                                    }
                                } );
                            }
                        }
                        catch( Exception e )
                        {
                            LiferayServerCore.logError( Msgs.addProfileError, e );
                        }

                        return Status.OK_STATUS;
                    }

                }.schedule();
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( Msgs.noDBConnectDriver, e );
            }

        }
    }

    private String generateUniqueConnectionProfileName( final String connectionProfileName )
    {
        int index = 1;
        String testName = connectionProfileName;
        while( ProfileManager.getInstance().getProfileByName( testName ) != null )
        {
            index++;
            testName = connectionProfileName + String.valueOf( index );
        }
        return testName;
    }

    private URL[] getLiferayRuntimeLibs( final ILiferayRuntime liferayRuntime ) throws Exception
    {
        final IPath[] extraLibs = liferayRuntime.getUserLibs();
        final List<URL> libUrlList = new ArrayList<URL>();

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

    private class LiferayDatabaseConnection
    {

        private String driverClass;
        private String providerId;
        private String connectionDesc;
        private String driverTemplate;
        private String userName;
        private String password;
        private String connectionUrl;

        public LiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate,
            final String userName, final String password, final String connectionUrl )
        {
            super();
            this.driverClass = driverClass;
            this.providerId = providerId;
            this.connectionDesc = connectinDesc;
            this.driverTemplate = driverTemplate;
            this.userName = userName;
            this.password = password;
            this.connectionUrl = connectionUrl;
        }

        private String generateUniqueDriverDefinitionName( final String driverDefinitionNameBase )
        {
            int index = 1;
            String testName = driverDefinitionNameBase;
            while( DriverManager.getInstance().getDriverInstanceByName( testName ) != null )
            {
                index++;
                testName = driverDefinitionNameBase + String.valueOf( index );
            }
            return testName;
        }

        public void addDatabaseConnectionProfile( String connectionName, String driverPath )
            throws ConnectionProfileException
        {
            String uniqueDriverInstanceName = generateUniqueDriverDefinitionName( connectionName );
            DriverInstance driverInstance =
                DriverManager.getInstance().createNewDriverInstance(
                    driverTemplate, uniqueDriverInstanceName, driverPath, driverClass );

            String uniqueConnectionProfileName = generateUniqueConnectionProfileName( connectionName );

            Properties connectionProfileProperties = driverInstance.getPropertySet().getBaseProperties();
            final String databaseName = "lportal"; //$NON-NLS-1$
            connectionProfileProperties.setProperty(
                ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, driverInstance.getId() );
            connectionProfileProperties.setProperty( IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, databaseName );
            connectionProfileProperties.setProperty( IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, userName );
            connectionProfileProperties.setProperty( IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password );
            connectionProfileProperties.setProperty( IJDBCDriverDefinitionConstants.URL_PROP_ID, connectionUrl );

            ProfileManager.getInstance().createProfile(
                uniqueConnectionProfileName, connectionDesc, providerId, connectionProfileProperties, "", false ); //$NON-NLS-1$
        }

    }


    private LiferayDatabaseConnection getLiferayDBConnection(
        final String driverClass, final String userName, final String password, final String connectionUrl )
    {
        if( driverClass.equals( "com.mysql.jdbc.Driver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "com.mysql.jdbc.Driver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Mysql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate"; //$NON-NLS-1$

            return new MysqlLiferayDatabaseConnection(
                defaultDriverClass, providerId, connectionDesc, driverTemplate, userName, password, connectionUrl );
        }
        else if( driverClass.equals( "org.postgresql.Driver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "org.postgresql.Driver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.postgresql.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Posgresql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.postgresql.postgresqlDriverTemplate"; //$NON-NLS-1$

            return new PostgresqlLiferayDatabaseConnection(
                defaultDriverClass, providerId, connectionDesc, driverTemplate, userName, password, connectionUrl );
        }
        else if( driverClass == null || driverClass.equals( "org.hsqldb.jdbcDriver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.hsqldb.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Hsql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.hsqldb.1_8.driver"; //$NON-NLS-1$

            return new HsqlLiferayDatabaseConnection( defaultDriverClass, providerId, connectionDesc, driverTemplate );
        }
        return null;
    }

    private class MysqlLiferayDatabaseConnection extends LiferayDatabaseConnection
    {

        public MysqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate,
            final String userName, final String password, final String connectionUrl )
        {
            super( driverClass, providerId, connectinDesc, driverTemplate, userName, password, connectionUrl );
        }

    }

    private class PostgresqlLiferayDatabaseConnection extends LiferayDatabaseConnection
    {
        public PostgresqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate,
            final String userName, final String password, final String connectionUrl )
        {
            super( driverClass, providerId, connectinDesc, driverTemplate, userName, password, connectionUrl );
        }

    }

    private class HsqlLiferayDatabaseConnection extends LiferayDatabaseConnection
    {
        public HsqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate)
        {
            super( driverClass, providerId, connectinDesc, driverTemplate, defaultUserName, defaultPassword, defaultConnecionUrl );
        }

        private final static String defaultConnecionUrl = "jdbc:hsqldb:lportal"; //$NON-NLS-1$
        private final static String defaultUserName = "sa"; //$NON-NLS-1$
        private final static String defaultPassword = ""; //$NON-NLS-1$

    }

    private static class Msgs extends NLS
    {

        public static String noDBConnectDriver;
        public static String addDBConnnection;
        public static String addProfileError;
        public static String noDatabasePropertyFile;

        static
        {
            initializeMessages( CreateDBConnectAction.class.getName(), Msgs.class );
        }
    }
}
