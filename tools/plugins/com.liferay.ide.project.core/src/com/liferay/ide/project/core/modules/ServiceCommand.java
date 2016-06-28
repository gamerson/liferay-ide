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

package com.liferay.ide.project.core.modules;

import aQute.remote.api.Agent;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Lovett Li
 * @author Simon Jiang
 */
public class ServiceCommand
{

    private final IServer _server;
    private String _serviceName;

    public ServiceCommand( IServer server )
    {
        _server = server;
    }

    public ServiceCommand( IServer server, String serviceName )
    {
        _serviceName = serviceName;
        _server = server;
    }

    public String[] execute() throws Exception
    {
        BundleSupervisor supervisor = null;
        String[] result = null;

        if( _server == null )
        {
            return getServiceListFromFile();
        }

        try
        {
            PortalServerBehavior serverBehavior =
                (PortalServerBehavior) _server.loadAdapter( PortalServerBehavior.class, null );
            supervisor = serverBehavior.getBundleSupervisor();

            if( supervisor == null )
            {
                return getServiceListFromFile();
            }

            if( !supervisor.getAgent().redirect( Agent.COMMAND_SESSION ) )
            {
                return getServiceListFromFile();
            }

            if( _serviceName == null )
            {
                result = getServices( supervisor );
                updateServicesDynamicFile( result );
            }
            else
            {
                result = getServiceBundle( _serviceName, supervisor );
            }
        }
        finally
        {
            if( supervisor != null )
            {
                try
                {
                    supervisor.getAgent().redirect( Agent.NONE );
                }
                catch( Exception e )
                {
                    // ignore error
                }
            }
        }

        return result;
    }

    private File checkStaticServicesFile() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/services-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            return servicesFile;
        }

        throw new FileNotFoundException( "can't find static services file services-static.json" );
    }

    private File checkDynamicServicesFile() throws IOException
    {
        File file = ProjectCore.getDefault().getStateLocation().append( "dynamic-services.json" ).toFile();

        if( !file.exists() )
        {
            file.createNewFile();;
        }

        return file;
    }

    private String[] getServiceListFromFile() throws Exception
    {
        String[] result = getStaticServices();

        String[] services = getDynamicFileServices();

        if( services != null )
        {
            result = services;
        }

        return result;

    }

    @SuppressWarnings( "unchecked" )
    private String[] getDynamicFileServices()
    {
        try
        {
            File servicesFile = checkDynamicServicesFile();
            final ObjectMapper mapper = new ObjectMapper();

            List<String> map = mapper.readValue( servicesFile, List.class );
            String[] services = map.toArray( new String[0] );

            return services;
        }
        catch( IOException e )
        {
            return null;
        }

    }

    private String[] getServiceBundle( String serviceName, BundleSupervisor supervisor ) throws Exception
    {
        String[] serviceBundleInfo;

        supervisor.getAgent().stdin( "packages " + serviceName.substring( 0, serviceName.lastIndexOf( "." ) ) );

        if( supervisor.getOutInfo().startsWith( "No exported packages" ) )
        {
            supervisor.getAgent().stdin(
                "services " + "(objectClass=" + serviceName + ")" + " | grep \"Registered by bundle:\" " );
            serviceBundleInfo = parseRegisteredBundle( supervisor.getOutInfo() );
        }
        else
        {
            serviceBundleInfo = parseSymbolicName( supervisor.getOutInfo() );
        }

        return serviceBundleInfo;
    }

    private String[] getServices( BundleSupervisor supervisor ) throws Exception
    {
        supervisor.getAgent().stdin( "services" );

        return parseService( supervisor.getOutInfo() );
    }

    @SuppressWarnings( "unchecked" )
    private String[] getStaticServiceBundle( String _serviceName ) throws Exception
    {
        final File servicesFile = checkStaticServicesFile();
        final ObjectMapper mapper = new ObjectMapper();

        Map<String, List<String>> map = mapper.readValue( servicesFile, Map.class );
        List<String> serviceBundle = map.get( _serviceName );

        if( serviceBundle != null && serviceBundle.size() != 0 )
        {
            return (String[]) serviceBundle.toArray( new String[serviceBundle.size()] );
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    private String[] getStaticServices() throws Exception
    {
        final File servicesFile = checkStaticServicesFile();
        final ObjectMapper mapper = new ObjectMapper();

        Map<String, String[]> map = mapper.readValue( servicesFile, Map.class );
        String[] services = map.keySet().toArray( new String[0] );

        return services;
    }

    private void updateServicesDynamicFile( final String[] servicesList ) throws Exception
    {
        final File servicesFile = checkDynamicServicesFile();
        final ObjectMapper mapper = new ObjectMapper();

        final Job job = new WorkspaceJob( "Update services file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    if( monitor.isCanceled() )
                    {
                        return Status.CANCEL_STATUS;
                    }

                    mapper.writeValue( servicesFile, servicesList );
                }
                catch( Exception e )
                {
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    private String[] parseRegisteredBundle( String serviceName )
    {
        if( serviceName.startsWith( "false" ) )
        {
            return null;
        }

        String str = serviceName.substring( 0, serviceName.indexOf( "[" ) );
        str = str.replaceAll( "\"Registered by bundle:\"", "" ).trim();
        String[] result = str.split( "_" );

        if( result.length == 2 )
        {
            return result;
        }

        return null;
    }

    private String[] parseService( String outinfo )
    {
        final Pattern pattern = Pattern.compile( "(?<=\\{)(.+?)(?=\\})" );
        final Matcher matcher = pattern.matcher( outinfo );
        final List<String> ls = new ArrayList<>();

        while( matcher.find() )
        {
            ls.add( matcher.group() );
        }

        Iterator<String> iterator = ls.iterator();

        while( iterator.hasNext() )
        {
            String serviceName = iterator.next();

            if( serviceName.contains( "bundle.id=" ) || serviceName.contains( "service.id=" ) ||
                serviceName.contains( "=" ) )
            {
                iterator.remove();
            }
        }

        final List<String> listservice = new ArrayList<>();

        for( String bs : ls )
        {
            if( bs.split( "," ).length > 1 )
            {
                for( String bbs : bs.split( "," ) )
                {
                    listservice.add( bbs.trim() );
                }
            }
            else
            {
                listservice.add( bs );
            }
        }

        final Set<String> set = new HashSet<String>();
        final List<String> newList = new ArrayList<>();

        for( Iterator<String> iter = listservice.iterator(); iter.hasNext(); )
        {
            String element = iter.next();

            if( set.add( element ) )
            {
                newList.add( element );
            }
        }

        Collections.sort( newList );

        return newList.toArray( new String[0] );
    }

    private String[] parseSymbolicName( String info )
    {
        final int symbolicIndex = info.indexOf( "bundle-symbolic-name" );
        final int versionIndex = info.indexOf( "version:Version" );
        String symbolicName;
        String version;

        if( symbolicIndex != -1 && versionIndex != -1 )
        {
            symbolicName = info.substring( symbolicIndex, info.indexOf( ";", symbolicIndex ) );
            version = info.substring( versionIndex, info.indexOf( ";", versionIndex ) );

            final Pattern p = Pattern.compile( "\"([^\"]*)\"" );
            Matcher m = p.matcher( symbolicName );

            while( m.find() )
            {
                symbolicName = m.group( 1 );
            }

            m = p.matcher( version );

            while( m.find() )
            {
                version = m.group( 1 );
            }

            return new String[] { symbolicName, version };
        }

        return null;
    }
}
