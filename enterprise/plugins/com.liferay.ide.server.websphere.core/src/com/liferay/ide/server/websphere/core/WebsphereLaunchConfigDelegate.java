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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.portal.PortalServerLaunchConfigDelegate;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereLaunchConfigDelegate extends PortalServerLaunchConfigDelegate
{

    public static final String SERVER_ID = "server-id";

    private IProcess createTerminateableStreamsProxyProcess(
        IServer server, WebsphereServer websphereServer, final WebsphereServerBehavior websphereServerBehaviour,
        ILaunch launch, boolean isDebug )
    {
        if( ( server == null ) || ( websphereServer == null ) || ( websphereServerBehaviour == null ) ||
            ( launch == null ) )
        {
            return null;
        }

        ITerminateableStreamsProxy streamsProxy =
            new WebsphereServerLogFileStreamsProxy( websphereServer, websphereServerBehaviour, launch );

        IProcess retvalProcess  = websphereServerBehaviour.getProcess();

        if ( retvalProcess == null )
        {
            retvalProcess = new WebsphereMonitorProcess( server, websphereServerBehaviour, launch, streamsProxy );

            retvalProcess.setAttribute( IProcess.ATTR_PROCESS_TYPE, "java" );

            retvalProcess.setAttribute( IProcess.ATTR_PROCESS_LABEL, server.getName() );

            launch.addProcess( retvalProcess );
        }
        else
        {
            launch.addProcess( retvalProcess );
        }

        return retvalProcess;
    }

    @Override
    protected void launchServer(
        final IServer server, final ILaunchConfiguration config, final String mode, final ILaunch launch,
        final IProgressMonitor monitor ) throws CoreException
    {
        final IVMInstall vm = verifyVMInstall( config );

        final IVMRunner runner =
            vm.getVMRunner( mode ) != null ? vm.getVMRunner( mode ) : vm.getVMRunner( ILaunchManager.RUN_MODE );

        final File workingDir = verifyWorkingDirectory( config );
        // final File workingDir = server.getRuntime().getLocation().toFile();
        final String workingDirPath = workingDir != null ? workingDir.getAbsolutePath() : null;

        final String progArgs = getProgramArguments( config );
        final String vmArgs = getVMArguments( config );
        final String[] envp = getEnvironment( config );

        final ExecutionArguments execArgs = new ExecutionArguments( vmArgs, progArgs );

        final Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap( config );

        final WebsphereServerBehavior portalServer =
            (WebsphereServerBehavior) server.loadAdapter( WebsphereServerBehavior.class, monitor );

        final String classToLaunch = portalServer.getClassToLaunch();
        final String[] classpath = getClasspath( config );

        final VMRunnerConfiguration runConfig = new VMRunnerConfiguration( classToLaunch, classpath );
        runConfig.setProgramArguments( execArgs.getProgramArgumentsArray() );
        runConfig.setVMArguments( execArgs.getVMArgumentsArray() );
        runConfig.setWorkingDirectory( workingDirPath );
        runConfig.setEnvironment( envp );
        runConfig.setVMSpecificAttributesMap( vmAttributesMap );


        final String[] bootpath = getBootpath( config );

        if( !CoreUtil.isNullOrEmpty( bootpath ) )
        {
            runConfig.setBootClassPath( bootpath );
        }

        WebsphereServer websphereServer = (WebsphereServer) server.loadAdapter( WebsphereServer.class, monitor );
        WebsphereServerBehavior websphereServerBehavior =
            (WebsphereServerBehavior) server.loadAdapter( WebsphereServerBehavior.class, monitor );

        IProcess streamProxyProcess = createTerminateableStreamsProxyProcess(
            server, websphereServer, websphereServerBehavior, launch, "debug".equals( mode ) );

        if( websphereServerBehavior.getProcess() == null )
        {
            websphereServerBehavior.setProcess( streamProxyProcess );
        }

        portalServer.launchServer( launch, mode, monitor );

        try
        {
            runner.run( runConfig, launch, monitor );
            portalServer.addProcessListener( launch.getProcesses()[1] );
        }
        catch( Exception e )
        {
            portalServer.cleanup();
        }
    }
}
