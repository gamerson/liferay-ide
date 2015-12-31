package com.liferay.ide.project.core.modules;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

/**
 * @author Lovett Li
 */
public class ServicesCommand {

    private String _serviceName;

    public ServicesCommand()
    {
    }
    public ServicesCommand( String serviceName )
    {
        _serviceName = serviceName;
    }

    public String[] execute() throws Exception
    {
        ServicesSupervisor supervisor = null;
        String[] result = null;
        try
        {
            supervisor = connectRemote();

            if( _serviceName == null )
            {
                result = getServices( supervisor );
            }
            else
            {
                result = getServiceBundle( _serviceName, supervisor );
            }

            return result;
        }
        finally
        {
            if( supervisor != null )
            {
                supervisor.close();
            }

        }
    }

    private ServicesSupervisor connectRemote() throws Exception
    {
        if( !canConnect( "localhost", Agent.DEFAULT_PORT ) )
        {
            throw new Exception( "Can't connect remote server." );
        }

        ServicesSupervisor supervisor = new ServicesSupervisor();
        supervisor.connect( "localhost", Agent.DEFAULT_PORT );

        if( !supervisor.getAgent().redirect( -1 ) )
        {
            supervisor.close();
            throw new Exception( "Unable to redirect input to agent." );
        }

        return supervisor;
    }

    static boolean canConnect( String host, int port )
    {
        InetSocketAddress address = new InetSocketAddress( host, Integer.valueOf( port ) );
        InetSocketAddress local = new InetSocketAddress( 0 );

        InputStream in = null;

        try(Socket socket = new Socket())
        {
            socket.bind( local );
            socket.connect( address, 3000 );
            in = socket.getInputStream();

            return true;
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( Exception e )
                {
                }
            }
        }

        return false;
    }

    private String[] getServices( ServicesSupervisor supervisor ) throws Exception
    {
        supervisor.getAgent().stdin( "services" );
        return new ServiceParser().parseService( supervisor.getOutInfo() );
    }

    private String[] getServiceBundle( String serviceName, ServicesSupervisor supervisor ) throws Exception
    {
        String[] serviceBundleInfo;

        supervisor.getAgent().stdin( "packages " + serviceName.substring( 0, serviceName.lastIndexOf( "." ) ) );
        if( supervisor.getOutInfo().equals( "No exported packages\n" ) )
        {
            supervisor.getAgent().stdin(
                "services " + "(objectClass=" + serviceName + ")" + " | grep \"Registered by bundle:\" " );
            serviceBundleInfo = new ServiceParser().parseRegisteredBundle( supervisor.getOutInfo() );
        }
        else
        {
            serviceBundleInfo = new ServiceParser().parseSymbolicName( supervisor.getOutInfo() );
        }

        return serviceBundleInfo;
    }

    public class ServicesSupervisor extends AgentSupervisor<Supervisor, Agent> implements Supervisor
    {

        private String outinfo;

        @Override
        public boolean stdout( String out ) throws Exception
        {
            if( !"".equals( out ) && out != null )
            {
                out = out.replaceAll( ".*>.*$", "" );
                if( !out.equals( "" ) && !out.equals( "true\n" ) && !out.equals( "false\n" ) )
                {
                    outinfo = out;
                }
            }
            return true;
        }

        @Override
        public boolean stderr( String out ) throws Exception
        {
            return true;
        }

        public void connect( String host, int port ) throws Exception
        {
            super.connect( Agent.class, this, host, port );
        }

        @Override
        public void event( Event e ) throws Exception
        {
        }

        public String getOutInfo()
        {
            return outinfo;
        }

    }

    private class ServiceParser
    {

        public String[] parseService( String outinfo )
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
                iterator.next();
                if( iterator.hasNext() )
                {
                    iterator.next();
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
                    newList.add( element );
            }

            return newList.toArray( new String[0] );
        }

        public String[] parseRegisteredBundle( String serviceName )
        {

            String str = serviceName.substring( 0, serviceName.indexOf( "[" ) );
            str = str.replaceAll( "\"Registered by bundle:\"", "" ).trim();
            String[] result = str.split( "_" );
            if( result.length == 2 )
                return result;
            return null;
        }

        public String[] parseSymbolicName( String info )
        {
            final int symbolicIndex = info.indexOf( "bundle-symbolic-name" );
            final int versionIndex = info.indexOf( "version:Version" );
            String symbolicName = info.substring( symbolicIndex, info.indexOf( ";", symbolicIndex ) );
            String version = info.substring( versionIndex, info.indexOf( ";", versionIndex ) );

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

    }
}