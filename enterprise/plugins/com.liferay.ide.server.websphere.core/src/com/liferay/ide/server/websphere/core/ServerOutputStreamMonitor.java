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

import java.util.Vector;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;

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
            this._text = text;
            Object[] listeners = _listeners.toArray( new IStreamListener[_listeners.size() ]  );

            for( int i = 0; i < listeners.length; ++i )
            {
                this._listener = ( (IStreamListener) listeners[i] );
                SafeRunner.run( this );
            }
            this._listener = null;
            this._text = null;
        }

        public void run() throws Exception
        {
            this._listener.streamAppended( this._text, ServerOutputStreamMonitor.this );
        }
    }

    Vector<IStreamListener> _listeners = new Vector<IStreamListener>( 1 );
    private boolean _buffered = true;

    private StringBuffer _contentsBuffer;

    public ServerOutputStreamMonitor()
    {
        this._contentsBuffer = new StringBuffer();
    }

    public void addListener( IStreamListener listener )
    {
        this._listeners.add( listener );
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
            this._contentsBuffer.append( text );
        }
        new StreamNotifier().notifyAppend( text );
    }

    protected void close()
    {
        this._listeners.removeAllElements();
    }

    public void flushContents()
    {
        this._contentsBuffer.setLength( 0 );
    }

    public String getContents()
    {
        return this._contentsBuffer.toString();
    }

    public boolean isBuffered()
    {
        return this._buffered;
    }

    public void removeListener( IStreamListener listener )
    {
        this._listeners.remove( listener );
    }

    public void setBuffered( boolean buffer )
    {
        this._buffered = buffer;
    }
}
