/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
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

package com.liferay.ide.server.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class ServerManagerTests extends ServerCoreBase
{

    private final static String portalSetupWizardFileName = "portal-setup-wizard.properties";
    private final static String remoteIDEConnectorLPKGFileName = "Remote IDE Connector CE.lpkg";
    private static IServerManagerConnection service;
    private final static String testApplicationPartialModificationWarFileName = "test-portlet-partial-modification.war";
    private final static String testApplicationPartialDeletionWarFileName = "test-portlet-partial-deletion.war";
    private final static String testApplicationWarFileName = "test-portlet.war";

    protected File getTestApplicationWar()
    {
        return getProjectFile( "files", testApplicationWarFileName );
    }

    protected File getTestApplicationPartialModificationWar()
    {
        return getProjectFile( "files", testApplicationPartialModificationWarFileName );
    }

    protected File getTestApplicationPartialDeletionWar()
    {
        return getProjectFile( "files", testApplicationPartialDeletionWarFileName );
    }

    protected void ping( Server server )
    {
        int count = 0;

        boolean stop = false;

        while( !stop )
        {
            try
            {
                if( count == 1000 )
                {
                    try
                    {
                        server.stop( false );
                    }
                    catch( Exception e )
                    {
                    }

                    stop = true;

                    break;
                }
                count++;

                URL pingUrl = new URL( "http://localhost:" + liferayServerPort );

                URLConnection conn = pingUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                ( (HttpURLConnection) conn ).getResponseCode();

                if( !stop )
                {
                    Thread.sleep( 200 );
                    server.setServerState( IServer.STATE_STARTED );
                }

                stop = true;
            }
            catch( Exception e )
            {
                if( !stop )
                {
                    try
                    {
                        Thread.sleep( 250 );
                    }
                    catch( Exception e2 )
                    {
                    }
                }
            }
        }
    }

    @Before
    public void startServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        final IServer server = getServer();

        changeServerXmlPort( "8080", liferayServerPort );

        copyFileToServer( server, "deploy", "files", remoteIDEConnectorLPKGFileName );

        copyFileToServer( server, "", "files", portalSetupWizardFileName );

        if( server.getServerState() == IServer.STATE_STOPPED )
        {
            Thread serverThread = new Thread( "Server Thread" )
            {

                public void run()
                {
                    try
                    {
                        server.start( ILaunchManager.DEBUG_MODE, npm );
                    }
                    catch( CoreException e )
                    {
                    }
                }
            };

            serverThread.start();

            ping( (Server) server );
        }

        assertEquals( "Expected server has started", IServer.STATE_STARTED, server.getServerState() );

        service = new ServerManagerConnection();

        service.setHost( "localhost" );
        service.setHttpPort( liferayServerPort );
        service.setManagerContextPath( "/server-manager-web" );
        service.setUsername( "test@liferay.com" );
        service.setPassword( "test" );

        // Given the server 10 seconds to deploy remote IDE Connector plugin
        try
        {
            Thread.sleep( 10000 );
        }
        catch( Exception e )
        {
        }

        assertEquals( "Expected the remote connection's status should be alive", true, service.isAlive() );
    }

    @After
    public void stopServer() throws Exception
    {
        IServer server = getServer();

        if( server.getServerState() != IServer.STATE_STOPPED )
        {
            server.stop( true );

            changeServerXmlPort( liferayServerPort, "8080" );
        }
    }

    @Test
    public void testDeployApplication() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        assertEquals( "Expected the server doesn't have debug port", -1, service.getDebugPort() );

        assertEquals( "Expected the server state is started", "STARTED", service.getServerState() );

        Object result = service.installApplication( getTestApplicationWar().getAbsolutePath(), "test-application", npm );

        assertEquals( "Expected the Test Application has been installed", null, result );

        result = service.isAppInstalled( "test-application" );

        assertEquals( "Expected the Test Application has been installed", true, result );

        assertNotNull( service.getLiferayPlugins() );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialModificationWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Modified Test Portlet is success", null, result );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialDeletionWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Deletion Test Portlet is success", null, result );

        result = service.uninstallApplication( "test-application", npm );

        assertEquals( "Expected uninstall the Test Portlet is success", null, result );
    }

}
