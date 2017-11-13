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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class WebsphereMonitorProcess implements IProcess
{

    protected WebsphereServerBehavior serverBehavior;
    protected String label;
    protected ILaunch launch;
    protected IServer server;
    protected ITerminateableStreamsProxy streamsProxy;
    protected IWebsphereServer websphereServer;

    protected Map<String, String> map = new HashMap<String, String>();

    public WebsphereMonitorProcess(
        IServer server, final WebsphereServerBehavior serverBehavior, ILaunch launch, ITerminateableStreamsProxy proxy )
    {
        this.server = server;
        this.websphereServer = (IWebsphereServer) server.loadAdapter( IWebsphereServer.class, null );
        this.serverBehavior = serverBehavior;
        this.streamsProxy = proxy;
        this.launch = launch;
    }

    public boolean canTerminate()
    {
        return( ( serverBehavior.isLocalHost() ) && ( !( streamsProxy.isTerminated() ) ) );
    }

    public <T> T getAdapter( Class<T> adapterType )
    {
        return (T) null;
    }

    public String getAttribute( String key )
    {
        return( (String) this.map.get( key ) );
    }

    public int getExitValue() throws DebugException
    {
        return 0;
    }

    public String getLabel()
    {
        if( this.label == null )
        {
            String host = null;
            String port = null;

            if( server != null )
            {
                host = server.getHost();
            }

            IWebsphereServer wasServer = (IWebsphereServer) server.loadAdapter( IWebsphereServer.class, null );

            if( wasServer != null )
            {
                port = wasServer.getWebsphereSOAPPort();
            }

            this.label = ( host != null ? host : "" ) + ":" + ( port != null ? port : "" );
        }

        return this.label;
    }

    public ILaunch getLaunch()
    {
        return launch;
    }

    public IStreamsProxy getStreamsProxy()
    {
        return streamsProxy;
    }

    public boolean isTerminated()
    {
        return streamsProxy.isTerminated();
    }

    public void setAttribute( String key, String value )
    {
        this.map.put( key, value );
    }

    public void setStreamsProxy( ITerminateableStreamsProxy streamsProxy )
    {
        this.streamsProxy = streamsProxy;
    }

    public void terminate() throws DebugException
    {
        if( ( server != null ) && ( serverBehavior.isLocalHost() ) )
        {
            serverBehavior.stop( false );
        }
    }
}
