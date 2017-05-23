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

import com.liferay.ide.server.websphere.util.WebsphereUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class WebsphereSDKUtilities
{

    public static final String SEPARATOR_CHAR = System.getProperty( "file.separator" );

    public static FilenameFilter PROPERTIES_FILTER = new FilenameFilter()
    {

        public boolean accept( File dir, String name )
        {
            return name.endsWith( ".properties" );
        }
    };

    public static String constructSDKDisplayName( String version, String bits )
    {
        if( bits == null )
        {
            return "";
        }

        WebsphereSDKInfo.Bits b;

        if( bits.trim().equals( "64" ) )
        {
            b = WebsphereSDKInfo.Bits._64_;
        }
        else
        {
            b = WebsphereSDKInfo.Bits._32_;
        }
        return constructSDKDisplayName( version, b );
    }

    public static String constructSDKDisplayName( String version, WebsphereSDKInfo.Bits bits )
    {
        final String SDK_DISPLAY_NAME = "WebSphere Application Server JRE {0}, {1} bit";

        if( ( version == null ) || ( bits == null ) )
        {
            return "";
        }

        String sBits = "32";
        if( bits == WebsphereSDKInfo.Bits._64_ )
        {
            sBits = "64";
        }
        String sdkLogicalName = MessageFormat.format( SDK_DISPLAY_NAME, new Object[] { version, sBits } );

        return sdkLogicalName;
    }

    public static String getWebsphereDefaultSDKId( String wasHome )
    {
        String defaultSDKId = null;

        String propertiesDirectory = wasHome + SEPARATOR_CHAR + "properties" + SEPARATOR_CHAR + "sdk";

        String propertyFileName = propertiesDirectory + SEPARATOR_CHAR + "cmdDefaultSDK.properties";

        try(FileInputStream fileInputStream = new FileInputStream( new File( propertyFileName ) ))
        {

            Properties properties = new Properties();
            properties.load( fileInputStream );
            defaultSDKId = properties.getProperty( "COMMAND_DEFAULT_SDK" );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        if( defaultSDKId == null )
        {
            String arch = System.getProperty( "sun.arch.data.model" );
            if( ( arch != null ) && ( !( arch.equals( "unknown" ) ) ) )
            {
                defaultSDKId = "1.6_" + arch;
            }
            else
            {
                defaultSDKId = "1.6_32";
            }
        }
        return defaultSDKId;
    }

    public static List<WebsphereSDKInfo> getWebsphereSDKInfo( String wasHome )
    {
        ArrayList<WebsphereSDKInfo> sdkInfoList = new ArrayList<WebsphereSDKInfo>();
        WebsphereSDKInfo sdkInfo = null;

        String propertiesDirectory = wasHome + SEPARATOR_CHAR + "properties" + SEPARATOR_CHAR + "sdk";

        File directory = new File( propertiesDirectory );
        String[] propertyFileName = directory.list( PROPERTIES_FILTER );

        if( propertyFileName == null )
        {
            ArrayList<WebsphereSDKInfo> localArrayList1 = sdkInfoList;
            return localArrayList1;
        }
        List<String> propFileList = Arrays.asList( propertyFileName );
        Enumeration<?> enumeration = null;
        Object element = null;

        for( int i = 0; propFileList.size() > i; ++i )
        {
            if( propertyFileName[i].equals( "cmdDefaultSDK.properties" ) )
            {
                continue;
            }

            if( propertyFileName[i].equals( "newProfileDefaultSDK.properties" ) )
            {
                continue;
            }

            Properties properties = new Properties();
            File sdkPropFile = new File( propertiesDirectory + SEPARATOR_CHAR + propertyFileName[i] );
            if( sdkPropFile.exists() )
            {
                try(FileInputStream fileInputStream = new FileInputStream( sdkPropFile ))
                {
                    properties.load( fileInputStream );
                    String sdkVersion = null;
                    String sdkBits = null;
                    String sdkLocation = null;
                    String sdkArchitecture = null;
                    String sdkPlatform = null;

                    for( enumeration = properties.propertyNames(); enumeration.hasMoreElements(); )
                    {
                        element = enumeration.nextElement();

                        if( ( element != null ) && ( element.toString().contains( "com.ibm.websphere.sdk.version" ) ) )
                        {
                            sdkVersion = properties.getProperty( element.toString() );
                        }

                        if( ( element != null ) && ( element.toString().contains( "com.ibm.websphere.sdk.bits" ) ) )
                        {
                            sdkBits = properties.getProperty( element.toString() );
                        }

                        if( ( element != null ) && ( element.toString().contains( "com.ibm.websphere.sdk.location" ) ) )
                        {
                            sdkLocation = properties.getProperty( element.toString() );
                            sdkLocation = WebsphereUtil.resolveVariablesIfNecessary( wasHome, sdkLocation );
                        }

                        if( ( element != null ) && ( element.toString().contains( "com.ibm.websphere.sdk.platform" ) ) )
                        {
                            sdkPlatform = properties.getProperty( element.toString() );
                        }

                        if( ( element != null ) &&
                            ( element.toString().contains( "com.ibm.websphere.sdk.architecture" ) ) )
                        {
                            sdkArchitecture = properties.getProperty( element.toString() );
                        }
                    }

                    String sdkId = propertyFileName[i];
                    int index = sdkId.indexOf( ".properties" );

                    if( index != -1 )
                    {
                        sdkId = sdkId.substring( 0, index );
                    }
                    sdkInfo = new WebsphereSDKInfo(
                        sdkId, constructSDKDisplayName( sdkVersion, sdkBits ), sdkVersion, sdkBits, sdkLocation,
                        sdkPlatform, sdkArchitecture );

                    if( ( sdkInfo != null ) && ( isSDKInfoValid( sdkInfo ) ) )
                        sdkInfoList.add( sdkInfo );
                }
                catch( IOException e )
                {
                    WebsphereCore.logError( e );
                }
            }
        }
        return sdkInfoList;
    }

    public static boolean isSDKInfoValid( WebsphereSDKInfo sdkInfo )
    {
        if( sdkInfo == null )
        {
            return false;
        }
        boolean isValid = ( sdkInfo.getId() != null ) && ( sdkInfo.getDisplayName() != null ) &&
            ( sdkInfo.getVersion() != null ) && ( sdkInfo.getBits() != null ) && ( sdkInfo.getLocation() != null ) &&
            ( sdkInfo.getArchitecture() != null ) && ( sdkInfo.getPlatform() != null );

        return isValid;
    }
}
