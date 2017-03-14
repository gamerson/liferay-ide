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

import java.util.StringTokenizer;

public class WebsphereServerProductInfo
{

    String productXmlStr = null;
    String parsedBuildDate = null;
    String parsedBuildVersion = null;
    String parsedProductId = null;
    String parsedReleaseVersion = null;

    public WebsphereServerProductInfo( String curProductXmlStr )
    {
        this.productXmlStr = curProductXmlStr;

        parseProductInfo();
    }

    public boolean checkBuildDateRange( String beginDate, String endDate )
    {
        if( ( beginDate == null ) || ( endDate == null ) )
        {
            return false;
        }
        String buildDateStr = getBuildDate();

        if( buildDateStr == null )
        {
            return true;
        }

        return( ( compareDateStr( buildDateStr, beginDate ) <= 0 ) &&
            ( compareDateStr( buildDateStr, endDate ) >= 0 ) );
    }

    public boolean checkBuildDateStart( String beginDate )
    {
        if( beginDate == null )
        {
            return false;
        }
        String buildDateStr = getBuildDate();
        if( buildDateStr == null )
        {
            return true;
        }

        return( compareDateStr( buildDateStr, beginDate ) <= 0 );
    }

    private int compareDateStr( String baseDateStr, String compareDateStr )
    {
        if( ( baseDateStr == null ) || ( compareDateStr == null ) )
        {
            return 0;
        }
        StringTokenizer baseTokenizer = new StringTokenizer( baseDateStr, "/" );
        StringTokenizer compareTokenizer = new StringTokenizer( compareDateStr, "/" );

        if( ( baseTokenizer.countTokens() != 3 ) || ( compareTokenizer.countTokens() != 3 ) )
        {
            return 0;
        }

        int baseMonthValue = Integer.parseInt( baseTokenizer.nextToken() );
        int compareMonthValue = Integer.parseInt( compareTokenizer.nextToken() );
        int baseDateValue = Integer.parseInt( baseTokenizer.nextToken() );
        int compareDateValue = Integer.parseInt( compareTokenizer.nextToken() );
        int baseYearValue = Integer.parseInt( baseTokenizer.nextToken() );
        int compareYearValue = Integer.parseInt( compareTokenizer.nextToken() );

        if( compareYearValue != baseYearValue )
        {
            return( compareYearValue - baseYearValue );
        }

        if( compareMonthValue != baseMonthValue )
        {
            return( compareMonthValue - baseMonthValue );
        }

        if( compareDateValue != baseDateValue )
        {
            return( compareDateValue - baseDateValue );
        }

        return 0;
    }

    public String getBuildDate()
    {
        if( this.parsedBuildDate != null )
        {
            return this.parsedBuildDate;
        }

        if( this.productXmlStr != null )
        {
            int i = this.productXmlStr.indexOf( "date=\"" );

            if( i < 0 )
            {
                return null;
            }

            int j = this.productXmlStr.indexOf( "\"", i + 6 );

            if( j < 0 )
            {
                return null;
            }
            this.parsedBuildDate = this.productXmlStr.substring( i + 6, j );
        }
        return this.parsedBuildDate;
    }

    public String getBuildVersion()
    {
        if( this.parsedBuildVersion != null )
        {
            return this.parsedBuildVersion;
        }

        String buildVersion = null;
        if( this.productXmlStr != null )
        {
            int i = this.productXmlStr.indexOf( "level=\"" );

            if( i < 0 )
            {
                return null;
            }
            int j = this.productXmlStr.indexOf( "\"/>", i );

            if( j < 0 )
            {
                return null;
            }

            buildVersion = this.productXmlStr.substring( i + 7, j );
        }
        return buildVersion;
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
            if( !( st.hasMoreTokens() ) )
            {
                return;
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
