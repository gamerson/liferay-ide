/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.core.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author Tao Tao
 */
public class RemoteServerSocketFactory implements SchemeSocketFactory
{

    public Socket createSocket( final HttpParams params ) throws IOException
    {
        if( params == null )
        {
            throw new IllegalArgumentException( "HTTP parameters may not be null" ); //$NON-NLS-1$
        }

        String proxyHost = (String) params.getParameter( "socks.host" ); //$NON-NLS-1$
        Integer proxyPort = (Integer) params.getParameter( "socks.port" ); //$NON-NLS-1$
        InetSocketAddress socksaddr = new InetSocketAddress( proxyHost, proxyPort );
        Proxy proxy = new Proxy( Proxy.Type.SOCKS, socksaddr );

        return new Socket( proxy );
    }

    public Socket connectSocket(
        final Socket socket, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress,
        final HttpParams params ) throws IOException, UnknownHostException, ConnectTimeoutException
    {
        if( remoteAddress == null )
        {
            throw new IllegalArgumentException( "Remote address may not be null" ); //$NON-NLS-1$
        }

        if( params == null )
        {
            throw new IllegalArgumentException( "HTTP parameters may not be null" ); //$NON-NLS-1$
        }

        Socket sock;

        if( socket != null )
        {
            sock = socket;
        }
        else
        {
            sock = createSocket( params );
        }

        if( localAddress != null )
        {
            sock.setReuseAddress( HttpConnectionParams.getSoReuseaddr( params ) );
            sock.bind( localAddress );
        }

        int timeout = HttpConnectionParams.getConnectionTimeout( params );

        try
        {
            sock.connect( remoteAddress, timeout );
        }
        catch( SocketTimeoutException ex )
        {
            throw new ConnectTimeoutException( "Connect to " //$NON-NLS-1$
                +
                remoteAddress.getHostName() + "/" //$NON-NLS-1$
                + remoteAddress.getAddress() + " timed out" ); //$NON-NLS-1$
        }

        return sock;
    }

    public boolean isSecure( final Socket sock ) throws IllegalArgumentException
    {
        return false;
    }
}
