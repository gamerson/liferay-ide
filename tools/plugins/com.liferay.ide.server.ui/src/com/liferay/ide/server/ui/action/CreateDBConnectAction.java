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
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.DriverMgmtMessages;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.drivers.models.TemplateDescriptor;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
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

    protected ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) selectedServer.loadAdapter( ILiferayServer.class, null );
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    private Properties getDatabaseProperties( IPath bundlePath )
    {
        IPath bundleExtPath = bundlePath.append( "portal-ext.properties" ); //$NON-NLS-1$
        Properties pluginPackageProperties = new Properties();
        try
        {
            if( bundleExtPath.toFile().exists() )
            {
                pluginPackageProperties.load( new FileInputStream( bundleExtPath.toFile() ) );
                final String driverName = (String) pluginPackageProperties.getProperty( "jdbc.default.driverClassName" ); //$NON-NLS-1$
                if( CoreUtil.isNullOrEmpty( driverName ) )
                {
                    IPath setupWizardPath = bundlePath.append( "portal-setup-wizard.properties" ); //$NON-NLS-1$
                    if( setupWizardPath.toFile().exists() )
                    {
                        pluginPackageProperties.load( new FileInputStream( setupWizardPath.toFile() ) );
                        return pluginPackageProperties;
                    }
                }
            }
            else
            {
                IPath setupWizardPath = bundlePath.append( "portal-setup-wizard.properties" ); //$NON-NLS-1$
                if( setupWizardPath.toFile().exists() )
                {
                    pluginPackageProperties.load( new FileInputStream( setupWizardPath.toFile() ) );
                    return pluginPackageProperties;
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
            final ILiferayServer portalServer = getLiferayServer();
            ILiferayRuntime liferayRuntime =
                ServerUtil.getLiferayRuntime( ( (ServerDelegate) portalServer ).getServer() );

            IPath bundlePath = liferayRuntime.getAppServerDir().removeLastSegments( 1 );
            Properties pluginPackageProperties = getDatabaseProperties( bundlePath );
            final String driverName =
                (String) pluginPackageProperties.getProperty( "jdbc.default.driverClassName", "org.hsqldb.jdbcDriver" ); //$NON-NLS-1$ //$NON-NLS-2$

            final String connectionName = liferayRuntime.getRuntime().getName();
            final String userName = (String) pluginPackageProperties.getProperty( "jdbc.default.username" ); //$NON-NLS-1$
            final String connectionUrl = (String) pluginPackageProperties.getProperty( "jdbc.default.url" ); //$NON-NLS-1$
            final String password = (String) pluginPackageProperties.getProperty( "jdbc.default.password" ); //$NON-NLS-1$

            try
            {
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
                            ILiferayDatabaseConnection dbConnection = getLiferayDBConnection( driverName );
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

    private interface ILiferayDatabaseConnection
    {

        public void addDatabaseConnectionProfile(
            String connectionName, String connectionUrl, String driverPath, String userName, String password );

        public String getUserName( String userName );

        public String getPassword( String password );

        public String getConnectionUrl( String connectionUrl );
    }

    private class LiferayDatabaseConnection implements ILiferayDatabaseConnection
    {

        private String driverClass;
        private String providerId;
        private String connectionDesc;
        private String driverTemplate;

        public LiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate )
        {
            super();
            this.driverClass = driverClass;
            this.providerId = providerId;
            this.connectionDesc = connectinDesc;
            this.driverTemplate = driverTemplate;
        }

        public void addDatabaseConnectionProfile(
            String connectionName, String connectionUrl, String driverPath, String userName, String password )
        {
            IConnectionProfile mysqlProfile = ProfileManager.getInstance().getProfileByName( connectionName );
            if( mysqlProfile == null )
            {
                mysqlProfile = new ConnectionProfile( connectionName, connectionDesc, providerId, null, true );
                Properties properties = new Properties();
                properties.setProperty( IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, driverClass);
                properties.setProperty( IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, "lportal" ); //$NON-NLS-1$
                properties.setProperty( IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password );
                properties.setProperty( IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, "true" ); //$NON-NLS-1$
                properties.setProperty( IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, userName );
                properties.setProperty( IDriverMgmtConstants.PROP_DEFN_TYPE, driverTemplate );
                properties.setProperty( IJDBCDriverDefinitionConstants.URL_PROP_ID, connectionUrl );
                properties.setProperty( ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, connectionName );
                properties.setProperty( IDriverMgmtConstants.PROP_DEFN_JARLIST, driverPath );
                mysqlProfile.setBaseProperties( properties );

                try
                {
                    addConnectionProfileAndDriverInstance( mysqlProfile );
                }
                catch( ConnectionProfileException e )
                {
                    LiferayServerCore.logError( Msgs.addProfileError, e );
                }

            }
        }

        public String getUserName( String userName )
        {
            return userName;
        }

        public String getPassword( String password )
        {
            return password;
        }

        public String getConnectionUrl( String connectionUrl )
        {
            return connectionUrl;
        }

        protected void addConnectionProfileAndDriverInstance( IConnectionProfile currentConnectionProfile )
            throws ConnectionProfileException
        {
            addDriverInstance( currentConnectionProfile );
            addConnectionProfile( currentConnectionProfile );
        }

        protected void addConnectionProfile( IConnectionProfile currentConnectionProfile )
            throws ConnectionProfileException
        {
            ProfileManager.getInstance().addProfile( currentConnectionProfile );
        }

        protected void addDriverInstance( IConnectionProfile currentConnectionProfile )
            throws ConnectionProfileException
        {
            Properties baseProperties = currentConnectionProfile.getBaseProperties();
            final String jarList = baseProperties.getProperty( IDriverMgmtConstants.PROP_DEFN_JARLIST, null );
            final String driverDefinitionID =
                baseProperties.getProperty( ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, null );
            final String driverTemplateID = baseProperties.getProperty( IDriverMgmtConstants.PROP_DEFN_TYPE, null );
            final TemplateDescriptor templateDescriptor =
                TemplateDescriptor.getDriverTemplateDescriptor( driverTemplateID );

            final DriverInstance driverInstance =
                DriverManager.getInstance().getDriverInstanceByID( driverDefinitionID );
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
                    final String uniqueDriverInstanceName =
                        generateUniqueDriverDefinitionName( driverDefinitionNameBase );
                    final DriverInstance newDriverInstance =
                        createNewDriverInstance(
                            driverTemplateID, uniqueDriverInstanceName, jarList, driverClass, baseProperties );
                    if( newDriverInstance != null )
                    {
                        baseProperties.setProperty(
                            ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, newDriverInstance.getId() );
                        currentConnectionProfile.setProperties(
                            currentConnectionProfile.getProviderId(), baseProperties );
                    }
                }
            }
        }

        public DriverInstance createNewDriverInstance(
            String templateID, String name, String jarList, String driverClass, Properties baseProperties )
        {
            if( templateID == null )
                return null;
            if( name == null )
                return null;

            IPropertySet pset = DriverManager.getInstance().createDefaultInstance( templateID );

            // if for some reason, we get back a null, pass that back
            if( pset == null )
                return null;

            if( name != null )
                pset.setName( name );
            String prefix = DriverMgmtMessages.getString( "NewDriverDialog.text.id_prefix" ); //$NON-NLS-1$
            String id = prefix + templateID + "." + name; //$NON-NLS-1$
            pset.setID( id );
            Properties props = pset.getBaseProperties();
            if( jarList != null )
                props.setProperty( IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList );
            props.setProperty( "org.eclipse.datatools.connectivity.db.driverClass", driverClass ); //$NON-NLS-1$
            pset.setBaseProperties( baseProperties );
            DriverManager.getInstance().addDriverInstance( pset );
            return DriverManager.getInstance().getDriverInstanceByID( pset.getID() );
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

    }

    private ILiferayDatabaseConnection getLiferayDBConnection( final String driverClass )
    {
        if( driverClass.equals( "com.mysql.jdbc.Driver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "com.mysql.jdbc.Driver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Mysql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate"; //$NON-NLS-1$

            return new MysqlLiferayDatabaseConnection( defaultDriverClass, providerId, connectionDesc, driverTemplate );
        }
        else if( driverClass.equals( "org.postgresql.Driver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "org.postgresql.Driver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.postgresql.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Posgresql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.postgresql.postgresqlDriverTemplate"; //$NON-NLS-1$

            return new PostgresqlLiferayDatabaseConnection(
                defaultDriverClass, providerId, connectionDesc, driverTemplate );
        }
        else if( driverClass == null || driverClass.equals( "org.hsqldb.jdbcDriver" ) ) //$NON-NLS-1$
        {
            final String defaultDriverClass = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$
            final String providerId = "org.eclipse.datatools.enablement.hsqldb.connectionProfile"; //$NON-NLS-1$
            final String connectionDesc = "Hsql Connection Profile"; //$NON-NLS-1$
            final String driverTemplate = "org.eclipse.datatools.enablement.hsqldb.2_1.driver"; //$NON-NLS-1$

            return new HsqlLiferayDatabaseConnection( defaultDriverClass, providerId, connectionDesc, driverTemplate );
        }
        return null;
    }

    private class MysqlLiferayDatabaseConnection extends LiferayDatabaseConnection
    {

        public MysqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate )
        {
            super( driverClass, providerId, connectinDesc, driverTemplate );
        }

    }

    private class PostgresqlLiferayDatabaseConnection extends LiferayDatabaseConnection
        implements ILiferayDatabaseConnection
    {


        public PostgresqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate )
        {
            super( driverClass, providerId, connectinDesc, driverTemplate );
        }

        @Override
        protected void addConnectionProfileAndDriverInstance( IConnectionProfile currentConnectionProfile )
            throws ConnectionProfileException
        {
            addDriverInstance( currentConnectionProfile );
            addConnectionProfile( currentConnectionProfile );
        }

    }

    private class HsqlLiferayDatabaseConnection extends LiferayDatabaseConnection
    {

        public HsqlLiferayDatabaseConnection(
            final String driverClass, final String providerId, final String connectinDesc, final String driverTemplate )
        {
            super( driverClass, providerId, connectinDesc, driverTemplate );
        }

        private final static String defaultConnecionUrl = "jdbc:hsqldb:lportal"; //$NON-NLS-1$
        private final static String defaultUserName = "sa"; //$NON-NLS-1$
        private final static String defaultPassword = ""; //$NON-NLS-1$

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
        public static String addProfileError;
        public static String noDatabasePropertyFile;

        static
        {
            initializeMessages( CreateDBConnectAction.class.getName(), Msgs.class );
        }
    }
}
