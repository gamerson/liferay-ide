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

/**
 * @author Simon Jiang
 */

public class WebsphereServerLogFileStreamsProxy extends WebsphereServerFileStreamsProxy
{

    private WebsphereServerBehavior serverBehaviour;
    private ILaunch launch;

    public WebsphereServerLogFileStreamsProxy(
        IWebsphereServer curWebsphereServer, WebsphereServerBehavior curServerBehaviour, ILaunch curLaunch )
    {
        this( curWebsphereServer, curServerBehaviour, curLaunch, new ServerOutputStreamMonitor(), new ServerOutputStreamMonitor() );
    }

    public WebsphereServerLogFileStreamsProxy(
        IWebsphereServer curWebsphereServer, WebsphereServerBehavior curServerBehaviour, 
        ILaunch curLaunch, ServerOutputStreamMonitor systemOut, ServerOutputStreamMonitor systemErr )
    {
        this.launch = null;

        if( ( curWebsphereServer == null ) || ( curServerBehaviour == null ) )
        {
            return;
        }

        this.serverBehaviour = curServerBehaviour;

        this.launch = curLaunch;
        try
        {

            this._sysoutFile = curWebsphereServer.getWebsphereOutLogLocation();
            this._syserrFile = curWebsphereServer.getWebsphereErrLogLocation();

            if( systemOut != null )
            {
                this._sysOut = systemOut;
            }
            else
            {
                this._sysOut = new ServerOutputStreamMonitor();
            }
            if( systemErr != null )
            {
                this._sysErr = systemErr;
            }
            else
            {
                this._sysErr = new ServerOutputStreamMonitor();
            }
            startMonitoring();
        }
        catch( Exception e )
        {
        }
    }

    public ILaunch getLaunch()
    {
        return launch;
    }

    public WebsphereServerBehavior getServerBehaviour()
    {
        return serverBehaviour;
    }
}
