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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WebsphereServerProductInfoHandler
{

    private WebsphereServerProductInfo productInfo = null;

    public WebsphereServerProductInfoHandler( String serverProductInfoFilePath ) throws IOException
    {
        File file = new File( serverProductInfoFilePath );

        if( file.exists() )
        {
            try(BufferedReader input = new BufferedReader( new FileReader( file.getAbsoluteFile() ) ))
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

    public String getBuildVersion()
    {
        return this.productInfo.getBuildVersion();
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
