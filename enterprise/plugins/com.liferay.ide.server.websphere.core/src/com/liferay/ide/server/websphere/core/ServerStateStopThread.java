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
    private WebsphereServerBehavior behaviour;
    private long defaultTimeout = 2 * 60 * 1000;
    private long timeout = 0;
    private long startedTime;

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
        this.behaviour = behaviour;
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
                        behaviour.stopModules();
                        behaviour.setServerStoped();
                        ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                        behaviour.terminateWebsphereProcess();
                        triggerCleanupEvent();
                        stop = true;
                    }
                    catch( Exception e )
                    {
                    }
                    break;
                }
                Thread.sleep( 1000 );

                if ( behaviour.isServerProcessTerminate() )
                {
                    Thread.sleep( 200 );
                    behaviour.setServerStoped();
                    ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                    behaviour.terminateWebsphereProcess();
                    triggerCleanupEvent();
                    stop = true;
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
