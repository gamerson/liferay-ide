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

import aQute.remote.api.Agent;

import com.liferay.ide.core.LiferayRuntimeClasspathEntry;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServerBehavior;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.websphere.util.WebsphereUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.core.util.SocketUtil;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereServerBehavior extends PortalServerBehavior implements IWebsphereServerBehavior, IStreamListener
{

    private IWebsphereServer websphereServer;
    private IWebsphereRuntime websphereRuntime;
    private transient IProcess process;
    protected transient ServerStateStartThread startedThread = null;
    protected transient ServerStateStopThread stopedThread = null;
    protected transient ServerStateRestartThread restartThread = null;
    protected boolean serverRestarting = false;

    private static final String[] JMX_EXCLUDE_ARGS = new String []
    {
        "-Djavax.management.builder.initial=",
        "-Dcom.sun.management.jmxremote",
        "-Dcom.sun.management.jmxremote.port=",
        "-Dcom.sun.management.jmxremote.ssl=",
        "-Dcom.sun.management.jmxremote.authenticate="
    };

    public WebsphereServerBehavior()
    {
        super();
    }

    public void addOutputStreamListener( IProcess curProcess )
    {
        curProcess.getStreamsProxy().getOutputStreamMonitor().addListener( this );
    }

    @Override
    public void cleanup()
    {
        if( startedThread != null )
        {
            startedThread.stop();
            startedThread = null;
        }

        if( stopedThread != null )
        {
            stopedThread.stop();
            stopedThread = null;
        }

        if( restartThread != null && ( isServerRestarting() == false ) )
        {
            restartThread.stop();
            restartThread = null;
        }

        setProcess( null );
        super.cleanup();
    }

    public IPath getAppServerDir()
    {
        return getWebsphereServer().getLiferayHome();    
    }

    @Override
    protected IPath getAutoDeployPath()
    {
        return getWebsphereServer().getAutoDeployPath();
    }

    @Override
    protected String[] getVMExcludeArgs()
    {
        return JMX_EXCLUDE_ARGS;
    }

    @Override
    protected IPath getModulePath()
    {
        return getWebsphereServer().getModulesPath();
    }

    @Override
    public BundleSupervisor createBundleSupervisor() throws Exception
    {
        return ServerUtil.createBundleSupervisor(
            Integer.valueOf( getWebsphereServer().getWebsphereJMXPort() ), getServer() );
    }

    @Override
    public void dispose()
    {
        if( this.process != null )
        {
            setProcess( null );
        }
    }

    @Override
    public String getClassToLaunch()
    {
        return "com.ibm.wsspi.bootstrap.WSPreLauncher";
    }

    @Override
    protected IPath getLiferayHome()
    {
        return getWebsphereServer().getLiferayHome();
    }

    @Override
    protected File getPortalImplFile()
    {
        IPath profileLocation = new Path( getWebsphereServer().getWebsphereProfileLocation() );
        final String websphereCellName = getWebsphereServer().getWebsphereCellName();
        final String websphereNodeName = getWebsphereServer().getWebsphereNodeName();

        String[] liferayAppNames = WebsphereUtil.extractLiferayAppFolder(
            profileLocation, websphereCellName, websphereNodeName );

        if( liferayAppNames.length> 0 )
        {
            IPath liferayApplicationPath = profileLocation.append( "installedApps" ).append( websphereCellName ).append(
                liferayAppNames[0] + ".ear" ).append( liferayAppNames[1] );

            if( liferayApplicationPath.toFile().exists() )
            {
                return liferayApplicationPath.append( "WEB-INF/lib/portal-impl.jar" ).toFile();
            }
        }

        return null;
    }

    @Override
    protected WebsphereServer getPortalServer()
    {
        WebsphereServer retval = null;

        if( getServer() != null )
        {
            retval = (WebsphereServer) getServer().loadAdapter( WebsphereServer.class, null );
        }

        return retval;
    }

    public IProcess getProcess()
    {
        return this.process;
    }

    private IRuntime getRuntime()
    {
        return getServer().getRuntime();
    }

    @Override
    protected String[] getRuntimeStartProgArgs()
    {
        List<String> programeArguments = new ArrayList<String>();
        programeArguments.add( "\"" + "com.ibm.wsspi.bootstrap.WSPreLauncher" + "\"" );
        programeArguments.add( "\"" + "-nosplash" + "\"" );
        programeArguments.add( "\"" + "-application" + "\"" );
        programeArguments.add( "\"" + "com.ibm.ws.bootstrap.WSLauncher" + "\"" );
        programeArguments.add( "\"" + "com.ibm.ws.runtime.WsServer" + "\"" );
        IPath profileLocation = new Path( getWebsphereServer().getWebsphereProfileLocation() );
        programeArguments.add( "\"" + profileLocation.append( "config" ) + "\"" );

        programeArguments.add( "\"" + getWebsphereServer().getWebsphereCellName() + "\"" );
        programeArguments.add( "\"" + getWebsphereServer().getWebsphereNodeName() + "\"" );
        programeArguments.add( "\"" + getWebsphereServer().getWebsphereServerName() + "\"" );

        return programeArguments.toArray( new String[programeArguments.size()] );
    }

    private String[] getRuntimeStartVMArgs()
    {
        List<String> vmArguments = new ArrayList<String>();

        IPath runtimeLocation = getWebsphereRuntime().getRuntimeLocation();
        vmArguments.add( "\"" + "-Dconsole.encoding=" + System.getProperty( "file.encoding" ) + "\"" );
        vmArguments.add( "\"" + "-Declipse.security" + "\"" );
        vmArguments.add( "\"" + "-Dosgi.install.area=" + runtimeLocation + "\"" );

        IPath profileLocation = new Path( getWebsphereServer().getWebsphereProfileLocation() );
        String websphereServerName = getWebsphereServer().getWebsphereServerName();
        vmArguments.add(
            "\"" + "-Dosgi.configuration.area=" +
                profileLocation.append( "servers" ).append( websphereServerName ).append( "configuration" ) + "\"" );
        vmArguments.add( "\"" + "-Dosgi.framework.extensions=com.ibm.cds,com.ibm.ws.eclipse.adaptors" + "\"" );
        vmArguments.add( "\"" + "-Xshareclasses:name=webspherev85_1.8_64,nonFatal" + "\"" );
        vmArguments.add( "\"" + "-Dsun.reflect.inflationThreshold=250" + "\"" );
        vmArguments.add( "\"" + "-Dcom.ibm.xtq.processor.overrideSecureProcessing=true" + "\"" );
        vmArguments.add(
            "\"" + "-Djava.security.properties=" + runtimeLocation.append( "properties" ).append( "java.security" ) +
                "\"" );

        IVMInstall vmInstall = getWebsphereRuntime().getVMInstall();
        IPath vmLocation = new Path( vmInstall.getInstallLocation().getAbsolutePath() );
        vmArguments.add(
            "\"" + "-Xbootclasspath/p:" + vmLocation.append( "jre" ).append( "lib" ).append( "ibmorb.jar" ) + "\"" );

        List<IPath> classpathList = new ArrayList<IPath>();
        classpathList.add( profileLocation.append( "properties" ) );
        classpathList.add( runtimeLocation.append( "properties" ) );
        classpathList.add( runtimeLocation.append( "lib" ).append( "startup.jar" ) );
        classpathList.add( runtimeLocation.append( "lib" ).append( "bootstrap.jar" ) );
        classpathList.add( runtimeLocation.append( "lib" ).append( "jsf-nls.jar" ) );
        classpathList.add( runtimeLocation.append( "lib" ).append( "lmproxy.jar" ) );
        classpathList.add( runtimeLocation.append( "lib" ).append( "urlprotocols.jar" ) );
        classpathList.add( runtimeLocation.append( "deploytool" ).append( "itp" ).append( "batchboot.jar" ) );
        classpathList.add( runtimeLocation.append( "deploytool" ).append( "itp" ).append( "batch2.jar" ) );

        StringBuilder classpathBuilder = new StringBuilder();
        for( int i = 0; i < classpathList.size(); i++ )
        {
            IPath classpath = classpathList.get( i );
            classpathBuilder.append( classpath ).append( ";" );
        }
        classpathBuilder.append( vmLocation.append( "lib" ).append( "tools.jar" ) );

        vmArguments.add( "-classpath" + " " + "\"" + classpathBuilder.toString() + "\"" );
        vmArguments.add( "\"" + "-Dibm.websphere.internalClassAccessMode=allow" + "\"" );

        StringBuilder wsExtDirs = new StringBuilder();
        wsExtDirs.append( vmLocation.append( "lib" ).append( ";" ) );
        wsExtDirs.append( profileLocation.append( "classes" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "classes" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "lib" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "installedChannels" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "lib" ).append( "ext" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "web" ).append( "help" ).append( ";" ) );
        wsExtDirs.append(
            runtimeLocation.append( "deploytool" ).append( "itp" ).append( "plugins" ).append(
                "com.ibm.etools.ejbdeploy" ).append( "runtime" ).append( ";" ) );

        vmArguments.add( "\"" + "-Dws.ext.dirs=" + wsExtDirs.toString() + "\"" );

        vmArguments.add( "\"" + "-Dderby.system.home=" + runtimeLocation.append( "derby" ) + "\"" );
        vmArguments.add( "\"" + "-Dcom.ibm.itp.location=" + runtimeLocation.append( "bin" ) + "\"" );
        vmArguments.add( "\"" + "-Djava.util.logging.configureByServer=true" + "\"" );
        vmArguments.add( "\"" + "-Duser.install.root=" + profileLocation + "\"" );
        vmArguments.add(
            "\"" + "-Djava.ext.dirs= " + runtimeLocation.append( "tivoli" ).append( "tam" ) + ";" +
                vmLocation.append( "jre" ).append( "lib" ).append( "ext" ) + "\"" );
        vmArguments.add(
            "\"" + "-Djavax.management.builder.initial=com.ibm.ws.management.PlatformMBeanServerBuilder" + "\"" );
        vmArguments.add( "\"" + "-Dpython.cachedir= " + profileLocation.append( "temp" ).append( "cechedir" ) + "\"" );
        vmArguments.add( "\"" + "-Dwas.install.root=" + runtimeLocation + "\"" );
        vmArguments.add( "\"" + "-Djava.util.logging.manager=com.ibm.ws.bootstrap.WsLogManager" + "\"" );
        vmArguments.add( "\"" + "-Dserver.root=" + profileLocation + "\"" );
        vmArguments.add( "\"" + "-Dcom.ibm.security.jgss.debug=off" + "\"" );
        vmArguments.add( "\"" + "-Dcom.ibm.security.krb5.Krb5Debug=off" + "\"" );
        // vmArguments.add( "\"" + "-Dfile.encoding=UTF8" + "\"" );
        vmArguments.add( "\"" + "-Djava.net.preferIPv4Stack=true" + "\"" );
        vmArguments.add( "\"" + "-Duser.timezone=GMT" + "\"" );

        StringBuilder javaLibraryPathBuilder = new StringBuilder();

        if( CoreUtil.isWindows() )
        {
            String arch = Platform.getOSArch();
            if( arch.equals( "x86_64" ) )
            {
                javaLibraryPathBuilder.append(
                    runtimeLocation.append( "lib" ).append( "native" ).append( "win" ).append( "x86_64" ) ).append( ";" );
            }
            else
            {
                javaLibraryPathBuilder.append(
                    runtimeLocation.append( "lib" ).append( "native" ).append( "win" ).append( "x86_32" ) ).append( ";" );
            }
        }

        javaLibraryPathBuilder.append( System.getenv( "Path" ) );

        javaLibraryPathBuilder.append( profileLocation.append( "liferay" ).append( ";" ) );
        javaLibraryPathBuilder.append( vmLocation.append( "jre" ).append( "bin" ).append( "compressedrefs" ) ).append( ";" );
        javaLibraryPathBuilder.append( runtimeLocation.append( "bin" ) ).append( ";" );
        javaLibraryPathBuilder.append( vmLocation.append( "bin" ) ).append( ";" );
        javaLibraryPathBuilder.append( vmLocation.append( "jre" ).append( "bin" ) ).append( ";" ).append( "." ).append( ";" );

        vmArguments.add( "\"" + "-Djava.library.path=" + javaLibraryPathBuilder.toString() + "\"" );

        vmArguments.add( "\"" + "-Dcom.ibm.ws.management.event.pull_notification_timeout=120000" + "\"" );

        StringBuilder javaEndorsedDirs = new StringBuilder();
        javaEndorsedDirs.append( runtimeLocation.append( "endorsed_apis" ).append( ";" ) );
        javaEndorsedDirs.append( vmLocation.append( "jre" ).append( "lib" ).append( "endorsed" ) );
        vmArguments.add( "\"" + "-Djava.endorsed.dirs=" + javaEndorsedDirs.toString() + "\"" );

        vmArguments.add(
            "\"" + "-Djava.security.auth.login.config=" +
                profileLocation.append( "properties" ).append( "wsjaas.conf" ) + "\"" );
        vmArguments.add(
            "\"" + "-Djava.security.policy=" + profileLocation.append( "properties" ).append( "server.policy" ) + "\"" );

        vmArguments.add( "\"" + "-Xquickstart" + "\"" );

        vmArguments.add( "-Djavax.management.builder.initial=" );
        vmArguments.add( "-Dcom.sun.management.jmxremote" );
        vmArguments.add( "-Dcom.sun.management.jmxremote.port=" + getWebsphereServer().getWebsphereJMXPort() );
        vmArguments.add( "-Dcom.sun.management.jmxremote.ssl=false" );
        vmArguments.add( "-Dcom.sun.management.jmxremote.authenticate=false" );

        vmArguments.add( "\"" + "-Ddefault.liferay.home=" + profileLocation.append( "liferay" ) + "\"" );
        vmArguments.add( "\"" + "-Duser.dir=" + profileLocation.toOSString() + "\"" );

        return vmArguments.toArray( new String[vmArguments.size()] );
    }

    @Override
    protected String[] getRuntimeStartVMArguments()
    {
        final List<String> retval = new ArrayList<String>();

        Collections.addAll( retval, getPortalServer().getMemoryArgs() );

        Collections.addAll( retval, getRuntimeStartVMArgs() );

        int agentPort = getServer().getAttribute( AGENT_PORT, Agent.DEFAULT_PORT );

        retval.add( "-D" + Agent.AGENT_SERVER_PORT_KEY + "=" + agentPort );

        return retval.toArray( new String[0] );
    }

    @Override
    protected String[] getRuntimeStopProgArgs()
    {
        List<String> programeArguments = new ArrayList<String>();
        programeArguments.add( "\"" + "com.ibm.wsspi.bootstrap.WSPreLauncher" + "\"" );
        programeArguments.add( "\"" + "-nosplash" + "\"" );
        programeArguments.add( "\"" + "-application" + "\"" );
        programeArguments.add( "\"" + "com.ibm.ws.bootstrap.WSLauncher" + "\"" );
        programeArguments.add( "\"" + "com.ibm.ws.admin.services.WsServerStop" + "\"" );
        IPath profileLocation = new Path( getWebsphereServer().getWebsphereProfileLocation() );
        programeArguments.add( "\"" + profileLocation.append( "config" ) + "\"" );

        programeArguments.add( "\"" + getWebsphereServer().getWebsphereCellName() + "\"" );
        programeArguments.add( "\"" + getWebsphereServer().getWebsphereNodeName() + "\"" );
        programeArguments.add( "\"" + getWebsphereServer().getWebsphereServerName() + "\"" );

        return programeArguments.toArray( new String[programeArguments.size()] );
    }

    @Override
    protected String[] getRuntimeStopVMArguments()
    {
        List<String> vmArguments = new ArrayList<String>();
        IPath profileLocation = new Path( getWebsphereServer().getWebsphereProfileLocation() );
        IPath runtimeLocation = getWebsphereRuntime().getRuntimeLocation();

        IVMInstall vmInstall = getWebsphereRuntime().getVMInstall();
        IPath vmLocation = new Path( vmInstall.getInstallLocation().getAbsolutePath() );

        StringBuilder javaEndorsedDirs = new StringBuilder();
        javaEndorsedDirs.append( runtimeLocation.append( "endorsed_apis" ).append( ";" ) );
        javaEndorsedDirs.append( vmLocation.append( "jre" ).append( "lib" ).append( "endorsed" ) );
        vmArguments.add( "\"" + "-Djava.endorsed.dirs=" + javaEndorsedDirs.toString() + "\"" );

        StringBuilder wsExtDirs = new StringBuilder();
        wsExtDirs.append( vmLocation.append( "lib" ).append( ";" ) );
        wsExtDirs.append( profileLocation.append( "classes" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "classes" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "lib" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "installedChannels" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "lib" ).append( "ext" ).append( ";" ) );
        wsExtDirs.append( runtimeLocation.append( "web" ).append( "help" ).append( ";" ) );
        wsExtDirs.append(
            runtimeLocation.append( "deploytool" ).append( "itp" ).append( "plugins" ).append(
                "com.ibm.etools.ejbdeploy" ).append( "runtime" ).append( ";" ) );

        vmArguments.add( "\"" + "-Dcom.ibm.ffdc.log=" + profileLocation.append( "logs" ).append( "ffdc" ) + "\"" );

        vmArguments.add( "\"" + "-Dws.ext.dirs=" + wsExtDirs.toString() + "\"" );

        vmArguments.add(
            "\"" + "-Dcom.ibm.SOAP.ConfigURL=" + profileLocation.append( "properties" ).append( "soap.client.props" ) +
                "\"" );
        vmArguments.add(
            "\"" + "-Djava.security.auth.login.config=" +
                profileLocation.append( "properties" ).append( "wsjaas_client.conf" ) + "\"" );
        vmArguments.add(
            "\"" + "-Dcom.ibm.CORBA.ConfigURL=" + profileLocation.append( "properties" ).append( "sas.client.props" ) +
                "\"" );
        vmArguments.add(
            "\"" + "-Dcom.ibm.SSL.ConfigURL=" + profileLocation.append( "properties" ).append( "ssl.client.props" ) +
                "\"" );
        vmArguments.add( "\"" + "-Duser.install.root=" + profileLocation.toOSString() + "\"" );
        vmArguments.add( "\"" + "-Dwas.install.root=" + runtimeLocation.toOSString() + "\"" );
        return vmArguments.toArray( new String[vmArguments.size()] );
    }

    private IWebsphereRuntime getWebsphereRuntime()
    {
        if( websphereRuntime == null )
        {
            websphereRuntime = (IWebsphereRuntime) getRuntime().loadAdapter( IWebsphereRuntime.class, null );
        }
        return websphereRuntime;
    }

    private IWebsphereServer getWebsphereServer()
    {
        if( websphereServer == null )
        {
            websphereServer = (IWebsphereServer) getServer().loadAdapter( IWebsphereServer.class, null );
        }
        return websphereServer;
    }

    public boolean isLocalHost()
    {
        return SocketUtil.isLocalhost( getWebsphereServer().getHost() );
    }

    public boolean isServerRestarting()
    {
        return serverRestarting;
    }

    @Override
    public void launchServer( ILaunch launch, String mode, IProgressMonitor monitor ) throws CoreException
    {
        if( "true".equals( launch.getLaunchConfiguration().getAttribute( ATTR_STOP, "false" ) ) )
        {
            return;
        }

        final IStatus status = getPortalRuntime().validate();

        if( status != null && status.getSeverity() == IStatus.ERROR )
            throw new CoreException( status );

        setServerRestartState( false );
        setServerState( IServer.STATE_STARTING );
        setMode( mode );

        try
        {
            startedThread = new ServerStateStartThread( getServer(), this );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Can't ping for portal startup." );
        }
    }

    private Map<String, String> mergeEnviroument( Map<String, String> oldEnv )
    {
        Map<String, String> newEnv = new HashMap<String, String>();

        if( oldEnv != null )
        {
            newEnv.putAll( oldEnv );
        }
        newEnv.put( "USER_INSTALL_ROOT", websphereServer.getWebsphereProfileLocation() );
        newEnv.put( "WAS_HOME", websphereRuntime.getRuntimeLocation().toOSString() );

        return newEnv;
    }

    public void removeOutputStreamListener( IProcess curProcess )
    {
        curProcess.getStreamsProxy().getOutputStreamMonitor().removeListener( this );
    }

    @Override
    public void restart( final String launchMode ) throws CoreException
    {
        setServerRestarting( true );
        getServer().stop( false, new IOperationListener()
        {

            @Override
            public void done( IStatus result )
            {
                try
                {
                    restartThread =
                        new ServerStateRestartThread( getServer(), WebsphereServerBehavior.this, launchMode );
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( "Can't restart websphere." );
                }
            }
        } );
    }

    protected void setProcess( IProcess newProcess )
    {
        if( ( this.process != null ) && ( !( this.process.isTerminated() ) ) )
        {
            try
            {
                removeOutputStreamListener( this.process );
                this.process.terminate();
            }
            catch( Exception e )
            {
                WebsphereCore.logError( e );
            }
        }

        this.process = newProcess;
        if( ( this.process != null ) && ( !( this.process.isTerminated() ) ) )
        {
            addOutputStreamListener( this.process );
        }
    }

    public void setServerRestarting( boolean serverRestarting )
    {
        this.serverRestarting = serverRestarting;
    }

    public synchronized void setupAgentAndJMX()
    {
        setupAgent();
        setupAriesJmxBundles();
    }

    @Override
    public void setupLaunchConfiguration( ILaunchConfigurationWorkingCopy launch, IProgressMonitor monitor )
        throws CoreException
    {
        final String existingProgArgs = launch.getAttribute( ATTR_PROGRAM_ARGUMENTS, (String) null );
        launch.setAttribute(
            ATTR_PROGRAM_ARGUMENTS, mergeArguments( existingProgArgs, getRuntimeStartProgArgs(), null, true ) );

        final String existingVMArgs = launch.getAttribute( ATTR_VM_ARGUMENTS, (String) null );

        final String[] configVMArgs = getRuntimeStartVMArguments();
        launch.setAttribute( ATTR_VM_ARGUMENTS, mergeArguments( existingVMArgs, configVMArgs, null, false ) );

        final PortalRuntime portalRuntime = getPortalRuntime();
        final IVMInstall vmInstall = portalRuntime.getVMInstall();

        if( vmInstall != null )
        {
            launch.setAttribute(
                ATTR_JRE_CONTAINER_PATH, JavaRuntime.newJREContainerPath( vmInstall ).toPortableString() );
        }

        Map<String, String> oldEnv =
            launch.getAttribute( ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map<String, String>) null );

        Map<String, String> mergeEnviroument = mergeEnviroument( oldEnv );
        launch.setAttribute( ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, mergeEnviroument );

        final IRuntimeClasspathEntry[] orgClasspath = JavaRuntime.computeUnresolvedRuntimeClasspath( launch );
        final int orgClasspathSize = orgClasspath.length;

        final List<IRuntimeClasspathEntry> oldCp = new ArrayList<>( orgClasspathSize );
        Collections.addAll( oldCp, orgClasspath );

        if( vmInstall != null )
        {
            try
            {
                final String typeId = vmInstall.getVMInstallType().getId();
                final IRuntimeClasspathEntry newJRECp = JavaRuntime.newRuntimeContainerClasspathEntry(
                    new Path( JavaRuntime.JRE_CONTAINER ).append( typeId ).append( vmInstall.getName() ),
                    IRuntimeClasspathEntry.BOOTSTRAP_CLASSES );
                replaceJREConatiner( oldCp, newJRECp );
            }
            catch( Exception e )
            {
            }
        }

        final List<String> cp = new ArrayList<>();

        for( IRuntimeClasspathEntry entry : oldCp )
        {
            try
            {
                if( entry.getClasspathEntry().getEntryKind() != IClasspathEntry.CPE_CONTAINER )
                {
                    entry = new LiferayRuntimeClasspathEntry( entry.getClasspathEntry() );
                }
                cp.add( entry.getMemento() );
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( "Could not resolve cp entry " + entry, e );
            }
        }

        launch.setAttribute( ATTR_CLASSPATH, cp );
        launch.setAttribute( ATTR_DEFAULT_CLASSPATH, false );

    }

    @Override
    public void stop( boolean force )
    {
        super.stop( force );

        try
        {
            stopedThread = new ServerStateStopThread( getServer(), this );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Can't ping for portal startup." );
        }
    }

    @Override
    public void streamAppended( String serverLogText, IStreamMonitor monitor )
    {
    }

    public void terminateWebsphereProcess()
    {
        final int websphereProcessPID = WebsphereUtil.getWebsphereProcessPID( getWebsphereServer() );
        WebsphereUtil.killWebsphereProcess( websphereProcessPID );
    }
}
