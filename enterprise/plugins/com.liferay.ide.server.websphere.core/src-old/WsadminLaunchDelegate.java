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

package com.liferay.ide.eclipse.server.ee.websphere.wsadmin;

import com.liferay.ide.eclipse.server.ee.websphere.core.IWebsphereServerWorkingCopy;
import com.liferay.ide.eclipse.server.ee.websphere.util.WebsphereUtil;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.externaltools.internal.launchConfigurations.ProgramLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class WsadminLaunchDelegate extends ProgramLaunchDelegate {

	public static final String TYPE_ID = "com.liferay.ide.eclipse.server.ee.websphere.wsadminLaunchConfigType";

	public static void initFromRuntime(IRuntime runtime, ILaunchConfigurationWorkingCopy config) {
		IPath wsadminPath = WebsphereUtil.getWsadminPath(runtime);
		config.setAttribute(IWsadminConstants.ATTR_WEBSPHERE_RUNTIME_ID, runtime.getId());
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, wsadminPath.toOSString());
		config.setAttribute(
			IExternalToolConstants.ATTR_WORKING_DIRECTORY, wsadminPath.removeLastSegments(1).toOSString());
	}

	public static void initFromServer(IServer server, ILaunchConfigurationWorkingCopy config) {
		IRuntime runtime = server.getRuntime();
		initFromRuntime(runtime, config);

		config.setAttribute(IWebsphereServerWorkingCopy.ATTR_HOSTNAME, server.getHost());
		config.setAttribute(
			IWebsphereServerWorkingCopy.ATTR_CONNECTION_TYPE, IWebsphereServerWorkingCopy.DEFAULT_CONNECTION_TYPE);
		config.setAttribute(IWebsphereServerWorkingCopy.ATTR_SOAP_PORT, IWebsphereServerWorkingCopy.DEFAULT_SOAP_PORT);
		config.setAttribute(IWebsphereServerWorkingCopy.ATTR_RMI_PORT, IWebsphereServerWorkingCopy.DEFAULT_RMI_PORT);
		config.setAttribute(
			IWebsphereServerWorkingCopy.ATTR_SECURITY_ENABLED, IWebsphereServerWorkingCopy.DEFAULT_SECURITY_ENABLED);
		config.setAttribute(IWebsphereServerWorkingCopy.ATTR_USERNAME, "");
		config.setAttribute(IWebsphereServerWorkingCopy.ATTR_PASSWORD, "");	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
		throws CoreException {

		// don't build
		return false;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		ILaunchConfigurationWorkingCopy config = configuration.getWorkingCopy();
		String args =
			getDefaultArgs() + "\n" + getHostnameArg(config) +
				config.getAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "");
		config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, args);

		super.launch(config, mode, launch, monitor);
	}

	private String getDefaultArgs() {
		return IWsadminConstants.DEFAULT_ARGS;
	}

	private String getHostnameArg(ILaunchConfiguration config)
		throws CoreException {

		return "-host\n" + config.getAttribute(IWebsphereServerWorkingCopy.ATTR_HOSTNAME, "") + "\n";
	}

	@Override
	protected boolean saveBeforeLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
		throws CoreException {

		// do nothing
		return true;
	}

}
