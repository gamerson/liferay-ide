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
package com.liferay.ide.server.websphere.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author Simon Jiang
 */

public class WebsphereServerProductInfoHandler
{
    private class WebsphereServerProductInfo
    {
        String productXmlStr = null;
        String parsedBuildVersion = null;
        String parsedProductId = null;
        String parsedReleaseVersion = null;

        public WebsphereServerProductInfo( String curProductXmlStr )
        {
            this.productXmlStr = curProductXmlStr;

            parseProductInfo();
        }

        public String getProductId()
        {
            if( this.parsedProductId != null )
            {
                return this.parsedProductId;
            }

            String productId = null;

            if( this.productXmlStr != null )
            {
                int i = this.productXmlStr.indexOf( "<id>" );

                if( i < 0 )
                {
                    return null;
                }
                int j = this.productXmlStr.indexOf( "</id>", i );

                if( j < 0 )
                {
                    return null;
                }
                productId = this.productXmlStr.substring( i + 4, j );
            }
            return productId;
        }

        public String getReleaseVersion()
        {
            if( this.parsedReleaseVersion != null )
            {
                return this.parsedReleaseVersion;
            }

            String releaseVersion = null;

            if( this.productXmlStr != null )
            {
                int i = this.productXmlStr.indexOf( "<version>" );

                if( i < 0 )
                {
                    return null;
                }
                int j = this.productXmlStr.indexOf( "</version>", i );

                if( j < 0 )
                {
                    return null;
                }
                releaseVersion = this.productXmlStr.substring( i + 9, j );
            }
            return releaseVersion;
        }

        private void parseProductInfo()
        {
            if( this.productXmlStr == null )
            {
                return;
            }

            StringTokenizer st = new StringTokenizer( this.productXmlStr, "\n" );

            do
            {
                if( !( st.hasMoreTokens() ) )
                {
                    return;
                }
            }
            while( !( st.nextToken().startsWith( "Installed Product" ) ) );

            while( ( ( ( this.parsedBuildVersion == null ) || ( this.parsedProductId == null ) ||
                ( this.parsedReleaseVersion == null ) ) ) && ( st.hasMoreTokens() ) )
            {
                String curToken = st.nextToken();

                if( curToken.startsWith( "Version" ) )
                {
                    this.parsedReleaseVersion = curToken.substring( 7 ).trim();
                }
                else if( curToken.startsWith( "ID" ) )
                {
                    this.parsedProductId = curToken.substring( 2 ).trim();
                }
                else if( curToken.startsWith( "Build Level" ) )
                {
                    this.parsedBuildVersion = curToken.substring( 11 ).trim();
                }
            }
        }

        public String toString()
        {
            return this.productXmlStr;
        }
    }

    private WebsphereServerProductInfo productInfo = null;

    public WebsphereServerProductInfoHandler( String serverProductInfoFilePath ) throws IOException
    {
        File file = new File( serverProductInfoFilePath );

        if( file.exists() )
        {
            try( BufferedReader input = new BufferedReader( new FileReader( file.getAbsoluteFile() ) ) )
            {
                StringBuffer buffer = new StringBuffer();
                String str;

                while( ( str = input.readLine() ) != null )
                {
                    buffer.append( str );
                }
                this.productInfo = new WebsphereServerProductInfo( buffer.toString() );
            }
        }
    }

    public String getProductId()
    {
        return this.productInfo.getProductId();
    }

    public String getReleaseVersion()
    {
        return this.productInfo.getReleaseVersion();
    }
}
