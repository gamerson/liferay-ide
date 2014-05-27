/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
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

package com.liferay.ide.server.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Terry Jia
 */
public class ServerManagerTests extends BaseTests
{

    private final static String portalSetupWizardFileName = "portal-setup-wizard.properties";
    private final static String remoteIDEConnectorLPKGFileName = "Remote IDE Connector CE.lpkg";
    private static IServerManagerConnection service;
    private final static String testApplicationPartialModificationWarFileName = "test-portlet-partial-modification.war";
    private final static String testApplicationPartialDeletionWarFileName = "test-portlet-partial-deletion.war";
    private final static String testApplicationWarFileName = "test-portlet.war";

    protected File getProjectFile( String fileName )
    {
        try
        {
            File tempFile = File.createTempFile( System.currentTimeMillis() + "", fileName );

            FileUtil.writeFileFromStream( tempFile, getClass().getResourceAsStream( "files/" + fileName ) );

            if( tempFile.exists() )
            {
                return tempFile;
            }
        }
        catch( IOException e )
        {
        }

        return null;
    }

    protected File getPortalSetupWizard()
    {
        return getProjectFile( portalSetupWizardFileName );
    }

    protected File getRemoteIDEConnectorLPKG()
    {
        return getProjectFile( remoteIDEConnectorLPKGFileName );
    }

    protected File getTestApplicationWar()
    {
        return getProjectFile( testApplicationWarFileName );
    }

    protected File getTestApplicationPartialModificationWar()
    {
        return getProjectFile( testApplicationPartialModificationWarFileName );
    }

    protected File getTestApplicationPartialDeletionWar()
    {
        return getProjectFile( testApplicationPartialDeletionWarFileName );
    }

    @Before
    public void setupServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime runtime = setupRuntime();

        // Testing liferay server start
        final IServerWorkingCopy serverWC = ServerUtil.createServerForRuntime( runtime );
        final IServer server = serverWC.save( true, npm );

        assertNotNull( server );

        final File remoteIDEConnectorFile = getRemoteIDEConnectorLPKG();

        IPath portalBundleFolder = runtime.getLocation().removeLastSegments( 1 );

        assertEquals(
            "Expected Remote IDE Connector LPKG file to exist:" + remoteIDEConnectorFile.getAbsolutePath(), true,
            remoteIDEConnectorFile.exists() );

        IPath deployPath = portalBundleFolder.append( "deploy" );
        File deployFolder = deployPath.toFile();

        if( !deployFolder.exists() )
        {
            deployFolder.mkdir();
        }

        assertEquals( "Expected the deploy folder to exist:" + deployPath.toOSString(), true, deployFolder.exists() );

        FileUtils.moveFile( remoteIDEConnectorFile, deployPath.append( remoteIDEConnectorLPKGFileName ).toFile() );

        final File portalSetupWizardFile = getPortalSetupWizard();

        assertEquals( "Expected portal-setupe-wizard.properties exist:" +
            portalSetupWizardFile.toPath().toAbsolutePath(), true, portalSetupWizardFile.exists() );

        FileUtils.moveFile( portalSetupWizardFile, portalBundleFolder.append( portalSetupWizardFileName ).toFile() );

        server.start( ILaunchManager.DEBUG_MODE, npm );

        assertEquals( "Expected server has started", IServer.STATE_STARTED, server.getServerState() );

        service = new ServerManagerConnection();

        service.setHost( "localhost" );
        service.setHttpPort( "8080" );
        service.setManagerContextPath( "/server-manager-web" );
        service.setUsername( "test@liferay.com" );
        service.setPassword( "test" );

        // Given the server 10 seconds to deploy remote IDE Connector plugin
        try
        {
            Thread.sleep( 10000 );
        }
        catch( Exception e )
        {
        }

        assertEquals( "Expected the remote connection's status should be alive", true, service.isAlive() );
        // Testing liferay server end
    }

    @Test
    public void testDeployApplication() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        assertEquals( "Expected the server doesn't have debug port", -1, service.getDebugPort() );

        assertEquals( "Expected the server state is started", "STARTED", service.getServerState() );

        Object result = service.installApplication( getTestApplicationWar().getAbsolutePath(), "test-application", npm );

        assertEquals( "Expected the Test Application has been installed", null, result );

        result = service.isAppInstalled( "test-application" );

        assertEquals( "Expected the Test Application has been installed", true, result );

        List<String> liferayPlugins = service.getLiferayPlugins();

        assertNotNull( liferayPlugins );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialModificationWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Modified Test Portlet is success", null, result );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialDeletionWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Deletion Test Portlet is success", null, result );

        result = service.uninstallApplication( "test-application", npm );

        assertEquals( "Expected uninstall the Test Portlet is success", null, result );
    }

}
