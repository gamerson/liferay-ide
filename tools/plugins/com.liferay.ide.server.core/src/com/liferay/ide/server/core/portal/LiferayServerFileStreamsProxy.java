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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */

public abstract class LiferayServerFileStreamsProxy implements ITerminateableStreamsProxy
{

    protected LiferayServerOutputStreamMonitor _sysOut;
    protected String _sysoutFile;
    protected Thread streamThread;
    protected boolean done = false;
    protected boolean isPaused = false;
    protected boolean isMonitorStopping = false;
    BufferedReader _bufferOut = null;
    protected File _fpOut = null;

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
        this.streamThread = new Thread( "Liferay Server IO Stream")
        {

            public void run()
            {
                boolean isOutInitialized = false;
                boolean isOutFileEmpty = false;

                while( ( !( done ) ) && ( ( ( !( isOutInitialized ) ) ) ) )
                {
                    try
                    {
                        _fpOut = _sysoutFile!= null? new File( _sysoutFile ):null;

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
                    }
                    catch( Exception e )
                    {
                    }

                    if( ( isOutInitialized ) )
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
                }
                catch( Exception e )
                {
                }

                long originalFpOutSize = _fpOut.length();

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

        this.done = true;
    }

    public void write( String input ) throws IOException
    {
    }
}
