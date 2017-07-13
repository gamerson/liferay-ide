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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.AbstractPortalBundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PortalTomcatBundle extends AbstractPortalBundle
{
    private final static String FRAMEWORK_OSGI_CONSOLE_NAME = "module.framework.properties.osgi.console";

    private final static String PORTAL_EXT_PROPERTIES = "portal-ext.properties";

    public PortalTomcatBundle( IPath path )
    {
       super(path);
    }

    public PortalTomcatBundle( Map<String, String> appServerProperties )
    {
       super(appServerProperties);
    }

    @Override
    public String getAjpPort()
    {
        String retval = "8009";

        File serverXmlFile = new File( getAppServerDir().toPortableString(), "conf/server.xml" );

        String portValue = getPortalServerPortValue( serverXmlFile, "Connector", "protocol", "AJP/1.3", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            return portValue;
        }

        return retval;
    }

    @Override
    public IPath getAppServerDeployDir()
    {
        return getAppServerDir().append( "webapps" ); //$NON-NLS-1$
    }

    @Override
    protected IPath getAppServerLibDir()
    {
        return getAppServerDir().append( "lib" ); //$NON-NLS-1$
    }

    @Override
    public IPath getAppServerLibGlobalDir()
    {
        return getAppServerDir().append( "/lib/ext" );
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        if( this.bundlePath != null )
        {
            retval = this.bundlePath.append( "webapps/ROOT" );
        }

        return retval;
    }

    @Override
    public String getDisplayName()
    {
        return "Tomcat";
    }

    @Override
    public String getHttpPort()
    {
        String retVal = "8080";

        File serverXmlFile = new File( getAppServerDir().toPortableString(), "conf/server.xml" );

        String portValue = getPortalServerPortValue( serverXmlFile, "Connector", "protocol", "HTTP/1.1", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            return portValue;
        }

        return retVal;
    }

    @Override
    public String getMainClass()
    {
        return "org.apache.catalina.startup.Bootstrap";
    }

    @Override
    public IPath[] getRuntimeClasspath()
    {
        final List<IPath> paths = new ArrayList<IPath>();

        final IPath binPath = this.bundlePath.append( "bin" );

        if( binPath.toFile().exists() )
        {
            paths.add( binPath.append( "bootstrap.jar" ) );

            final IPath juli = binPath.append( "tomcat-juli.jar" );

            if( juli.toFile().exists() )
            {
                paths.add( juli );
            }
        }

        return paths.toArray( new IPath[0] );
    }

    @Override
    public String[] getRuntimeStartProgArgs()
    {
        final String[] retval = new String[1];
        retval[0] = "start";
        return retval;
    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        final String[] retval = new String[1];
        retval[0] = "stop";
        return retval;
    }

    @Override
    public String[] getRuntimeStartVMArgs()
    {
        return getRuntimeVMArgs();
    }

    @Override
    public String[] getRuntimeStopVMArgs()
    {
        return getRuntimeVMArgs();
    }

    private String[] getRuntimeVMArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-Dcatalina.base=" + "\"" + this.bundlePath.toPortableString() + "\"" );
        args.add( "-Dcatalina.home=" + "\"" + this.bundlePath.toPortableString() + "\"" );
        // TODO use dynamic attach API
        args.add( "-Dcom.sun.management.jmxremote" );
        args.add( "-Dcom.sun.management.jmxremote.authenticate=false" );
        args.add( "-Dcom.sun.management.jmxremote.ssl=false" );
        args.add( "-Dfile.encoding=UTF8" );
        args.add( "-Djava.endorsed.dirs=" + "\"" + this.bundlePath.append( "endorsed" ).toPortableString() + "\"" );
        args.add( "-Djava.io.tmpdir=" + "\"" + this.bundlePath.append( "temp" ).toPortableString() + "\"" );
        args.add( "-Djava.net.preferIPv4Stack=true" );
        args.add( "-Djava.util.logging.config.file=" + "\"" + this.bundlePath.append( "conf/logging.properties" ) +
            "\"" );
        args.add( "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager" );
        args.add( "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false" );
        args.add( "-Duser.timezone=GMT" );

        return args.toArray( new String[0] );
    }

    @Override
    public String getShutdownPort()
    {
        String retVal = "8005";

        File serverXmlFile = new File( getAppServerDir().toPortableString(), "conf/server.xml" );

        String portValue = getPortalServerPortValue( serverXmlFile, "Server", "shutdown", "SHUTDOWN", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            return portValue;
        }

        return retVal;
    }

    @Override
    public String getTelnetPort()
    {
        String retVal = "11311";

        String port = null;

        File portalExt = liferayHome.append( PORTAL_EXT_PROPERTIES ).toFile();

        if( portalExt.exists() )
        {
            PortalPropertiesConfiguration config;
            try
            {
                config = new PortalPropertiesConfiguration();
                InputStream in = new FileInputStream( portalExt );

                config.load( in );

                in.close();

                if( config.containsKey( FRAMEWORK_OSGI_CONSOLE_NAME ) )
                {
                    port = ( (String) config.getProperty( FRAMEWORK_OSGI_CONSOLE_NAME ) )
                                    .replaceAll( "localhost:", "" ).trim();
                }

                if( !CoreUtil.empty( port ))
                {
                    return port;
                }
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( "Could not read telnet port from properties ext file", e );
            }
        }
        return retVal;
    }

    @Override
    public String getType()
    {
        return "tomcat";
    }

    @Override
    public IPath[] getUserLibs()
    {
        List<IPath> libs = new ArrayList<IPath>();
        try
        {
            List<File>  portallibFiles = FileListing.getFileListing( new File( getAppServerPortalDir().append( "WEB-INF/lib" ).toPortableString() ) );
            for( File lib : portallibFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            List<File>  libFiles = FileListing.getFileListing( new File( getAppServerLibDir().toPortableString() ) );
            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ))
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            List<File>  extlibFiles = FileListing.getFileListing( new File( getAppServerLibGlobalDir().toPortableString() ) );
            for( File lib : extlibFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) )
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return libs.toArray( new IPath[libs.size()] );
    }

    @Override
    public void setAjpPort( String port )
    {
        setPortalServerPortValue(
            new File( getAppServerDir().toPortableString(), "conf/server.xml" ), "Connector", "protocol", "AJP/1.3",
            "port", port );
    }

    @Override
    public void setHttpPort( String port )
    {
        setPortalServerPortValue(
            new File( getAppServerDir().toPortableString(), "conf/server.xml" ), "Connector", "protocol", "HTTP/1.1",
            "port", port );
    }

    private void setPortalServerPortValue(
        File xmlFile, String tagName, String attriName, String attriValue, String targetName, String value )
    {
        DocumentBuilder db = null;

        DocumentBuilderFactory dbf = null;

        try
        {
            dbf = DocumentBuilderFactory.newInstance();

            db = dbf.newDocumentBuilder();

            Document document = db.parse( xmlFile );

            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();

                Node protocolNode = attributes.getNamedItem( attriName );

                if( protocolNode != null )
                {
                    if( protocolNode.getNodeValue().equals( attriValue ) )
                    {
                        Node portNode = attributes.getNamedItem( targetName );

                        portNode.setNodeValue( value );

                        break;
                    }
                }
            }

            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer transformer = factory.newTransformer();

            DOMSource domSource = new DOMSource( document );

            StreamResult result = new StreamResult( xmlFile );

            transformer.transform( domSource, result );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    @Override
    public void setShutdownPort( String port )
    {
        setPortalServerPortValue(
            new File( getAppServerDir().toPortableString(), "conf/server.xml" ), "Server", "shutdown", "SHUTDOWN",
            "port", port );
    }

    @Override
    public void setTelnetPort( String port )
    {
        File portalExt = liferayHome.append( PORTAL_EXT_PROPERTIES ).toFile();

        try
        {
            if( !portalExt.exists() )
            {
                portalExt.createNewFile();
            }

            final PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

            InputStream in = new FileInputStream( portalExt );

            config.load( in );

            in.close();

            if( !config.containsKey( FRAMEWORK_OSGI_CONSOLE_NAME ) )
            {
                config.addProperty( FRAMEWORK_OSGI_CONSOLE_NAME, "localhost:" + port );
            }
            else
            {
                config.setProperty( FRAMEWORK_OSGI_CONSOLE_NAME, "localhost:" + port );
            }

            config.save( portalExt );

        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

}
