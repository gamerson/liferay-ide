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

import com.liferay.ide.server.websphere.util.WebsphereUtil;

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

            IWebsphereServer wasServer = WebsphereUtil.getWebsphereServer( server );

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
