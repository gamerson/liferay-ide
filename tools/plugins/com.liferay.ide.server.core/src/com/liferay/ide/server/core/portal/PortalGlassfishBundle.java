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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;

/**
 * @author Simon Jiang
 */
public class PortalGlassfishBundle extends AbstractPortalBundle  implements PortalBundle
{

    public static final int DEFAULT_JMX_PORT = 2099;

    public PortalGlassfishBundle( IPath path )
    {
       super(path);
    }

    protected int getDefaultJMXRemotePort()
    {
        return DEFAULT_JMX_PORT;
    }

    public String getStartMainClass()
    {
        return "com.sun.enterprise.glassfish.bootstrap.ASMain";
    }
    
    public String getStopMainClass()
    {
        return "com.sun.enterprise.admin.cli.AsadminMain";
    }    

    protected IPath getPortalDir( IPath appServerDir )
    {
        IPath retval = null;

        if( appServerDir != null )
        {
            
            retval = appServerDir.append( "/domains/domain1/lib" );
        }

        return retval;
    }
    
    protected IPath getPortaGlobalLib()
    {
        return new Path(this.bundlePath + "/domains/domain1/lib" );
    }
    
    public IPath[] getRuntimeClasspath(IVMInstall vmInstall)
    {
        final List<IPath> paths = new ArrayList<IPath>();

        if( this.bundlePath.toFile().exists() )
        {
            try
            {
                addLibs(this.bundlePath,paths);
                addLibs(this.bundlePath.append( "lib" ),paths);
                addLibs(this.bundlePath.append( "modules" ),paths);
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
        args.add( "stop-domain" );
        args.add( "domain1");
        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStartVMArgs(IVMInstall vmInstall)
    {
        IPath vmInstallPath = new Path(vmInstall.getInstallLocation().getAbsolutePath());

        final List<String> args = new ArrayList<String>();
        args.add( "-Xdebug" );
        args.add( "-XX:+UnlockDiagnosticVMOptions");
        args.add( "-XX:+LogVMOutput");
        args.add( "-XX:NewRatio=2");
        args.add( "-Dcom.sun.aas.configName=server-config");
        args.add( "-Ddomain.name=domain1");
        args.add( "-Djavax.net.ssl.keyStore=" + "\"" + this.bundlePath + "/domains/domain1/config/keystore.jks" + "\"");
        args.add( "-Djmx.invoke.getters=true");
        args.add( "-Djava.security.auth.login.config="+ "\"" + this.bundlePath + "\"" + "/domains/domain1/config/login.conf"  + "\"" );
        args.add( "-Djava.security.policy="+ "\"" + this.bundlePath + "/domains/domain1/config/server.policy"  + "\"" );
        args.add( "-Dsun.rmi.dgc.server.gcInterval=3600000");
        args.add( "-Dsun.rmi.dgc.client.gcInterval=3600000");
        args.add( "-Djava.endorsed.dirs=" + "\"" + this.bundlePath + "/modules/endorsed"  + "\"");
        args.add( "-Dcom.sun.aas.instanceRoot=" + "\"" + this.bundlePath  + "/domains/domain1" + "\"" );
        args.add( "-Djdbc.drivers=org.apache.derby.jdbc.ClientDriver");
        args.add( "-Djavax.net.ssl.trustStore="+ "\"" + this.bundlePath + "/domains/domain1/config/cacerts.jks"  + "\"" );
        args.add( "-Dcom.sun.aas.configRoot=" + "\"" + this.bundlePath  + "/domains/domain1" + "\"" );
        args.add( "-Djava.library.path=" + "\"" + vmInstall.getInstallLocation() + "\"");
        args.add( "-Dcom.sun.aas.instanceName=server");
        args.add( "-Dcom.sun.aas.processLauncher=SE");
        args.add( "-Dcom.sun.aas.verboseMode=true");
        args.add( "-Dcom.sun.enterprise.server.ss.ASQuickStartup=false");
        args.add( "-DJAVA_HOME=" + "\"" + vmInstall.getInstallLocation() + "\"");
        args.add( "-DGLASSFISH_HOME=" + "\"" + this.bundlePath  + "\"");
        args.add( "-Djava.ext.dirs="+ "\"" + this.bundlePath + "/domains/domain1/lib/ext" + ":" + vmInstallPath.append( "/lib/ext" ) + ":" 
          +  vmInstallPath.append( "/jre/lib/ext" ) + "\"");    
        args.add( "-Dcom.sun.management.jmxremote" );
        args.add( "-Dcom.sun.management.jmxremote.authenticate=false" );
        args.add( "-Dcom.sun.management.jmxremote.port=" + getJmxRemotePort() );
        args.add( "-Dcom.sun.management.jmxremote.ssl=false" );        

        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStopVMArgs(IVMInstall vmInstall)
    {
        return getRuntimeStartVMArgs(vmInstall);
    }

    public String getType()
    {
        return "glassfish";
    }
}