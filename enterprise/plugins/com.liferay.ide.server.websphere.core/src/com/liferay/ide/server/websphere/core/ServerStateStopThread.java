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

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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
    private String url;
    private IProcess mointorProcess;
    private WebsphereServerBehavior behaviour;
    private long defaultTimeout = 5 * 60 * 1000;
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
    public ServerStateStopThread( IServer server, String url, WebsphereServerBehavior behaviour )
    {
        super();
        this.url = url;
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
                        behaviour.setServerStoped();
                        ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                        triggerCleanupEvent();
                    }
                    catch( Exception e )
                    {
                    }
                    stop = true;
                    break;
                }

                URL pingUrl = new URL( url );
                URLConnection conn = pingUrl.openConnection();

                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                int code = ( (HttpURLConnection) conn ).getResponseCode();
                System.out.println( "Response code is " + code );

                // ping worked - server is up
                if( !stop && code == 404 )
                {
                    Thread.sleep( 200 );
                    behaviour.setServerStoped();
                    behaviour.stopModules();
                    ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                    triggerCleanupEvent();
                    stop = true;
                }
                Thread.sleep( 1000 );
            }
            catch( ConnectException e )
            {
                // pinging failed
                if( !stop )
                {
                    try
                    {
                        behaviour.setServerStoped();
                        behaviour.stopModules();
                        ( (ITerminateableStreamsProxy) mointorProcess.getStreamsProxy() ).terminate();
                        triggerCleanupEvent();
                        stop = true;
                    }
                    catch( Exception e2 )
                    {
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
