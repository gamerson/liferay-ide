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
package com.liferay.ide.server.core;

import java.util.Properties;

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
import org.eclipse.osgi.util.NLS;


/**
 * @author Simon Jiang
 */

public abstract class AbstractLiferayDatabaseConnection implements ILiferayDBConnection
{

    public void addDatabaseConnectionProfile(
        String connectionName, String connectionUrl, String driverPath, String userName, String password )
    {
        IConnectionProfile mysqlProfile = ProfileManager.getInstance().getProfileByName( connectionName );
        if( mysqlProfile == null )
        {
            mysqlProfile =
                new ConnectionProfile( connectionName, getConnectionDescription(), getConnectonProviderId(), null, true );
            Properties properties = new Properties();
            properties.setProperty( IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, getDriverClass() );
            properties.setProperty( IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, "lportal" ); //$NON-NLS-1$
            properties.setProperty( IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password );
            properties.setProperty( IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, "true" ); //$NON-NLS-1$
            properties.setProperty( IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, userName );
            properties.setProperty( IDriverMgmtConstants.PROP_DEFN_TYPE, getDriverTemplate() );
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
    
    protected abstract String getDriverClass();

    protected abstract String getDriverTemplate();

    protected abstract String getConnectonProviderId();

    protected abstract String getConnectionDescription();

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
                    createNewDriverInstance(
                        driverTemplateID, uniqueDriverInstanceName, jarList, driverClass, baseProperties );
                if( newDriverInstance != null )
                {
                    baseProperties.setProperty(
                        ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, newDriverInstance.getId() );
                    currentConnectionProfile.setProperties( currentConnectionProfile.getProviderId(), baseProperties );
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
        props.setProperty( "org.eclipse.datatools.connectivity.db.driverClass", driverClass );
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

    private static class Msgs extends NLS
    {

        public static String addProfileError;

        static
        {
            initializeMessages( AbstractLiferayDatabaseConnection.class.getName(), Msgs.class );
        }
    }
}
