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

package com.liferay.ide.server.core.portal;

import java.util.Vector;

import org.eclipse.debug.core.IStreamListener;

/**
 * @author Simon Jiang
 */

public class PortalListenerList
{

    private static final Vector<IStreamListener> EmptyVector = new Vector<IStreamListener>();

    private Vector<IStreamListener> _listeners = null;

    public PortalListenerList( int capacity )
    {
        if( capacity < 1 )
        {
            throw new IllegalArgumentException();
        }
        this._listeners = new Vector<IStreamListener>();
    }

    public void add( IStreamListener listener )
    {
        _listeners.add( listener );
    }

    public IStreamListener[] getListeners()
    {
        if( _listeners.size() == 0 )
        {
            return EmptyVector.toArray( new IStreamListener[EmptyVector.size()] );
        }

        return _listeners.toArray( new IStreamListener[_listeners.size() ] );
    }

    public void remove( IStreamListener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }

        _listeners.remove( listener );
    }

    public void removeAll()
    {
        _listeners.removeAllElements();
    }

    public int size()
    {
        return _listeners.size();
    }
}
