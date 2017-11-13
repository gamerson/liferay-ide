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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */

public class ServerStateRestartThread
{

    // delay before pinging starts
    private static final int PING_DELAY = 2000;

    // delay between pings
    private static final int PING_INTERVAL = 250;

    private boolean stop = false;
    private IProcess mointorProcess;
    private WebsphereServerBehavior behaviour;
    private long defaultTimeout = 15 * 60 * 1000;
    private long timeout = 0;
    private long startedTime;
    private String launchMode;
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
    public ServerStateRestartThread( IServer server, WebsphereServerBehavior behaviour, String launchMode )
    {
        super();
        this.server = server;
        this.behaviour = behaviour;
        this.mointorProcess = behaviour.getProcess();
        int serverStopTimeout = server.getStopTimeout();
        this.launchMode = launchMode;

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
                        behaviour.stop( true );
                        behaviour.setServerStoped();
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
                
                int serverState = server.getServerState();

                if ( serverState == IServer.STATE_STOPPED )
                {
                    boolean serverRestarting = behaviour.isServerRestarting();
                    
                    if ( serverRestarting )
                    {
                        Job restartJob = new Job(launchMode)
                        {
                            @Override
                            protected IStatus run( IProgressMonitor monitor )
                            {
                                try
                                {
                                    server.start( launchMode, new NullProgressMonitor() );
                                    behaviour.setServerRestarting( false );
                                    stop = true;
                                }
                                catch( CoreException e )
                                {
                                    return WebsphereCore.createErrorStatus( e );
                                }
                                return Status.OK_STATUS;
                            }
            
                        };
                        restartJob.setName( "Liferay Websphere RestartJob" );
                        restartJob.setUser( true );
                        restartJob.schedule();
                    }
                }
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
