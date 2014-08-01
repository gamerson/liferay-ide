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
import com.liferay.ide.server.core.AbstractLiferayDatabaseConnection;
import com.liferay.ide.server.core.ILiferayDBConnection;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Simon Jiang
 */
public class CreateDBConnectAction extends AbstractServerRunningAction
{
    public CreateDBConnectAction()
    {
        super();
    }

    protected ServerDelegate getLiferayServer()
    {
        return (ServerDelegate) selectedServer.loadAdapter( ILiferayServer.class, null );
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
            final ServerDelegate portalServer = getLiferayServer();
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
                        (String) pluginPackageProperties.getString(
                            "jdbc.default.driverClassName", "org.hsqldb.jdbcDriver" ); //$NON-NLS-1$ //$NON-NLS-2$

                    final String connectionName = liferayRuntime.getRuntime().getName();
                    final String userName = (String) pluginPackageProperties.getProperty( "jdbc.default.username" ); //$NON-NLS-1$
                    final String connectionUrl = (String) pluginPackageProperties.getProperty( "jdbc.default.url" ); //$NON-NLS-1$
                    final String password = (String) pluginPackageProperties.getProperty( "jdbc.default.password" ); //$NON-NLS-1$

                    final URL[] runtimeLibs = getLiferayRuntimeLibs( liferayRuntime );

                    final Class<?> classRef = new URLClassLoader( runtimeLibs ).loadClass( driverName );
                    if( classRef != null )
                    {
                        final String libPath = classRef.getProtectionDomain().getCodeSource().getLocation().getPath();
                        final String driverPath = new File( libPath ).getAbsolutePath();
                        new Job( Msgs.addDBConnnection )
                        {

                            @Override
                            protected IStatus run( IProgressMonitor monitor )
                            {
                                ILiferayDBConnection dbConnection = getLiferayDBConnection( driverName );
                                final String _userName = dbConnection.getUserName( userName );
                                final String _password = dbConnection.getPassword( password );
                                final String _connectionUrl = dbConnection.getConnectionUrl( connectionUrl );
                                dbConnection.addDatabaseConnectionProfile(
                                    connectionName, _connectionUrl, driverPath, _userName, _password );
                                return Status.OK_STATUS;
                            }

                        }.schedule();
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

    private ILiferayDBConnection getLiferayDBConnection( final String driverClass )
    {
        if( driverClass.equals( "com.mysql.jdbc.Driver" ) ) //$NON-NLS-1$
        {
            return new MysqlLiferayDatabaseConnection();
        }
        else if( driverClass.equals( "org.postgresql.Driver" ) ) //$NON-NLS-1$
        {
            return new PostgresqlLiferayDatabaseConnection();
        }
        else if( driverClass == null || driverClass.equals( "org.hsqldb.jdbcDriver" ) ) //$NON-NLS-1$
        {
            return new HsqlLiferayDatabaseConnection();
        }
        return null;
    }

    private class MysqlLiferayDatabaseConnection extends AbstractLiferayDatabaseConnection
        implements ILiferayDBConnection
    {


        private final static String defaultDriverClass = "com.mysql.jdbc.Driver"; //$NON-NLS-1$
        private final static String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile"; //$NON-NLS-1$
        private final static String connectionDesc = "Mysql Connection Profile"; //$NON-NLS-1$
        private final static String driverTemplate = "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate"; //$NON-NLS-1$

        @Override
        protected String getDriverTemplate()
        {
            return driverTemplate;
        }

        @Override
        protected String getConnectonProviderId()
        {
            return providerId;
        }

        @Override
        protected String getConnectionDescription()
        {
            return connectionDesc;
        }

        @Override
        protected String getDriverClass()
        {
            return defaultDriverClass;
        }

    }

    private class PostgresqlLiferayDatabaseConnection extends AbstractLiferayDatabaseConnection
        implements ILiferayDBConnection
    {

        private final static String defaultDriverClass = "org.postgresql.Driver"; //$NON-NLS-1$
        private final static String providerId = "org.eclipse.datatools.enablement.postgresql.connectionProfile"; //$NON-NLS-1$
        private final static String connectionDesc = "Posgresql Connection Profile"; //$NON-NLS-1$
        private final static String driverTemplate =
            "org.eclipse.datatools.enablement.postgresql.postgresqlDriverTemplate"; //$NON-NLS-1$

        @Override
        protected String getDriverTemplate()
        {
            return driverTemplate;
        }

        @Override
        protected String getConnectonProviderId()
        {
            return providerId;
        }

        @Override
        protected String getConnectionDescription()
        {
            return connectionDesc;
        }

        @Override
        protected String getDriverClass()
        {
            return defaultDriverClass;
        }

        @Override
        protected void addConnectionProfileAndDriverInstance( IConnectionProfile currentConnectionProfile )
            throws ConnectionProfileException
        {
            addDriverInstance( currentConnectionProfile );
            addConnectionProfile( currentConnectionProfile );
        }

    }

    private class HsqlLiferayDatabaseConnection extends AbstractLiferayDatabaseConnection
        implements ILiferayDBConnection
    {

        private final static String defaultConnecionUrl = "jdbc:hsqldb:lportal"; //$NON-NLS-1$
        private final static String defaultUserName = "sa"; //$NON-NLS-1$
        private final static String defaultPassword = ""; //$NON-NLS-1$
        private final static String defaultDriverClass = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$
        private final static String providerId = "org.eclipse.datatools.enablement.hsqldb.connectionProfile"; //$NON-NLS-1$
        private final static String connectionDesc = "Hsql Connection Profile"; //$NON-NLS-1$
        private final static String driverTemplate = "org.eclipse.datatools.enablement.hsqldb.2_1.driver"; //$NON-NLS-1$

        @Override
        protected String getDriverTemplate()
        {
            return driverTemplate;
        }

        @Override
        protected String getConnectonProviderId()
        {
            return providerId;
        }

        @Override
        protected String getConnectionDescription()
        {
            return connectionDesc;
        }

        @Override
        protected String getDriverClass()
        {
            return defaultDriverClass;
        }

        @Override
        public String getUserName( String userName )
        {
            if( CoreUtil.isNullOrEmpty( userName ) )
            {
                return defaultUserName;
            }
            return userName;
        }

        @Override
        public String getPassword( String password )
        {
            if( CoreUtil.isNullOrEmpty( password ) )
            {
                return defaultPassword;
            }
            return password;
        }

        @Override
        public String getConnectionUrl( String connectionUrl )
        {
            if( CoreUtil.isNullOrEmpty( connectionUrl ) )
            {
                return defaultConnecionUrl;
            }
            return connectionUrl;
        }

    }

    private static class Msgs extends NLS
    {

        public static String noDBConnectDriver;
        public static String addDBConnnection;

        static
        {
            initializeMessages( CreateDBConnectAction.class.getName(), Msgs.class );
        }
    }
}
