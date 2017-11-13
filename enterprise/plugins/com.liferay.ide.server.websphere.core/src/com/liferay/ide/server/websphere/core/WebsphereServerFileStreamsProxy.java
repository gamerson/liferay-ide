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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */

public abstract class WebsphereServerFileStreamsProxy implements ITerminateableStreamsProxy
{

    protected ServerOutputStreamMonitor _sysOut;
    protected ServerOutputStreamMonitor _sysErr;
    protected String _sysoutFile;
    protected String _syserrFile;
    protected Thread streamThread;
    protected boolean done = false;
    protected boolean isPaused = false;
    protected boolean isMonitorStopping = false;
    BufferedReader _bufferOut = null;
    BufferedReader _bufferErr = null;
    protected File _fpOut = null;
    protected File _fpErr = null;

    public IStreamMonitor getErrorStreamMonitor()
    {
        return _sysErr;
    }

    public IStreamMonitor getOutputStreamMonitor()
    {
        return _sysOut;
    }

    public boolean isMonitorStopping()
    {
        return this.isMonitorStopping;
    }

    public boolean isPaused()
    {
        return this.isPaused;
    }

    public boolean isTerminated()
    {
        return this.done;
    }

    protected void readToNow( BufferedReader br ) throws IOException
    {
        String s = "";
        while( s != null )
        {
            s = br.readLine();
        }
    }

    protected void setIsMonitorStopping( boolean curIsMonitorStopping )
    {
        this.isMonitorStopping = curIsMonitorStopping;
    }

    protected void setIsPaused( boolean curIsPaused )
    {
        this.isPaused = curIsPaused;
    }

    protected final boolean shouldReloadFileReader( long originalFileSize, long newFileSize )
    {
        boolean reloadFileReader = true;

        if( originalFileSize <= newFileSize )
        {
            reloadFileReader = false;
        }
        return reloadFileReader;
    }

    protected void startMonitoring()
    {
        if( this.streamThread != null )
        {
            return;
        }
        this.streamThread = new Thread( "Liferay Websphere IO Stream")
        {

            public void run()
            {
                boolean isOutInitialized = false;
                boolean isErrInitialized = false;
                boolean isOutFileEmpty = false;
                boolean isErrFileEmpty = false;

                while( ( !( done ) ) && ( ( ( !( isOutInitialized ) ) || ( !( isErrInitialized ) ) ) ) )
                {
                    try
                    {
                        _fpOut = new File( _sysoutFile );
                        _fpErr = new File( _syserrFile );

                        if( !( isOutInitialized ) )
                        {
                            if( !( _fpOut.exists() ) )
                            {
                                isOutFileEmpty = true;
                            }
                            else
                            {
                                isOutInitialized = true;
                            }
                        }

                        if( !( isErrInitialized ) )
                        {
                            if( !( _fpErr.exists() ) )
                            {
                                isErrFileEmpty = true;
                            }
                            else
                            {
                                isErrInitialized = true;
                            }
                        }
                    }
                    catch( Exception e )
                    {
                    }

                    if( ( isOutInitialized ) && ( isErrInitialized ) )
                    {
                        continue;
                    }
                    try
                    {
                        sleep( 200L );
                    }
                    catch( Exception localException1 )
                    {
                    }
                }
                try
                {
                    if( isOutInitialized )
                    {
                        _bufferOut = new BufferedReader( new FileReader( _fpOut ) );

                        if( !( isOutFileEmpty ) )
                        {
                            readToNow( _bufferOut );
                        }
                    }

                    if( isErrInitialized )
                    {
                        _bufferErr = new BufferedReader( new FileReader( _fpErr ) );

                        if( !( isErrFileEmpty ) )
                        {
                            readToNow( _bufferErr );
                        }
                    }
                }
                catch( Exception e )
                {
                }

                long originalFpOutSize = _fpOut.length();
                long originalFpErrSize = _fpErr.length();

                while( !( done ) )
                {
                    try
                    {
                        sleep( 500L );
                    }
                    catch( Exception localException2 )
                    {
                    }

                    try
                    {
                        String s = "";

                        while( s != null )
                        {
                            long newFpOutSize = _fpOut.length();

                            if( shouldReloadFileReader( originalFpOutSize, newFpOutSize ) )
                            {

                                if( _bufferOut != null )
                                {
                                    _bufferOut.close();
                                }
                                _bufferOut = new BufferedReader( new FileReader( _fpOut ) );
                            }
                            originalFpOutSize = newFpOutSize;
                            s = _bufferOut.readLine();

                            if( s != null )
                            {
                                if( isPaused() )
                                {
                                    if( s.startsWith( "************ " ) )
                                    {
                                        _sysOut.append( s + "\n" );
                                        setIsPaused( false );
                                    }
                                }
                                else if( ( isMonitorStopping() ) && ( s.startsWith( "************ " ) ) )
                                {
                                    setIsPaused( true );
                                }
                                else
                                {
                                    _sysOut.append( s + "\n" );
                                }
                            }
                        }

                        if( !( isPaused() ) )
                        {
                            s = "";
                            while( s != null )
                            {
                                long newFpErrSize = _fpErr.length();

                                if( shouldReloadFileReader( originalFpErrSize, newFpErrSize ) )
                                {

                                    if( _bufferErr != null )
                                    {
                                        _bufferErr.close();
                                    }
                                    _bufferErr = new BufferedReader( new FileReader( _fpErr ) );
                                }
                                originalFpErrSize = newFpErrSize;
                                s = _bufferErr.readLine();

                                if( s != null )
                                {
                                    _sysErr.append( s + "\n" );
                                }
                            }
                        }
                    }
                    catch( Exception e )
                    {
                    }

                }

                streamThread = null;
            }
        };
        this.streamThread.setPriority( 1 );
        this.streamThread.setDaemon( true );
        this.streamThread.start();
    }

    public void terminate()
    {
        if( _bufferOut != null )
        {
            try
            {
                _bufferOut.close();
            }
            catch( Exception e )
            {
            }
        }

        if( _bufferErr != null )
        {
            try
            {
                _bufferErr.close();
            }
            catch( Exception e )
            {
            }
        }

        this.done = true;
    }

    public void write( String input ) throws IOException
    {
    }
}
