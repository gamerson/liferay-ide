/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved./
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
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Terry Jia
 */
public class ServerManagerTests extends BaseTests
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;
    private final static String portalSetupWizardFileName = "portal-setup-wizard.properties";
    private final static String remoteIDEConnectorLPKGFileName = "Remote IDE Connector CE.lpkg";
    private static IServerManagerConnection service;
    private final static String testApplicationPartialWarFileName = "test-portlet-partial.war";
    private final static String testApplicationWarFileName = "test-portlet.war";

    protected File getProjectFile( String fileName )
    {
        try
        {
            File tempFile = File.createTempFile( System.currentTimeMillis() + "", fileName );

            FileUtil.writeFileFromStream( tempFile, this.getClass().getResourceAsStream( "files/" + fileName ) );

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

    protected IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            liferayBundlesPath = new Path( liferayBundlesDir );
        }

        return liferayBundlesPath;
    }

    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    protected String getRuntimeVersion()
    {
        return "6.2.0";
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

    protected File getTestApplicationPartialWar()
    {
        return getProjectFile( testApplicationPartialWarFileName );
    }

    @Before
    public void setupPluginsSDKAndRuntime() throws Exception
    {
        assertNotNull(
            "Expected System.getProperty(\"liferay.bundles.dir\") to not be null",
            System.getProperty( "liferay.bundles.dir" ) );

        assertNotNull( "Expected liferayBundlesDir to not be null", liferayBundlesDir );

        // Testing liferay runtime start
        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );
        // Testing liferay runtime end

        // Testing liferay server start
        final IServerWorkingCopy serverWC = ServerUtil.createServerForRuntime( liferayRuntime.getRuntime() );
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

        result = service.updateApplication( "test-application", getTestApplicationPartialWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Modified Test Portlet is success", null, result );

        result = service.uninstallApplication( "test-application", npm );

        assertEquals( "Expected uninstall the Test Portlet is success", null, result );
    }

}
