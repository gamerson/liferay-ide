/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.websphere.core;

import com.liferay.ide.server.websphere.admin.IWebsphereAdminService;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */

public class ServerStateStartThread
{

    // delay before pinging starts
    private static final int PING_DELAY = 2000;

    // delay between pings
    private static final int PING_INTERVAL = 250;

    private boolean stop = false;
    private IProcess mointorProcess;
    private WebsphereServerBehavior behaviour;
    private IServer server;
    private long defaultTimeout = 15 * 60 * 1000;
    private long timeout = 0;
    private long startedTime;
    private IWebsphereAdminService wasService;
    private IWebsphereServer websphereServer;

    /**
     * Create a new PingThread.
     *
     * @param server
     * @param url
     * @param maxPings
     *            the maximum number of times to try pinging, or -1 to continue forever
     * @param behaviour
     */
    public ServerStateStartThread( IServer server, WebsphereServerBehavior behaviour )
    {
        super();
        this.server = server;
        this.behaviour = behaviour;
        this.mointorProcess = behaviour.getProcess();
        this.websphereServer = (IWebsphereServer) this.server.loadAdapter( IWebsphereServer.class, null );

        int serverStartTimeout = server.getStartTimeout();

        if( serverStartTimeout < defaultTimeout / 1000 )
        {
            this.timeout = defaultTimeout;
        }
        else
        {
            this.timeout = serverStartTimeout * 1000;
        }

        Thread t = new Thread( "Liferay WebsphereServerBehavior Ping Thread")
        {

            public void run()
            {
                startedTime = System.currentTimeMillis();
                ping( );
            }
        };
        t.setDaemon( true );
        t.start();
    }

    /**
     * Ping the server until it is started. Then set the server state to STATE_STARTED.
     */
    protected void ping()
    {
        long currentTime = 0;
        try
        {
            Thread.sleep( PING_DELAY );
        }
        catch( Exception e )
        {
        }
        while( !stop )
        {
            try
            {
                currentTime = System.currentTimeMillis();
                if( ( currentTime - startedTime ) > timeout )
                {
                    try
                    {
                        server.stop( false );
                        mointorProcess.terminate();
                    }
                    catch( Exception e )
                    {
                    }
                    stop = true;
                    break;
                }

                if( wasService == null )
                {
                    wasService = WebsphereCore.getWebsphereAdminService( websphereServer );
                }
                else
                {
                    if( wasService.isAlive() )
                    {
                        String serverState = wasService.getServerState();

                        if( serverState.equals( "STARTED" ) )
                        {
                            behaviour.setupAgentAndJMX();
                            Thread.sleep( 200 );
                            behaviour.setServerStarted();
                            behaviour.startModules();
                            stop = true;
                        }
                    }
                }
                Thread.sleep( 1000 );
            }
            catch( Exception e )
            {
                // pinging failed
                if( !stop )
                {
                    try
                    {
                        Thread.sleep( PING_INTERVAL );
                    }
                    catch( InterruptedException e2 )
                    {
                    }
                }
            }
        }
    }

    /**
     * Tell the pinging to stop.
     */
    public void stop()
    {
        stop = true;
    }
}
