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

import org.eclipse.debug.core.ILaunch;

public class WebsphereServerLogStreamsProxy extends WebsphereFileStreamsProxy
{

    private boolean isRestartingServer;
    private WebsphereServerBehavior serverBehaviour;
    private Integer debugPortNum;
    private boolean isDebug;
    private boolean isWaitForDebugAttach;
    private ILaunch launch;

    public WebsphereServerLogStreamsProxy(
        IWebsphereServer curWebsphereServer, WebsphereServerBehavior curServerBehaviour, boolean curIsRestartingServer,
        boolean curIsDebug, ILaunch curLaunch )
    {
        this( curWebsphereServer, curServerBehaviour, curIsRestartingServer, curIsDebug, curLaunch, new OutputStreamMonitor(), new OutputStreamMonitor() );
    }

    public WebsphereServerLogStreamsProxy(
        IWebsphereServer curWebsphereServer, WebsphereServerBehavior curServerBehaviour, boolean curIsRestartingServer,
        boolean curIsDebug, ILaunch curLaunch, OutputStreamMonitor systemOut, OutputStreamMonitor systemErr )
    {
        this.isRestartingServer = false;

        this.debugPortNum = null;
        this.isDebug = false;
        this.isWaitForDebugAttach = false;
        this.launch = null;

        if( ( curWebsphereServer == null ) || ( curServerBehaviour == null ) )
        {
            return;
        }

        this.serverBehaviour = curServerBehaviour;
        this.isRestartingServer = curIsRestartingServer;

        this.isPaused = curIsRestartingServer;
        this.isDebug = curIsDebug;
        this.launch = curLaunch;
        try
        {

            this.sysoutFile = curWebsphereServer.getWebsphereOutLogLocation();
            this.syserrFile = curWebsphereServer.getWebsphereErrLogLocation();

            if( systemOut != null )
            {
                this.sysOut = systemOut;
            }
            else
            {
                this.sysOut = new OutputStreamMonitor();
            }
            if( systemErr != null )
            {
                this.sysErr = systemErr;
            }
            else
            {
                this.sysErr = new OutputStreamMonitor();
            }
            startMonitoring();
        }
        catch( Exception e )
        {
        }
    }

    public Integer getDebugPortNum()
    {
        return debugPortNum;
    }

    public ILaunch getLaunch()
    {
        return launch;
    }

    public WebsphereServerBehavior getServerBehaviour()
    {
        return serverBehaviour;
    }

    public boolean isDebug()
    {
        return isDebug;
    }

    public boolean isRestartingServer()
    {
        return isRestartingServer;
    }

    public boolean isWaitForDebugAttach()
    {
        return isWaitForDebugAttach;
    }
}
