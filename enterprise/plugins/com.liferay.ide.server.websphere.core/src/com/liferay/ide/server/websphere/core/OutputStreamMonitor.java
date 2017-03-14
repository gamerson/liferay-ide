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

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;

public class OutputStreamMonitor implements IFlushableStreamMonitor
{

    class ContentNotifier implements ISafeRunnable
    {

        private IStreamListener fListener;
        private String fText;

        public void handleException( Throwable exception )
        {
            DebugPlugin.log( exception );
        }

        public void notifyAppend( String text )
        {
            if( text == null )
            {
                return;
            }
            this.fText = text;
            Object[] copiedListeners = fListeners.getListeners();

            for( int i = 0; i < copiedListeners.length; ++i )
            {
                this.fListener = ( (IStreamListener) copiedListeners[i] );
                SafeRunner.run( this );
            }
            this.fListener = null;
            this.fText = null;
        }

        public void run() throws Exception
        {
            this.fListener.streamAppended( this.fText, OutputStreamMonitor.this );
        }
    }

    ListenerList fListeners = new ListenerList( 1 );
    private boolean fBuffered = true;

    private StringBuffer fContents;

    public OutputStreamMonitor()
    {
        this.fContents = new StringBuffer();
    }

    public void addListener( IStreamListener listener )
    {
        this.fListeners.add( listener );
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
            this.fContents.append( text );
        }
        new ContentNotifier().notifyAppend( text );
    }

    protected void close()
    {
        this.fListeners.removeAll();
    }

    public void flushContents()
    {
        this.fContents.setLength( 0 );
    }

    public String getContents()
    {
        return this.fContents.toString();
    }

    public boolean isBuffered()
    {
        return this.fBuffered;
    }

    public void removeListener( IStreamListener listener )
    {
        this.fListeners.remove( listener );
    }

    public void setBuffered( boolean buffer )
    {
        this.fBuffered = buffer;
    }
}
