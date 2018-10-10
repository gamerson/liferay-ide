/**
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
 */

package com.liferay.ide.server.core.portal;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.ServerStopArgs;
import com.liferay.blade.cli.command.ServerStopCommand;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PortalServerBehavior
	extends ServerBehaviourDelegate implements ILiferayServerBehavior, IJavaLaunchConfigurationConstants {

	public static final String ATTR_STOP = "stop-server";

	public PortalServerBehavior() {
		_watchProjects = new LinkedHashSet<>();
	}

	public void addProcessListener(IProcess newProcess) {
		if ((_processListener != null) || (newProcess == null)) {
			return;
		}

		_processListener = new IDebugEventSetListener() {

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					for (DebugEvent event : events) {
						if ((newProcess != null) && newProcess.equals(event.getSource()) &&
							(event.getKind() == DebugEvent.TERMINATE)) {

							cleanup();
						}
					}
				}
			}

		};

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.addDebugEventListener(_processListener);
	}

	public void addWatchProject(IProject project) {
		if (IServer.STATE_STARTED == getServer().getServerState()) {
			_watchProjects.add(project);

			try {
				_refreshSourceLookup();
			}
			catch (CoreException ce) {
				LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
			}
		}
	}

	@Override
	public boolean canRestartModule(IModule[] modules) {
		for (IModule module : modules) {
			IProject project = module.getProject();

			if (project == null) {
				return false;
			}

			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if ((bundleProject != null) && !bundleProject.isFragmentBundle()) {
				return true;
			}
		}

		return false;
	}

	public void cleanup() {
		if (startedThread != null) {
			startedThread.stop();

			startedThread = null;
		}

		if (stopedThread != null) {
			stopedThread.stop();

			stopedThread = null;
		}

		if ((restartThread != null) && (isServerRestarting() == false)) {
			restartThread.stop();

			restartThread = null;
		}

		setProcess(null);

		if (_processListener != null) {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			debugPlugin.removeDebugEventListener(_processListener);

			_processListener = null;
		}

		setServerState(IServer.STATE_STOPPED);

		_watchProjects.clear();
	}

	public GogoBundleDeployer createBundleDeployer() throws Exception {
		return ServerUtil.createBundleDeployer(getPortalRuntime(), getServer());
	}

	@Override
	public void dispose() {
		if (_process != null) {
			setProcess(null);
		}
	}

	public String getClassToLaunch() {
		PortalBundle portalBundle = getPortalRuntime().getPortalBundle();

		return portalBundle.getMainClass();
	}

	@Override
	public IPath getDeployedPath(IModule[] module) {
		return null;
	}

	public IAdaptable getInfo() {
		return _info;
	}

	public PortalRuntime getPortalRuntime() {
		PortalRuntime retval = null;

		IServer s = getServer();

		IRuntime runtime = s.getRuntime();

		if (runtime != null) {
			retval = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);
		}

		return retval;
	}

	public PortalServer getPortalServer() {
		PortalServer retval = null;

		IServer s = getServer();

		if (s != null) {
			retval = (PortalServer)s.loadAdapter(PortalServer.class, null);
		}

		return retval;
	}

	public IProcess getProcess() {
		return _process;
	}

	public Set<IProject> getWatchProjects() {
		return _watchProjects;
	}

	public boolean isServerRestarting() {
		return serverRestarting;
	}

	public void launchServer(ILaunch launch, String mode, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();

		if ("true".equals(launchConfiguration.getAttribute(ATTR_STOP, "false"))) {
			return;
		}

		IStatus status = getPortalRuntime().validate();

		if ((status != null) && (status.getSeverity() == IStatus.ERROR)) {
			throw new CoreException(status);
		}

		setServerRestartState(false);
		setServerState(IServer.STATE_STARTING);
		setMode(mode);

		try {
			startedThread = new BladeServerStateStartThread(getServer(), this);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Can not ping for portal startup.");
		}
	}

	@Override
	public synchronized void publish(int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info)
		throws CoreException {

		// save info

		_info = info;

		super.publish(kind, modules, monitor, info);

		_info = null;
	}

	@Override
	public void redeployModule(IModule[] module) throws CoreException {
		setModulePublishState(module, IServer.PUBLISH_STATE_FULL);

		IAdaptable info = new IAdaptable() {

			@Override
			@SuppressWarnings({"unchecked", "rawtypes"})
			public Object getAdapter(Class adapter) {
				if (String.class.equals(adapter)) {
					return "user";
				}
				else if (IModule.class.equals(adapter)) {
					return module[0];
				}

				return null;
			}

		};

		List<IModule[]> modules = new ArrayList<>();

		modules.add(module);

		publish(IServer.PUBLISH_FULL, modules, null, info);
	}

	public void removeWatchProject(IProject project) {
		if (IServer.STATE_STARTED == getServer().getServerState()) {
			_watchProjects.remove(project);

			try {
				_refreshSourceLookup();
			}
			catch (CoreException ce) {
				LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
			}
		}
	}

	@Override
	public void restart(final String launchMode) throws CoreException {
		setServerRestarting(true);
		getServer().stop(
			false,
			new IOperationListener() {

				@Override
				public void done(IStatus result) {
					try {
						restartThread = new BladeServeStateRestartThread(
							getServer(), PortalServerBehavior.this, launchMode);
					}
					catch (Exception e) {
						LiferayServerCore.logError("Can not restart Blade Server.");
					}
				}

			});
	}

	public void setModulePublishState2(IModule[] module, int state) {
		super.setModulePublishState(module, state);
	}

	public void setModuleState2(IModule[] modules, int state) {
		super.setModuleState(modules, state);
	}

	public void setServerRestarting(boolean serverRestarting) {
		this.serverRestarting = serverRestarting;
	}

	public void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	public void setServerStoped() {
		setServerState(IServer.STATE_STOPPED);
	}

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
		throws CoreException {

		super.setupLaunchConfiguration(workingCopy, monitor);

		int port = SocketUtil.findFreePort();

		workingCopy.setAttribute("hostname", getServer().getHost());
		workingCopy.setAttribute("port", String.valueOf(port));
	}

	@Override
	public void startModule(IModule[] modules, IProgressMonitor monitor) throws CoreException {
		_startOrStopModules(modules, "start");
	}

	@Override
	public void stop(boolean force) {
		if (force) {
			try {
				_executStopCommand();

				if (stopedThread == null) {
					stopedThread = new BladeServerStateStopThread(getServer(), this);
				}

				terminate();
				setServerState(IServer.STATE_STOPPED);
			}
			catch (Exception e) {
			}

			return;
		}

		int state = getServer().getServerState();

		// If stopped or stopping, no need to run stop command again

		if ((state == IServer.STATE_STOPPED) || (state == IServer.STATE_STOPPING)) {
			return;
		}
		else if (state == IServer.STATE_STARTING) {
			try {
				_executStopCommand();

				if (stopedThread == null) {
					stopedThread = new BladeServerStateStopThread(getServer(), this);
				}

				terminate();
				setServerState(IServer.STATE_STOPPED);
			}
			catch (Exception e) {
			}

			return;
		}

		try {
			if (state != IServer.STATE_STOPPED) {
				setServerState(IServer.STATE_STOPPING);
			}

			if (stopedThread == null) {
				stopedThread = new BladeServerStateStopThread(getServer(), this);
			}

			final ILaunch launch = getServer().getLaunch();

			if (launch == null) {
				terminate();

				return;
			}

			_executStopCommand();

			setServerState(IServer.STATE_STOPPED);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Can not ping for portal startup.");
		}
	}

	@Override
	public void stopModule(IModule[] modules, IProgressMonitor monitor) throws CoreException {
		_startOrStopModules(modules, "stop");
	}

	public void triggerCleanupEvent(Object eventSource) {
		DebugEvent event = new DebugEvent(eventSource, DebugEvent.TERMINATE);

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.fireDebugEventSet(new DebugEvent[] {event});
	}

	protected static String renderCommandLine(String[] commandLine, String separator) {
		if ((commandLine == null) || (commandLine.length < 1)) {
			return "";
		}

		StringBuffer buf = new StringBuffer(commandLine[0]);

		for (int i = 1; i < commandLine.length; i++) {
			buf.append(separator);
			buf.append(commandLine[i]);
		}

		return buf.toString();
	}

	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] modules, IProgressMonitor monitor)
		throws CoreException {

		// publishing is done by PortalPublishTask

		return;
	}

	@Override
	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		setServerPublishState(IServer.PUBLISH_STATE_UNKNOWN);
	}

	protected void setProcess(IProcess newProcess) {
		if ((_process != null) && !_process.isTerminated()) {
			try {
				_process.terminate();
			}
			catch (Exception e) {
				LiferayServerCore.logError(e);
			}
		}

		_process = newProcess;
	}

	protected void terminate() {
		if (getServer().getServerState() == IServer.STATE_STOPPED) {
			return;
		}

		try {
			setServerState(IServer.STATE_STOPPING);

			ILaunch launch = getServer().getLaunch();

			if (launch != null) {
				launch.terminate();

				// cleanup();

			}
		}
		catch (Exception e) {
			LiferayServerCore.logError("Error killing the process", e);
		}
	}

	protected transient BladeServeStateRestartThread restartThread = null;
	protected boolean serverRestarting = false;
	protected transient BladeServerStateStartThread startedThread = null;
	protected transient BladeServerStateStopThread stopedThread = null;

	private void _executStopCommand() throws Exception {
		PortalRuntime portalRuntime = getPortalRuntime();

		IPath location = portalRuntime.getLiferayHome();

		ServerStopArgs args = new ServerStopArgs();
		ServerStopCommand stopCommand = new ServerStopCommand();

		BladeCLI bladeCLI = stopCommand.getBladeCLI();

		stopCommand.setBlade(bladeCLI);
		BaseArgs bladeArgs = bladeCLI.getBladeArgs();

		bladeArgs.setBase(location.toFile());

		stopCommand.setArgs(args);

		stopCommand.execute();
	}

	private void _refreshSourceLookup() throws CoreException {
		ILaunch launch = getServer().getLaunch();
		ILaunchConfiguration launchConfiguration = getServer().getLaunchConfiguration(false, new NullProgressMonitor());

		if (launchConfiguration != null) {
			AbstractSourceLookupDirector abstractSourceLookupDirector =
				(AbstractSourceLookupDirector)launch.getSourceLocator();

			String memento = launchConfiguration.getAttribute(
				ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);

			if (memento != null) {
				abstractSourceLookupDirector.initializeFromMemento(memento);
			}
			else {
				abstractSourceLookupDirector.initializeDefaults(launchConfiguration);
			}
		}
	}

	private void _startOrStopModules(IModule[] modules, String action) {
		for (IModule module : modules) {
			IProject project = module.getProject();

			if (project == null) {
				continue;
			}

			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if (bundleProject != null) {
				try {
					String symbolicName = bundleProject.getSymbolicName();

					GogoBundleDeployer helper = new GogoBundleDeployer();

					long bundleId = helper.getBundleId(symbolicName);

					if (bundleId > 0) {
						if (action.equals("start")) {
							String error = helper.start(bundleId);

							if (error == null) {
								setModuleState(new IModule[] {module}, IServer.STATE_STARTED);
							}
							else {
								LiferayServerCore.logError("Unable to start this bundle");
							}
						}
						else if (action.equals("stop")) {
							String error = helper.stop(bundleId);

							if (error == null) {
								setModuleState(new IModule[] {module}, IServer.STATE_STOPPED);
							}
							else {
								LiferayServerCore.logError("Unable to stop this bundle");
							}
						}
					}
				}
				catch (Exception e) {
					LiferayServerCore.logError("Unable to " + action + " module", e);
				}
			}
		}
	}

	private IAdaptable _info;
	private transient IProcess _process;
	private transient IDebugEventSetListener _processListener;
	private Set<IProject> _watchProjects;

}