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

package com.liferay.ide.server.core.portal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;

/**
 * @author Simon Jiang
 */
public class PortalJettyBundle extends AbstractPortalBundle  implements PortalBundle
{

    public static final int DEFAULT_JMX_PORT = 2099;

    public PortalJettyBundle( IPath path )
    {
       super(path);
    }

    protected int getDefaultJMXRemotePort()
    {
        return DEFAULT_JMX_PORT;
    }

    public String getStartMainClass()
    {
        return "org.eclipse.jetty.start.Main";
    }

    public String getStopMainClass()
    {
        return "org.eclipse.jetty.start.Main";
    }

    
    protected IPath getPortalDir( IPath appServerDir )
    {
        IPath retval = null;

        if( appServerDir != null )
        {
            retval = appServerDir.append( "/webapps/root" );
        }

        return retval;
    }

    protected IPath getPortaGlobalLib()
    {
        return new Path(this.bundlePath + "/lib/ext/liferay" );
    }
    
    public IPath[] getRuntimeClasspath(IVMInstall vmInstall)
    {
        IPath vmInstallPath = new Path(vmInstall.getInstallLocation().getAbsolutePath());
        final List<IPath> paths = new ArrayList<IPath>();

        if( this.bundlePath.toFile().exists() )
        {
            try
            {
                paths.add( this.bundlePath.append( "start.jar" ) );
                paths.add( vmInstallPath.append( "/jre/lib/rt.jar" ) );
                paths.add( vmInstallPath.append( "/lib/tools.jar" ) );                
                addLibs(this.bundlePath.append( "lib" ),paths);
                addLibs(this.bundlePath.append( "lib/jsp" ),paths);
                addLibs(this.bundlePath.append( "lib/annotations" ),paths);
            }
            catch( MalformedURLException e )
            {
            }
        }

        return paths.toArray( new IPath[0] );
    }

    @Override
    public String[] getRuntimeStartProgArgs()
    {
        final List<String> args = new ArrayList<String>();
        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        final List<String> args = new ArrayList<String>();
        args.add( "--stop" );
        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStartVMArgs(IVMInstall vmInstall)
    {
        IPath vmInstallPath = new Path(vmInstall.getInstallLocation().getAbsolutePath());
        
        final List<String> args = new ArrayList<String>();

        args.add( "-Dcom.sun.management.jmxremote" );
        args.add( "-Dcom.sun.management.jmxremote.authenticate=false" );
        args.add( "-Dcom.sun.management.jmxremote.port=" + jmxRemotePort );
        args.add( "-Dcom.sun.management.jmxremote.ssl=false" );
        args.add( "-Djetty.home=" +  this.bundlePath );
        args.add( "-Dinstall.jetty.home=" +  this.bundlePath );
        args.add( "-Djava.library.path=" + vmInstallPath);
        args.add( "-DVERBOSE" );
        args.add( "-Djetty.port=8080" ); 
        args.add( "-DSTOP.PORT=8082" );
        args.add( "-DSTOP.KEY=secret" );

        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStopVMArgs(IVMInstall vmInstall)
    {
        final List<String> args = new ArrayList<String>();
        return args.toArray( new String[0] );
    }

    public String getType()
    {
        return "jetty";
    }
}