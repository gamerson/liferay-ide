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
import static org.junit.Assert.fail;

import com.liferay.ide.project.core.modules.BladeCLI;

import aQute.remote.api.Agent;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class BladeCLITests
{

    @Test
    public void bladeCLICreateProject() throws Exception
    {
        Path temp = Files.createTempDirectory( "path with spaces" );

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append(  "-d \"" + temp.toAbsolutePath().toString() +  "\" " );
        sb.append( "-t mvcportlet " );
        sb.append( "foo" );

        BladeCLI.execute( sb.toString() );

        assertTrue( new File( temp.toFile(), "foo/build.gradle" ).exists() );
    }

    @Test
    public void bladeCLIExecute() throws Exception
    {
        String[] output = BladeCLI.execute( "help" );

        assertNotNull( output );

        assertTrue( output.length > 0 );

        for( String line : output )
        {
            if( line.contains( "[null]" ) )
            {
                fail( "Output contains [null]" );
            }
        }
    }

    @Test
    public void bladeCLIProjectTemplates() throws Exception
    {
        String[] projectTemplates = BladeCLI.getProjectTemplates();

        assertNotNull( projectTemplates );

        assertTrue( projectTemplates.length > 0 );
    }

    @Test
    public void bladeCLIGetIntegrationPoints() throws Exception
    {
        if( testConnect() )
        {
            String[] IntegrationPoints = BladeCLI.getIntegrationPoints();

            assertNotNull( IntegrationPoints );

            assertTrue( IntegrationPoints.length > 0 );
        }
    }

    @Test
    public void bladeCLIGetServiceBundle() throws Exception
    {
        if( testConnect() )
        {
            String[] serviceBundle =
                BladeCLI.getServiceBundle( "com.liferay.bookmarks.service.BookmarksEntryLocalService" );
            String[] serviceBundleNoExportPackage =
                BladeCLI.getServiceBundle( "com.liferay.site.teams.web.upgrade.SiteTeamsWebUpgrade" );

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
