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
package com.liferay.ide.server.websphere.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.websphere.core.IWebsphereServer;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereProfile;
import com.liferay.ide.server.websphere.core.WebsphereProfileProperties;
import com.liferay.ide.server.websphere.core.WebspherePropertyValueHandler;
import com.liferay.ide.server.websphere.core.WebsphereServerProductInfoHandler;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereUtil
{

    public static final String[] WAS_EDITION_IDS = { "BASE", "ND", "EXPRESS", "embeddedEXPRESS", "NDDMZ" };
    private static final String DEFAULT_LIFERAY_PORTAL_APP_NAME = ".*liferay.*";

    private static void bindPort( String host, int port ) throws Exception
    {
        Socket s = new Socket();
        s.bind( new InetSocketAddress( host, port ) );
        s.close();
    }

    public static String ensureEndingPathSeparator( String curPath, boolean isEndWithPathSeparator )
    {
        if( curPath != null )
        {
            boolean curIsEndWithPathSeparator = curPath.replace( '\\', '/' ).endsWith( "/" );
            if( curIsEndWithPathSeparator != isEndWithPathSeparator )
            {
                if( isEndWithPathSeparator )
                {
                    curPath = curPath + "/";
                }
                else
                {
                    curPath = curPath.substring( 0, curPath.length() - 1 );
                }
            }
        }
        return curPath;
    }

    public static String[] extractLiferayAppFolder( IPath profileLocation, String cellName, String nodeName )
    {
        List<String> liferayAppNameValues = new ArrayList<String>();

        if( profileLocation == null || !profileLocation.toFile().exists() )
        {
            return null;
        }

        Pattern pattern = Pattern.compile( DEFAULT_LIFERAY_PORTAL_APP_NAME );

        IPath serverIndexFile =
            profileLocation.append( "config" ).append( "cells" ).append( cellName ).append( "nodes" ).append(
                nodeName ).append( "serverindex.xml" );
        try(InputStreamReader reader =
            new InputStreamReader( new FileInputStream( serverIndexFile.toFile() ), "utf-8" ))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document document = parser.parse( new InputSource( reader ) );
            NodeList extendApplications = document.getElementsByTagName( "extendedApplicationDataElements" );

            for( int j = 0; j < extendApplications.getLength(); j++ )
            {
                final Node extendApplication = extendApplications.item( j );
                Node applicationNameValueNode = extendApplication.getAttributes().getNamedItem( "applicationName" );

                if( applicationNameValueNode != null )
                {
                    String applicationNamve = applicationNameValueNode.getNodeValue();
                    if( pattern.matcher( applicationNamve ).matches() )
                    {
                        liferayAppNameValues.add( applicationNamve );
                        Node extendApplicatonNode =
                            extendApplication.getAttributes().getNamedItem( "standaloneModuleName" );
                        liferayAppNameValues.add( extendApplicatonNode.getNodeValue() );
                    }
                }
            }
        }
        catch( IOException | SAXException | ParserConfigurationException e )
        {
            WebsphereCore.logError( e );
        }
        return liferayAppNameValues.toArray( new String[liferayAppNameValues.size()] );
    }

    public static String getLiferayPortalVersionInfo( URL portalUrl )
    {
        try
        {
            Map<String, List<String>> fieldsMap = portalUrl.openConnection().getHeaderFields();
            if( fieldsMap != null )
            {
                List<String> portalField = fieldsMap.get( "Liferay-Portal" );

                if( portalField != null )
                {
                    return portalField.get( 0 );
                }
            }
        }
        catch( IOException e )
        {
        }

        return null;
    }

    public static File getNormalizedPath( File file )
    {
        if( file == null )
        {
            return null;
        }

        String sPath = file.getAbsolutePath();

        boolean fIsTrailingFileSep = sPath.endsWith( File.separator );
        boolean fIsStartingFileSep = sPath.startsWith( File.separator );

        StringTokenizer st = new StringTokenizer( sPath, File.separator );
        List<String> listPathNames = new Vector<String>();

        while( st.hasMoreTokens() )
        {
            String sToken = st.nextToken();

            if( ".".equals( sToken ) )
            {
                continue;
            }

            if( ( listPathNames.size() > 1 ) && CoreUtil.isWindows() && ( "..".equals( sToken ) ) )
            {
                listPathNames.remove( listPathNames.size() - 1 );
            }
            else if( ( listPathNames.size() > 0 ) && ( !CoreUtil.isWindows() ) && ( "..".equals( sToken ) ) )
            {
                listPathNames.remove( listPathNames.size() - 1 );
            }
            else if( !( "..".equals( sToken ) ) )
            {
                listPathNames.add( sToken );
            }
        }

        StringBuffer sb = new StringBuffer();

        if( fIsStartingFileSep )
        {
            sb.append( File.separator );
        }

        Iterator<String> i = listPathNames.iterator();
        while( i.hasNext() )
        {
            sb.append( i.next() );
            if( !( i.hasNext() ) )
            {
                continue;
            }
            sb.append( File.separator );
        }

        if( fIsTrailingFileSep )
        {
            sb.append( File.separator );
        }
        else if( CoreUtil.isWindows() && ( listPathNames.size() == 1 ) )
        {
            sb.append( File.separator );
        }
        return new File( sb.toString() );
    }

    public static int getWebsphereProcessPID( IWebsphereServer server )
    {
        IPath websphereProfileLocation = new Path( server.getWebsphereProfileLocation() );
        String websphereServerName = server.getWebsphereServerName();
        IPath pidFolder = websphereProfileLocation.append( "logs" ).append( websphereServerName );

        IPath pidLocation = null;
        if( pidFolder != null && pidFolder.toFile().exists() )
        {
            pidLocation = pidFolder.append( websphereServerName ).append( ".pid" );

            if( !( pidLocation.toFile().exists() ) )
            {
                return 0;
            }
        }

        int pid = -1;

        try(BufferedReader bufferReader = new BufferedReader( new FileReader( pidLocation.toFile() ) ))
        {
            pid = Integer.parseInt( bufferReader.readLine() );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return pid;
    }

    public static boolean isPortAvailable( int port )
    {
        try
        {
            bindPort( "0.0.0.0", port );
            bindPort( InetAddress.getLocalHost().getHostAddress(), port );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public static String resolveVariablesIfNecessary( String wasInstallLocation, String locationPropertyValue )
    {
        WebspherePropertyValueHandler mapVariable = new WebspherePropertyValueHandler( wasInstallLocation );
        String result = mapVariable.convertVariableString( locationPropertyValue );
        return result;
    }

    public static boolean getIsRuntimeExists( IPath path, String targetRuntimeVersion )
    {
        if( ( path == null ) || ( !( path.toFile().exists() ) ) )
        {
            return false;
        }

        boolean result = true;

        try
        {
            IPath wasProductPath = path.append( "/properties/version/WAS.product" );

            if( wasProductPath.toFile().exists() )
            {
                WebsphereServerProductInfoHandler wasInfo =
                    new WebsphereServerProductInfoHandler( wasProductPath.toOSString() );

                if( ( wasInfo != null ) && ( wasInfo.getProductId() != null ) )
                {
                    String id = wasInfo.getProductId();
                    boolean matched = false;

                    for( String ids : WAS_EDITION_IDS )
                    {
                        if( ids.equals( id ) )
                        {
                            matched = true;
                            break;
                        }
                    }

                    if( !( matched ) )
                    {
                        result = false;
                    }

                    if( result )
                    {
                        String version = wasInfo.getReleaseVersion();
                        Version relVersion = new Version( version );

                        if( relVersion != null )
                        {
                            Integer major = Integer.valueOf( relVersion.getMajor() );
                            Integer minor = Integer.valueOf( relVersion.getMinor() );
                            Integer micro = Integer.valueOf( relVersion.getMicro() );
                            String targetRuntimeType =
                                "com.ibm.websphere." + major.toString() + minor.toString() + micro.toString();

                            if( !( targetRuntimeType.equals( targetRuntimeVersion ) ) )
                                result = false;
                        }
                    }
                }
            }
            else
            {
                result = false;
            }
        }
        catch( Exception e )
        {
            result = false;
        }

        return result;
    }

    public static List<WebsphereProfile> getProfileList( IPath websphereHomeLocation ) throws Exception
    {
        return listProfilesInRegistry( getRegistryFile( websphereHomeLocation ) );
    }

    public static File getRegistryFile( IPath websphereHomeLocation )
    {
        IPath profileRegistryPath = null;
        try
        {
            WebsphereProfileProperties websphereProfileProperties = new WebsphereProfileProperties( websphereHomeLocation );
            profileRegistryPath = websphereProfileProperties.getProperty( "WS_PROFILE_REGISTRY" );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return profileRegistryPath.toFile();
    }


    public static List<WebsphereProfile> listProfilesInRegistry( File registryFile ) throws Exception
    {
        List<WebsphereProfile> listProfiles = readProfileRegistry( registryFile );

        return listProfiles;
    }

    private static Vector<WebsphereProfile> readProfileRegistry( File registryFile ) throws Exception
    {
        if( registryFile != null && !registryFile.exists() )
        {
            return new Vector<WebsphereProfile>();
        }

        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        try
        {
            Document document = documentbuilderfactory.newDocumentBuilder().parse( registryFile );

            NodeList nodelistProfiles = document.getElementsByTagName( "profile" );

            return unmarshallProfilesFromDOM( nodelistProfiles );
        }
        catch( Exception e )
        {
        }
        return null;
    }

    private static Vector<WebsphereProfile> unmarshallProfilesFromDOM( NodeList nodelistProfiles )
    {
        Vector<WebsphereProfile> vprofiles = new Vector<WebsphereProfile>();

        for( int i = 0; i < nodelistProfiles.getLength(); ++i )
        {
            Node nodeProfileThis = nodelistProfiles.item( i );

            boolean fIsDefault = Boolean.valueOf(
                nodeProfileThis.getAttributes().getNamedItem( "isDefault" ).getNodeValue() ).booleanValue();

            String sName = nodeProfileThis.getAttributes().getNamedItem( "name" ).getNodeValue();

            String sPath = nodeProfileThis.getAttributes().getNamedItem( "path" ).getNodeValue();

            WebsphereProfile profileThis = new WebsphereProfile( sName, new File( sPath ), fIsDefault );

            vprofiles.add( profileThis );
        }

        return vprofiles;
    }


    public static WebsphereProfile getDefaultProfile( IPath sWASHome ) throws Exception
    {
        List<WebsphereProfile> listProfiles = getProfileList( sWASHome );

        for( int i = 0; i < listProfiles.size(); ++i )
        {
            WebsphereProfile profileThis = listProfiles.get( i );

            if( profileThis.isDefault() )
            {
                return profileThis;
            }
        }

        return null;
    }

    public static WebsphereProfile getProfile( IPath sWASHome, String sProfileName ) throws Exception
    {
        List<WebsphereProfile> listProfiles = getProfileList( sWASHome );

        for( int i = 0; i < listProfiles.size(); ++i )
        {
            WebsphereProfile profileThis = listProfiles.get( i );

            if( profileThis.getName().equals( sProfileName ) )
            {
                return profileThis;
            }
        }

        return null;
    }

    public static boolean canWriteToDirectory( File file )
    {
        if( CoreUtil.isWindows() )
        {
            String baseTempFile = ensureEndingPathSeparator( file.getPath(), true ) + "ST_TEST_FILE";
            String testFile = baseTempFile;

            File tempFile = new File( testFile );

            for( int i = 0; tempFile.exists(); ++i )
            {
                testFile = baseTempFile + i;
                tempFile = new File( testFile );
            }
            try
            {
                if( tempFile.createNewFile() )
                {
                    boolean canWrite = true;
                    if( isFileInVistaVirtualStore( testFile ) )
                    {
                        canWrite = false;
                    }

                    return canWrite;
                }

                return false;
            }
            catch( IOException e )
            {
                return false;
            }
        }

        return file.canWrite();
    }

    public static boolean isFileInVistaVirtualStore( String filePath )
    {
        if( ( filePath == null ) || ( filePath.length() == 0 ) )
        {
            return false;
        }
        String testPath = System.getenv( "LOCALAPPDATA" );

        if( ( testPath == null ) || ( testPath.length() == 0 ) )
        {
            return false;
        }
        testPath = testPath + "\\VirtualStore";
        int i = filePath.indexOf( ":" );

        if( i != -1 )
        {
            if( i + 1 != filePath.length() )
            {
                testPath = testPath + filePath.substring( i + 1 );
            }
        }
        else
        {
            testPath = testPath + filePath;
        }

        File tempFile = new File( testPath );
        return tempFile.exists();
    }
}
