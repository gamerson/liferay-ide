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
package com.liferay.ide.project.core.tests.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.project.core.util.OSGIComponentUtil;

import aQute.remote.api.Agent;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;

/**
 * @author Lovett Li
 */
public class OSGIComponentUtilTest
{

    @Test
    public void GetIntegrationPoints() throws Exception
    {
        if( testConnect() )
        {
            String[] IntegrationPoints = OSGIComponentUtil.getIntegrationPoints();

            assertNotNull( IntegrationPoints );

            assertTrue( IntegrationPoints.length > 0 );
        }
    }

    @Test
    public void GetServiceBundle() throws Exception
    {
        if( testConnect() )
        {
            String[] serviceBundle =
                OSGIComponentUtil.getServiceBundle( "com.liferay.bookmarks.service.BookmarksEntryLocalService" );
            String[] serviceBundleNoExportPackage =
                OSGIComponentUtil.getServiceBundle( "com.liferay.site.teams.web.upgrade.SiteTeamsWebUpgrade" );

            assertEquals( "com.liferay.bookmarks.api", serviceBundle[0] );
            assertEquals( "1.0.0", serviceBundle[1] );

            assertEquals( "com.liferay.site.teams.web", serviceBundleNoExportPackage[0] );
            assertEquals( "1.0.0", serviceBundleNoExportPackage[1] );
        }
    }

    private boolean testConnect()
    {
        InetSocketAddress address = new InetSocketAddress( "localhost", Integer.valueOf( Agent.DEFAULT_PORT ) );
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

}
