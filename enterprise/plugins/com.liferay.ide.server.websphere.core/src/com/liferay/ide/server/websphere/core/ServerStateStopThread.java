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
package com.liferay.ide.server.websphere.core;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */

public class ServerStateStopThread
{

    // delay before pinging starts
    private static final int PING_DELAY = 2000;

    // delay between pings
    private static final int PING_INTERVAL = 250;

    private boolean stop = false;
    private IProcess mointorProcess;
    private long defaultTimeout = 2 * 60 * 1000;
    private long timeout = 0;
    private long startedTime;
    private IServer server;

    public IProcess getMonitorProcess()
    {
        return this.mointorProcess;
    }

    /**
     * Create a new PingThread.
     *
     * @param server
     * @param url
     * @param maxPings
     *            the maximum number of times to try pinging, or -1 to continue forever
     * @param behaviour
     */
    public ServerStateStopThread( IServer server, WebsphereServerBehavior behaviour )
    {
        super();
        this.server = server;
        this.mointorProcess = behaviour.getProcess();
        int serverStopTimeout = server.getStopTimeout();

        if( serverStopTimeout < defaultTimeout / 1000 )
        {
            this.timeout = defaultTimeout;
        }
        else
        {
            this.timeout = serverStopTimeout * 1000;
        }

        Thread t = new Thread( "Liferay WebsphereServerBehavior Stop Thread")
        {

            public void run()
            {
                startedTime = System.currentTimeMillis();
                startMonitor( );
            }
        };
        t.setDaemon( true );
        t.start();
    }

    /**
     * Ping the server until it is started. Then set the server state to STATE_STARTED.
     */
    protected void startMonitor()
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
                        ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                        triggerCleanupEvent();
                        stop = true;
                    }
                    catch( Exception e )
                    {
                    }
                    break;
                }
                Thread.sleep( 1000 );

                if ( server.getServerState() == IServer.STATE_STOPPED )
                {
                    Thread.sleep( 200 );
                    ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                    stop = true;
                }
            }
            catch( Exception e )
            {
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

    private void triggerCleanupEvent()
    {
        DebugEvent event = new DebugEvent( this, DebugEvent.TERMINATE );
        DebugPlugin.getDefault().fireDebugEventSet( new DebugEvent[] { event } );
    }

    /**
     * Tell the pinging to stop.
     */
    public void stop()
    {
        stop = true;
    }
}
