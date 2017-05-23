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

public abstract class WebsphereFileStreamsProxy implements ITerminateableStreamsProxy
{

    protected OutputStreamMonitor sysOut;
    protected OutputStreamMonitor sysErr;
    protected String sysoutFile;
    protected String syserrFile;
    protected Thread streamThread;
    protected boolean done = false;
    protected boolean isPaused = false;
    protected boolean isMonitorStopping = false;
    BufferedReader fout = null;
    BufferedReader ferr = null;
    protected File fpOut = null;
    protected File fpErr = null;

    public IStreamMonitor getErrorStreamMonitor()
    {
        return this.sysErr;
    }

    public IStreamMonitor getOutputStreamMonitor()
    {
        return this.sysOut;
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
            s = br.readLine();
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
                        fpOut = new File( sysoutFile );
                        fpErr = new File( syserrFile );
                        if( !( isOutInitialized ) )
                        {
                            if( !( fpOut.exists() ) )
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
                            if( !( fpErr.exists() ) )
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
                        fout = new BufferedReader( new FileReader( fpOut ) );

                        if( !( isOutFileEmpty ) )
                        {
                            readToNow( fout );
                        }
                    }
                    if( isErrInitialized )
                    {
                        ferr = new BufferedReader( new FileReader( fpErr ) );

                        if( !( isErrFileEmpty ) )
                        {
                            readToNow( ferr );
                        }
                    }
                }
                catch( Exception e )
                {
                }

                long originalFpOutSize = fpOut.length();
                long originalFpErrSize = fpErr.length();
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
                            long newFpOutSize = fpOut.length();
                            if( shouldReloadFileReader( originalFpOutSize, newFpOutSize ) )
                            {

                                if( fout != null )
                                {
                                    fout.close();
                                }
                                fout = new BufferedReader( new FileReader( fpOut ) );
                            }
                            originalFpOutSize = newFpOutSize;
                            s = fout.readLine();
                            if( s != null )
                            {
                                if( isPaused() )
                                {
                                    if( s.startsWith( "************ " ) )
                                    {
                                        sysOut.append( s + "\n" );
                                        setIsPaused( false );
                                    }
                                }
                                else if( ( isMonitorStopping() ) && ( s.startsWith( "************ " ) ) )
                                {
                                    setIsPaused( true );
                                }
                                else
                                {
                                    sysOut.append( s + "\n" );
                                }
                            }
                        }

                        if( !( isPaused() ) )
                        {
                            s = "";
                            while( s != null )
                            {
                                long newFpErrSize = fpErr.length();
                                if( shouldReloadFileReader( originalFpErrSize, newFpErrSize ) )
                                {

                                    if( ferr != null )
                                    {
                                        ferr.close();
                                    }
                                    ferr = new BufferedReader( new FileReader( fpErr ) );
                                }
                                originalFpErrSize = newFpErrSize;
                                s = ferr.readLine();
                                if( s != null )
                                {
                                    sysErr.append( s + "\n" );
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
        if( this.fout != null )
        {
            try
            {
                this.fout.close();
            }
            catch( Exception e )
            {
            }
        }

        if( this.ferr != null )
        {
            try
            {
                this.ferr.close();
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
