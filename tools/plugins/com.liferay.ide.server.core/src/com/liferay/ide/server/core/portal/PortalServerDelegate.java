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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class PortalServerDelegate extends ServerDelegate implements PortalServerWorkingCopy
{

    private final static List<String> SUPPORT_TYPES_LIST = Arrays.asList( "liferay.bundle", "jst.web", "jst.utility" );

    public PortalServerDelegate()
    {
        super();
    }

    @Override
    public IStatus canModifyModules( IModule[] add, IModule[] remove )
    {
        IStatus retval = Status.OK_STATUS;

        if( !CoreUtil.isNullOrEmpty( add ) )
        {
            for( IModule module : add )
            {
                if( !SUPPORT_TYPES_LIST.contains( module.getModuleType().getId() ) )
                {
                    retval =
                        LiferayServerCore.error( "Unable to add module with type " + module.getModuleType().getName() );
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public int getAgentPort()
    {
        return getAttribute( ATTR_AGENT_PORT, DEFAULT_AGENT_PORT );
    }

    @Override
    public int getAjpPort()
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime.getPortalBundle().getAjpPort();
    }

    public int getAutoPublishTime()
    {
        return getAttribute( Server.PROP_AUTO_PUBLISH_TIME, 0 );
    }

    @Override
    public IModule[] getChildModules( IModule[] module )
    {
        IModule[] retval = null;

        if( module != null )
        {
            final IModuleType moduleType = module[0].getModuleType();

            if( module.length == 1 && moduleType != null && SUPPORT_TYPES_LIST.contains( moduleType.getId() ) )
            {
                retval = new IModule[0];
            }
        }

        return retval;
    }

    @Override
    public boolean getDeveloperMode()
    {
        return getAttribute( PROPERTY_DEVELOPER_MODE, PortalServerConstants.DEFAULT_DEVELOPER_MODE );
    }

    public String getExternalProperties()
    {
        return getAttribute( PROPERTY_EXTERNAL_PROPERTIES, StringPool.EMPTY );
    }

    public String getHost()
    {
        return getServer().getHost();
    }

    @Override
    public int getHttpPort()
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime.getPortalBundle().getHttpPort();
    }

    public String getId()
    {
        return getServer().getId();
    }

    @Override
    public int getJmxPort()
    {
        return getAttribute( ATTR_JMX_PORT, DEFAULT_JMX_PORT );
    }

    @Override
    public boolean getLaunchSettings()
    {
        return getAttribute( PROPERTY_LAUNCH_SETTINGS, PortalServerConstants.DEFAULT_LAUNCH_SETTING );
    }

    public String[] getMemoryArgs()
    {
        String[] retval = null;

        final String args = getAttribute( PROPERTY_MEMORY_ARGS, PortalServerConstants.DEFAULT_MEMORY_ARGS );

        if( !CoreUtil.isNullOrEmpty( args ) )
        {
            retval = args.split( " " );
        }

        return retval;
    }

    public String getPassword()
    {
        return getAttribute( ATTR_PASSWORD, DEFAULT_PASSWORD );
    }

    @Override
    public URL getPluginContextURL( String context )
    {
        try
        {
            return new URL( getPortalHomeUrl(), StringPool.FORWARD_SLASH + context );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public URL getPortalHomeUrl()
    {
        try
        {
            return new URL( "http://localhost:" + getHttpPort() );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public IModule[] getRootModules( IModule module ) throws CoreException
    {
        final IStatus status = canModifyModules( new IModule[] { module }, null );

        if( status == null || !status.isOK() )
        {
            throw new CoreException( status );
        }

        return new IModule[] { module };
    }

    @Override
    public int getShutdownPort()
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime.getPortalBundle().getShutdownPort();
    }

    @Override
    public int getTelnetPort()
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime.getPortalBundle().getTelnetPort();
    }

    public String getUsername()
    {
        return getAttribute( ATTR_USERNAME, DEFAULT_USERNAME );
    }

    @Override
    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/tunnel-web/axis" ); //$NON-NLS-1$
        }
        catch( MalformedURLException e )
        {
            LiferayServerCore.logError( "Unable to get web services list URL", e ); //$NON-NLS-1$
        }

        return null;
    }

    @Override
    public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException
    {
    }

    public void setAgentPort( int port )
    {
        setAttribute( ATTR_AGENT_PORT, port );
    }

    public void setAjpPort( int port )
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        runtime.getPortalBundle().setAjpPort( port );
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        setAttribute( Server.PROP_AUTO_PUBLISH_TIME, getAutoPublishTime() );

        setAgentPort( DEFAULT_AGENT_PORT );
        setAjpPort( DEFAULT_AJP_PORT );
        setHttpPort( DEFAULT_HTTP_PORT );
        setJmxPort( DEFAULT_JMX_PORT );
        setTelnetPort( DEFAULT_TELNET_PORT );
        setShutdownPort( DEFAULT_SHUTDOWN_PORT );
    }

    @Override
    public void setDeveloperMode( boolean developerMode )
    {
        setAttribute( PROPERTY_DEVELOPER_MODE, developerMode );
    }

    public void setExternalProperties( String externalProperties )
    {
        setAttribute( PROPERTY_EXTERNAL_PROPERTIES, externalProperties );
    }

    public void setHttpPort( int port )
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        runtime.getPortalBundle().setHttpPort( port );
    }

    public void setJmxPort( int port )
    {
        setAttribute( ATTR_JMX_PORT, port );
    }

    @Override
    public void setLaunchSettings( boolean launchSettings )
    {
        setAttribute( PROPERTY_LAUNCH_SETTINGS, launchSettings );
    }

    public void setMemoryArgs( String memoryArgs )
    {
        setAttribute( PROPERTY_MEMORY_ARGS, memoryArgs );
    }

    public void setPassword( String password )
    {
        setAttribute( ATTR_PASSWORD, password );
    }

    public void setShutdownPort( int port )
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        runtime.getPortalBundle().setShutdownPort( port );
    }

    public void setTelnetPort( int port )
    {
        PortalRuntime runtime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        runtime.getPortalBundle().setTelnetPort( port );
    }

    public void setUsername( String username )
    {
        setAttribute( ATTR_USERNAME, username );
    }

}
