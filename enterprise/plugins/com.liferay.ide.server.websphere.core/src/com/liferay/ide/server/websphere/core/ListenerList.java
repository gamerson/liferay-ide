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

public class ListenerList
{

    private static final Object[] EmptyArray = new Object[0];
    private int fSize;

    private Object[] fListeners = null;

    public ListenerList( int capacity )
    {
        if( capacity < 1 )
        {
            throw new IllegalArgumentException();
        }
        this.fListeners = new Object[capacity];
        this.fSize = 0;
    }

    public synchronized void add( Object listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }

        for( int i = 0; i < this.fSize; ++i )
        {
            if( this.fListeners[i] == listener )
            {
                return;
            }
        }

        if( this.fSize == this.fListeners.length )
        {
            Object[] temp = new Object[this.fSize * 2 + 1];
            System.arraycopy( this.fListeners, 0, temp, 0, this.fSize );
            this.fListeners = temp;
        }
        this.fListeners[( this.fSize++ )] = listener;
    }

    public synchronized Object[] getListeners()
    {
        if( this.fSize == 0 )
        {
            return EmptyArray;
        }
        Object[] result = new Object[this.fSize];
        System.arraycopy( this.fListeners, 0, result, 0, this.fSize );
        return result;
    }

    public synchronized void remove( Object listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }

        for( int i = 0; i < this.fSize; ++i )
            if( this.fListeners[i] == listener )
            {
                if( --this.fSize == 0 )
                {
                    this.fListeners = new Object[1];
                }
                else
                {
                    if( i < this.fSize )
                    {
                        this.fListeners[i] = this.fListeners[this.fSize];
                    }
                    this.fListeners[this.fSize] = null;
                }
                return;
            }
    }

    public synchronized void removeAll()
    {
        this.fListeners = new Object[0];
        this.fSize = 0;
    }

    public int size()
    {
        return this.fSize;
    }
}
