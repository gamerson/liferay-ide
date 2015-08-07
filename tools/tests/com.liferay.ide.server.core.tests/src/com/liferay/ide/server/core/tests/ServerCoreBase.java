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

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.MavenProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.LiferayPublishHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Module;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.junit.Before;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Li Lu
 */
public abstract class ServerCoreBase extends BaseTests
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;
    protected final static String liferayServerAjpPort = System.getProperty( "liferay.server.ajp.port" );
    protected final static String liferayServerShutdownPort = System.getProperty( "liferay.server.shutdown.port" );
    protected final static String liferayServerStartPort = System.getProperty( "liferay.server.start.port" );
    protected IRuntime runtime;
    protected static IServer server;
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );
    private final static String skipServerTests = System.getProperty( "skipServerTests" );
    
    public static IServerWorkingCopy createServerForRuntime( String id, IRuntime runtime )
    {
        for( IServerType serverType : ServerCore.getServerTypes() )
        {
            if( serverType.getRuntimeType().equals( runtime.getRuntimeType() ) )
            {
                try
                {
                    return serverType.createServer( id, null, runtime, null ); //$NON-NLS-1$
                }
                catch( CoreException e )
                {
                }
            }
        }

        return null;
    }

    protected static void extractRuntime( IPath zip , IPath dir ) throws Exception
    {
        final File liferayRuntimeDirFile = dir.toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = zip.toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, ProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );
    }

    public static void shutDownServer()
    {
        if( server == null )
        {
            IServer[] servers = ServerCore.getServers();
            if( servers.length != 0 )
                server = servers[0];
        }
        server.stop( false );

        long timeoutExpiredMs = System.currentTimeMillis() + 30000;
        while( true )
        {
            if( server.getServerState() == IServer.STATE_STOPPED )
            {
                break;
            }
            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                break;
            }
        }
    }

    protected void changeServerXmlPort( String currentPort, String targetPort )
    {
        final File serverXml = server.getRuntime().getLocation().append( "conf" ).append( "server.xml" ).toFile();

        assertEquals(
            "Expected the server.xml file to exist:" + serverXml.getAbsolutePath(), true, serverXml.exists() );

        try
        {
            String contents = CoreUtil.readStreamToString( new FileInputStream( serverXml ), true );

            contents = contents.replaceAll( currentPort, targetPort );

            CoreUtil.writeStreamFromString( contents, new FileOutputStream( serverXml ) );
        }
        catch( IOException e )
        {
        }

    }

    protected boolean checkProjectDeployed( String projectName, String expectedMessage ) throws Exception
    {
        boolean flag = false;

        if( runtime == null )
            runtime = ServerCore.getRuntimes()[0];

        long timeoutExpiredMs = System.currentTimeMillis() + 30000;
        while( true )
        {
            // check server output
            if( checkServerMessage( expectedMessage ) )
                return true;

            // check project copied to server
            IPath localhostDir = runtime.getLocation().append( "/work/Catalina/localhost/" );
            IPath file = localhostDir.append( projectName );

            if( file.toFile().exists() )
                return true;

            if( System.currentTimeMillis() >= timeoutExpiredMs )
                break;
        }

        return flag;
    }

    protected boolean checkServerMessage( CharSequence expectedMessage ) throws Exception
    {

        // liferay.{date}.log file in sync with server console output
        IPath path = runtime.getLocation().removeLastSegments( 1 ).append( "logs" );

        final String suffix = new SimpleDateFormat( "yyyy-MM-dd" ).format( Calendar.getInstance().getTime() );

        if( path.toFile().exists() )
        {
            File file = new File( path.append( "liferay." + suffix + ".log" ).toString() );

            if( file.exists() )
            {
                String[] contents = FileUtil.readLinesFromFile( file );
                for( String line : contents )
                {
                    if( line.contains( expectedMessage ) || line.matches( expectedMessage.toString() ) )
                        return true;
                }
            }
        }
        return false;
    }

    public void copyFileToServer( IServer server, String targetFolderLocation, String fileDir, String fileName )
        throws IOException
    {
        InputStream is = ServerCoreBase.class.getResourceAsStream( fileDir + "/" + fileName );

        assertNotNull( is );

        final IRuntime runtime = server.getRuntime();

        IPath portalBundleFolder = runtime.getLocation().removeLastSegments( 1 );

        IPath folderPath = portalBundleFolder.append( targetFolderLocation );

        File folder = folderPath.toFile();

        if( !folder.exists() )
        {
            folder.mkdir();
        }

        assertEquals(
            "Expected the " + targetFolderLocation + " to exist:" + folderPath.toOSString(), true, folder.exists() );

        File file = folderPath.append( fileName ).toFile();

        FileUtil.writeFileFromStream( file, is );

        assertEquals( "Expected the " + file.getName() + " to exist:" + file.getAbsolutePath(), true, file.exists() );
    }
    
    protected void deployMavenProject( final IProject project ) throws Exception
    {
        
        assertNotNull( project.getFile( "pom.xml" ) );

        final Job job = new Job( project.getName() + "run deploy" ) //$NON-NLS-1$
            {

                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    try
                    {
                        monitor.beginTask( "deploy", 100 );

                        new MavenProjectBuilder( project ).runMavenGoal( project, "package liferay:deploy", monitor );

                        monitor.worked( 80 );

                        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

                        monitor.worked( 10 );

                        project.refreshLocal( IResource.DEPTH_INFINITE, null );

                        monitor.worked( 10 );
                    }
                    catch( Exception e )
                    {
                    }

                    return Status.OK_STATUS;
                }
            };

        job.schedule();

        long timeoutExpiredMs = System.currentTimeMillis() + 20000;
        while( true )
        {
            IFile war = project.getFolder( "target" ).getFile( project.getName() + "-1.0.0-SNAPSHOT.war" );
            if( war.exists() )
            {
                break;
            }
            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                break;
            }
        }
    }
    
    protected void deploySDKProject( IProject project ) throws Exception
    {
        server = getServer();

        SDK sdk = SDKUtil.getSDK( project );
        IFile buildFile = project.getFile( "build.xml" );

        IStatus status = sdk.runCommand( project, buildFile, "deploy", null, new NullProgressMonitor() );
        assertEquals( "OK", status.getMessage() );
        project.refreshLocal( IResource.DEPTH_INFINITE, null );
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
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2-ce-ga4/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2-ce-ga4-20150416163831865.zip" );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    public String getRuntimeVersion()
    {
        return "6.2.3";
    }

    public IServer getServer() throws Exception
    {
        if( server == null )
        {
            IServer[] servers = ServerCore.getServers();
            if( servers.length != 0 )
                server = servers[0];
            setupServer();
        }
        return server;
    }

    protected File getServerLogFile() throws Exception
    {

        // liferay.{date}.log file in sync with server console output
        IPath path = runtime.getLocation().removeLastSegments( 1 ).append( "logs" );
        final String suffix = new SimpleDateFormat( "yyyy-MM-dd" ).format( Calendar.getInstance().getTime() );
        if( path.toFile().exists() )
            return new File( path.append( "liferay." + suffix + ".log" ).toString() );
        else
            return null;
    }
    
    @SuppressWarnings( "restriction" )
    protected void publishToServer( IProject project )
    {
        ServerBehaviourDelegate delegate = (ServerBehaviourDelegate) server.loadAdapter( ServerBehaviourDelegate.class, null );

        Module[] moduleTree = { new Module( null, project.getName(), project.getName(), "jst.web", "3.0", project ) };

        LiferayPublishHelper.prePublishModule( delegate, 1, 1, moduleTree, null, null );
    }

    @Before
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        assertNotNull(
            "Expected System.getProperty(\"liferay.bundles.dir\") to not be null",
            System.getProperty( "liferay.bundles.dir" ) );

        assertNotNull( "Expected liferayBundlesDir to not be null", liferayBundlesDir );

        assertEquals(
            "Expected liferayBundlesPath to exist: " + getLiferayBundlesPath().toOSString(), true,
            getLiferayBundlesPath().toFile().exists() );

        extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        runtime = ServerCore.findRuntime( runtimeName );

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

        final IPath portalBundleFolder = runtime.getLocation().removeLastSegments( 1 );

        final IPath deployPath = portalBundleFolder.append( "deploy" );

        final File deployFolder = deployPath.toFile();

        if( !deployFolder.exists() )
        {
            deployFolder.mkdir();
        }

        assertEquals( "Expected the deploy folder to exist:" + deployPath.toOSString(), true, deployFolder.exists() );
    }

    protected void setupServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        if( runtime == null )
        {
            setupRuntime();
        }
        final IServerWorkingCopy serverWC = createServerForRuntime( "6.2.0", runtime );

        ServerDelegate delegate = (ServerDelegate) serverWC.loadAdapter( ServerDelegate.class, null );
        delegate.importRuntimeConfiguration( serverWC.getRuntime(), null );
        server = serverWC.save( true, npm );

        assertNotNull( server );
    }

    protected boolean shouldSkipBundleTests()
    {
        return "true".equals( skipBundleTests );
    }

    protected boolean shouldSkipServerTests()
    {
        return "true".equals( skipServerTests );
    }

    public void startServer() throws Exception
    {
        server = getServer();
        if( server.getServerState() == IServer.STATE_STARTED )
        {
            return;
        }

        copyFileToServer( server, "", "files", "portal-setup-wizard.properties" );

        server.start( "run", new NullProgressMonitor() );
        long timeoutExpiredMs = System.currentTimeMillis() + 120000;
        while( true )
        {
            if( server.getServerState() == IServer.STATE_STARTED )
            {
                break;
            }
            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                break;
            }
        }
    }
}
