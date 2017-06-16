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

import com.liferay.ide.core.util.CoreUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
    private IWebsphereServer websphereServer;
    private ProcessBuilder checkServerStatusProcessBuilder;
    private String expectedStatusResponse;
    private URL liferayHomeUrl;

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

        liferayHomeUrl = websphereServer.getPortalHomeUrl();
        IPath profilePath = new Path( websphereServer.getWebsphereProfileLocation() );
        String websphereServerName = websphereServer.getWebsphereServerName();
        IPath checkServerStatusCommandPath = null;

        if( CoreUtil.isWindows() )
        {
            checkServerStatusCommandPath = profilePath.append( "bin" ).append( "serverStatus.bat" );
        }
        else
        {
            checkServerStatusCommandPath = profilePath.append( "bin" ).append( "serverStatus.sh" );
        }
        List<String> checkServerStatusCommandList =
            Arrays.asList( checkServerStatusCommandPath.toOSString(), websphereServerName );
        checkServerStatusProcessBuilder = new ProcessBuilder( checkServerStatusCommandList );
        expectedStatusResponse = "The Application Server " + "\"" + websphereServerName + "\"" + " is STARTED";

        Thread t = new Thread( "Liferay WebsphereServerBehavior Start Thread")
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
                        Process statusProcess = checkServerStatusProcessBuilder.start();
                        String response = IOUtils.toString( statusProcess.getInputStream() );

                        if( !response.contains( expectedStatusResponse ) )
                        {
                            server.stop( false );
                            mointorProcess.terminate();
                        }
                        else
                        {
                            behaviour.setupAgentAndJMX();
                            Thread.sleep( 200 );
                            behaviour.setServerStarted();
                            behaviour.startModules();
                            stop = true;
                            behaviour.setServerProcessTerminate( false );
                        }
                    }
                    catch( Exception e )
                    {
                    }
                    stop = true;
                    break;
                }

                URLConnection conn = liferayHomeUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                int code = ( (HttpURLConnection) conn ).getResponseCode();

                if( !stop && code != 404 )
                {
                    behaviour.setupAgentAndJMX();
                    Thread.sleep( 200 );
                    behaviour.setServerStarted();
                    behaviour.startModules();
                    stop = true;
                    behaviour.setServerProcessTerminate( false );
                }

                Thread.sleep( 1000 );
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

    /**
     * Tell the pinging to stop.
     */
    public void stop()
    {
        stop = true;
    }
}
