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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class PortalServerLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {

	public static final String ID = "com.liferay.ide.server.portal.launch";

	public static final String PREF_ENABLE_ADVANCED_SOURCELOOKUP =
		JDIDebugPlugin.getUniqueIdentifier() + ".enable_advanced_sourcelookup";

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		if (!(ILaunchManager.DEBUG_MODE.equals(mode) && _isAdvancedSourcelookupEnabled())) {
			return null;
		}

		return super.getLaunch(configuration, mode);
	}

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		IServer server = ServerUtil.getServer(config);

		if (server != null) {
			IRuntime runtime = server.getRuntime();

			if (runtime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server runtime is invalid."));
			}

			PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, monitor);

			if (portalRuntime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server portal runtime is invalid."));
			}

			IStatus status = portalRuntime.validate();

			if (!status.isOK()) {
				throw new CoreException(status);
			}

			_launchServer(server, config, mode, launch, monitor);
		}
	}

	private static int _compareJavaVersions(IVMInstall vm, String ver) {
		if (vm instanceof AbstractVMInstall) {
			AbstractVMInstall install = (AbstractVMInstall)vm;

			String vmver = install.getJavaVersion();

			if (vmver == null) {
				return -1;
			}

			// versionToJdkLevel only handles 3 char versions = 1.5, 1.6, 1.7, etc

			if (vmver.length() > 3) {
				vmver = vmver.substring(0, 3);
			}

			return Long.compare(CompilerOptions.versionToJdkLevel(vmver), CompilerOptions.versionToJdkLevel(ver));
		}

		return -1;
	}

	private static String _getJavaagentLocation() {
		return LaunchingPlugin.getFileInPlugin(new Path("lib/javaagent-shaded.jar")).getAbsolutePath();
	}

	private static String _getJavaagentString() {
		return "-javaagent:\"" + _getJavaagentLocation() + "\"";
	}

	private static boolean _isAdvancedSourcelookupEnabled() {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		return preferencesService.getBoolean(
			JDIDebugPlugin.getUniqueIdentifier(), PREF_ENABLE_ADVANCED_SOURCELOOKUP, false, null);
	}

	private String _getVMArgumentsForSourceLookUp(ILaunchConfiguration configuration, String mode)
		throws CoreException {

		if (!_isAdvancedSourcelup(mode)) {
			return "";
		}

		if (!_isJavaagentOptionSupported(configuration)) {
			return "";
		}

		return _getJavaagentString();
	}

	private boolean _isAdvancedSourcelup(String mode) {
		if (ILaunchManager.DEBUG_MODE.equals(mode) && _isAdvancedSourcelookupEnabled()) {
			return true;
		}

		return false;
	}

	private boolean _isJavaagentOptionSupported(ILaunchConfiguration configuration) {
		try {
			IVMInstall vm = JavaRuntime.computeVMInstall(configuration);

			if (_compareJavaVersions(vm, JavaCore.VERSION_1_4) > 0) {
				return true;
			}
		}
		catch (CoreException ce) {
			LaunchingPlugin.log(ce);
		}

		return false;
	}

	private void _launchServer(
			IServer server, ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		IVMInstall vm = verifyVMInstall(config);

		IVMRunner runner;

		if (vm.getVMRunner(mode) != null) {
			runner = vm.getVMRunner(mode);
		}
		else {
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);
		}

		File workingDir = verifyWorkingDirectory(config);

		String workingDirPath = workingDir != null ? workingDir.getAbsolutePath() : null;

		String progArgs = getProgramArguments(config);
		String vmArgs = getVMArguments(config);
		String[] envp = getEnvironment(config);

		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, progArgs);

		Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap(config);

		PortalServerBehavior portalServer = (PortalServerBehavior)server.loadAdapter(
			PortalServerBehavior.class, monitor);

		String classToLaunch = portalServer.getClassToLaunch();

		String[] classpath = getClasspath(config);

		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(classToLaunch, classpath);

		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());

		List<String> vmArguments = new ArrayList<>();

		Collections.addAll(vmArguments, DebugPlugin.parseArguments(_getVMArgumentsForSourceLookUp(config, mode)));
		Collections.addAll(vmArguments, execArgs.getVMArgumentsArray());
		runConfig.setVMArguments(vmArguments.toArray(new String[vmArguments.size()]));

		runConfig.setWorkingDirectory(workingDirPath);
		runConfig.setEnvironment(envp);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		String[] bootpath = getBootpath(config);

		if (ListUtil.isNotEmpty(bootpath)) {
			runConfig.setBootClassPath(bootpath);
		}

		portalServer.launchServer(launch, mode, monitor);

		server.addServerListener(
			new IServerListener() {

				@Override
				public void serverChanged(ServerEvent event) {
					if ((event.getKind() & ServerEvent.MODULE_CHANGE) > 0) {
						AbstractSourceLookupDirector sourceLocator =
							(AbstractSourceLookupDirector)launch.getSourceLocator();

						try {
							String memento = config.getAttribute(
								ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);

							if (memento != null) {
								sourceLocator.initializeFromMemento(memento);
							}
							else {
								sourceLocator.initializeDefaults(config);
							}
						}
						catch (CoreException ce) {
							LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
						}
					}
					else if (((event.getKind() & ServerEvent.SERVER_CHANGE) > 0) &&
							 (event.getState() == IServer.STATE_STOPPED)) {

						server.removeServerListener(this);
					}
				}

			});

		try {
			runner.run(runConfig, launch, monitor);
			portalServer.addProcessListener(launch.getProcesses()[0]);
		}
		catch (Exception e) {
			portalServer.cleanup();
		}
	}

}