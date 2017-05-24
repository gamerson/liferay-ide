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
        return this._sysErr;
    }

    public IStreamMonitor getOutputStreamMonitor()
    {
        return this._sysOut;
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
        if( this._bufferOut != null )
        {
            try
            {
                this._bufferOut.close();
            }
            catch( Exception e )
            {
            }
        }

        if( this._bufferErr != null )
        {
            try
            {
                this._bufferErr.close();
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
