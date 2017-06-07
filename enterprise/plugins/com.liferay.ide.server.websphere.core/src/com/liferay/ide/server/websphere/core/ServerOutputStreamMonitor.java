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

import java.util.Vector;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;

/**
 * @author Simon Jiang
 */

public class ServerOutputStreamMonitor implements IFlushableStreamMonitor
{

    class StreamNotifier implements ISafeRunnable
    {

        private IStreamListener _listener;
        private String _text;

        public void handleException( Throwable exception )
        {
            WebsphereCore.logError( exception );
        }

        public void notifyAppend( String text )
        {
            if( text == null )
            {
                return;
            }
            _text = text;
            Object[] listeners = _listeners.toArray( new IStreamListener[_listeners.size() ]  );

            for( int i = 0; i < listeners.length; ++i )
            {
                _listener = ( (IStreamListener) listeners[i] );
                SafeRunner.run( this );
            }
            _listener = null;
            _text = null;
        }

        public void run() throws Exception
        {
            _listener.streamAppended( _text, ServerOutputStreamMonitor.this );
        }
    }

    Vector<IStreamListener> _listeners = new Vector<IStreamListener>( 1 );
    private boolean _buffered = true;

    private StringBuffer _contentsBuffer;

    public ServerOutputStreamMonitor()
    {
        _contentsBuffer = new StringBuffer();
    }

    public void addListener( IStreamListener listener )
    {
        _listeners.add( listener );
    }

    public void append( byte[] b, int start, int length )
    {
        if( ( b == null ) || ( start < 0 ) )
        {
            return;
        }
        append( new String( b, start, length ) );
    }

    public void append( String text )
    {
        if( text == null )
        {
            return;
        }
        if( isBuffered() )
        {
            _contentsBuffer.append( text );
        }
        new StreamNotifier().notifyAppend( text );
    }

    protected void close()
    {
        _listeners.removeAllElements();
    }

    public void flushContents()
    {
        _contentsBuffer.setLength( 0 );
    }

    public String getContents()
    {
        return _contentsBuffer.toString();
    }

    public boolean isBuffered()
    {
        return _buffered;
    }

    public void removeListener( IStreamListener listener )
    {
        _listeners.remove( listener );
    }

    public void setBuffered( boolean buffer )
    {
        _buffered = buffer;
    }
}
