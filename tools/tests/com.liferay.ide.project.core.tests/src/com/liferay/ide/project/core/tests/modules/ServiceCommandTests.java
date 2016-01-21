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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.junit.Test;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ServiceCommand;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.tests.ServerCoreBase;

/**
 * @author Lovett Li
 */
public class ServiceCommandTests extends ServerCoreBase
{
    private PortalRuntime portalRuntime = null;

    public void startServer() throws Exception
    {
        final IServer server = getServer();

        final String exceFileName = Platform.getOS().contains( "win" ) ? "catalina.bat" : "catalina.sh";

        final LaunchHelper launchHelper = new LaunchHelper();

        launchHelper.setLaunchSync( false );

        final IPath serverLocation = server.getRuntime().getLocation().append( "bin" );

        setupAgent();

        launchHelper.launch( getLaunchConfig( serverLocation, exceFileName, "run" ), ILaunchManager.RUN_MODE, null );

        long timeoutExpiredMs = System.currentTimeMillis() + 120000;
        int Started = 0;

        while( true )
        {
            try
            {
                if( System.currentTimeMillis() > timeoutExpiredMs )
                {
                    break;
                }

                URL pingUrl = new URL( "http://localhost:" + liferayServerStartPort );
                URLConnection conn = pingUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                Started = ( (HttpURLConnection) conn ).getResponseCode();

                if( Started != 0 )
                {
                    break;
                }

            }
            catch( Exception e )
            {
                Thread.sleep( 200 );
            }
        }

        assertEquals( 200, Started );
    }

    public void stopServer() throws Exception
    {
        final IServer server = getServer();

        final String exceFileName = Platform.getOS().contains( "win" ) ? "shutdown.bat" : "shutdown.sh";

        final LaunchHelper launchHelper = new LaunchHelper();

        launchHelper.setLaunchSync( false );

        final IPath serverLocation = server.getRuntime().getLocation().append( "bin" );

        setupAgent();

        launchHelper.launch( getLaunchConfig( serverLocation, exceFileName, "run" ), ILaunchManager.RUN_MODE, null );

        long timeoutExpiredMs = System.currentTimeMillis() + 120000;
        int Started = 0;

        while( true )
        {
            try
            {
                if( System.currentTimeMillis() > timeoutExpiredMs )
                {
                    break;
                }

                URL pingUrl = new URL( "http://localhost:" + liferayServerStartPort );
                URLConnection conn = pingUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                Started = ( (HttpURLConnection) conn ).getResponseCode();

                if( Started != 200 )
                {
                    break;
                }
            }
            catch( Exception e )
            {
                break;
            }
        }
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-7.0-ce-m4/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-7.0-ce-m4-20150224120313668.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.portal.runtime";
    }

    @Override
    public void setupRuntime() throws Exception
    {
        if( shouldSkipServerTests() )
            return;

        extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );

        IProgressMonitor npm = new NullProgressMonitor();

        final String name = "ServicesTest";
        final IRuntimeWorkingCopy runtimeWC = ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

        assertNotNull( runtimeWC );

        runtimeWC.setName( name );
        runtimeWC.setLocation( getLiferayRuntimeDir() );
        runtime = runtimeWC.save( true, npm );

        portalRuntime = (PortalRuntime) ServerCore.findRuntime( name ).loadAdapter( PortalRuntime.class, npm );
    }

    protected void setupServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        final IServerWorkingCopy serverWC = createServerForRuntime( "7.0.0", runtime );

        ServerDelegate delegate = (ServerDelegate) serverWC.loadAdapter( ServerDelegate.class, null );
        delegate.importRuntimeConfiguration( serverWC.getRuntime(), null );
        serverWC.setAttribute( "AGENT_PORT", "29998" );
        server = serverWC.save( true, npm );

        assertNotNull( server );
    }

    @Test
    public void GetStaticService() throws Exception
    {
        String[] IntegrationPoints = new ServiceCommand( null ).execute();

        assertNotNull( IntegrationPoints );

        assertTrue( IntegrationPoints.length > 0 );
    }

    @Test
    public void GetStaticServiceBundle() throws Exception
    {
        String[] serviceBundle =
            new ServiceCommand( null, "com.liferay.bookmarks.service.BookmarksEntryLocalService" ).execute();
        String[] serviceBundleNoExportPackage =
            new ServiceCommand( null, "com.liferay.announcements.web.messaging.CheckEntryMessageListener" ).execute();
        String[] serviceBundleNotExit = new ServiceCommand( null, "com.liferay.test.TestServiceNotExit" ).execute();

        assertEquals( "com.liferay.bookmarks.api", serviceBundle[0] );
        assertEquals( "1.0.0", serviceBundle[1] );

        assertEquals( "com.liferay.announcements.web", serviceBundleNoExportPackage[0] );
        assertEquals( "1.0.0", serviceBundleNoExportPackage[1] );

        assertNull( serviceBundleNotExit );
    }

    @Test
    public void GetService() throws Exception
    {
        startServer();

        IServer server = getServer();

        String[] IntegrationPoints = new ServiceCommand( server ).execute();

        assertNotNull( IntegrationPoints );

        assertTrue( IntegrationPoints.length > 0 );

        stopServer();
    }

    @Test
    public void GetServiceBundle() throws Exception
    {
        startServer();

        IServer server = getServer();

        String[] serviceBundle =
            new ServiceCommand( server, "com.liferay.bookmarks.service.BookmarksEntryLocalService" ).execute();
        String[] serviceBundleNoExportPackage =
            new ServiceCommand( null, "com.liferay.announcements.web.messaging.CheckEntryMessageListener" ).execute();
        String[] serviceBundleNotExit = new ServiceCommand( null, "com.liferay.test.TestServiceNotExit" ).execute();

        assertEquals( "com.liferay.bookmarks.api", serviceBundle[0] );
        assertEquals( "1.0.0", serviceBundle[1] );

        assertEquals( "com.liferay.announcements.web", serviceBundleNoExportPackage[0] );
        assertEquals( "1.0.0", serviceBundleNoExportPackage[1] );

        assertNull( serviceBundleNotExit );

        stopServer();
    }

    private ILaunchConfigurationWorkingCopy getLaunchConfig( IPath workingDir, String execFileName, String command )
        throws CoreException
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ILaunchConfigurationType configType =
            launchManager.getLaunchConfigurationType( "org.eclipse.ui.externaltools.ProgramLaunchConfigurationType" );
        ILaunchConfigurationWorkingCopy config =
            configType.newInstance( null, launchManager.generateLaunchConfigurationName( "tomcat-server" ) );

        config.setAttribute( "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true );
        config.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", true );
        config.setAttribute( "org.eclipse.debug.ui.ATTR_PRIVATE", true );

        String execPath = workingDir.append( execFileName ).toOSString();

        new File( execPath ).setExecutable( true );

        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_LOCATION", execPath );
        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", workingDir.toOSString() );
        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS", command );

        return config;
    }

    private void setupAgent()
    {
        final IPath modulesPath = portalRuntime.getPortalBundle().getLiferayHome().append( "osgi/modules" );
        final IPath agentInstalledPath = modulesPath.append( "biz.aQute.remote.agent.jar" );

        if( !agentInstalledPath.toFile().exists() )
        {
            try
            {
                final File file = new File ( FileLocator.toFileURL(
                    LiferayServerCore.getDefault().getBundle().getEntry( "bundles/biz.aQute.remote.agent-3.1.0.jar" ) ).getFile() );

                FileUtil.copyFile( file, modulesPath.append( "biz.aQute.remote.agent.jar" ).toFile() );
            }
            catch( IOException e )
            {
            }
        }
    }
}