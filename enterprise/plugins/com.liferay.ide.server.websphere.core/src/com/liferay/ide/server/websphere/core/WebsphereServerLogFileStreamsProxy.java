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

            _sysoutFile = curWebsphereServer.getWebsphereOutLogLocation();
            _syserrFile = curWebsphereServer.getWebsphereErrLogLocation();

            if( systemOut != null )
            {
                _sysOut = systemOut;
            }
            else
            {
                _sysOut = new ServerOutputStreamMonitor();
            }
            if( systemErr != null )
            {
                _sysErr = systemErr;
            }
            else
            {
                _sysErr = new ServerOutputStreamMonitor();
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
