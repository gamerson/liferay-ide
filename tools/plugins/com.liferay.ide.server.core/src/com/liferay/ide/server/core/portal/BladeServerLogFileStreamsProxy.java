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

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;

/**
 * @author Simon Jiang
 */
public class BladeServerLogFileStreamsProxy extends BladeServerFileStreamsProxy {

	public BladeServerLogFileStreamsProxy(
		PortalServer curPortalServer, PortalServerBehavior curServerBehaviour, ILaunch curLaunch) {

		this(
			curPortalServer, curServerBehaviour, curLaunch, new BladeServerOutputStreamMonitor(),
			new BladeServerOutputStreamMonitor());
	}

	public BladeServerLogFileStreamsProxy(
		PortalServer portalServer, PortalServerBehavior curServerBehaviour, ILaunch curLaunch,
		BladeServerOutputStreamMonitor systemOut, BladeServerOutputStreamMonitor systemErr) {

		_launch = null;

		if ((portalServer == null) || (curServerBehaviour == null)) {
			return;
		}

		_serverBehaviour = curServerBehaviour;

		_launch = curLaunch;

		try {
			PortalRuntime portalRuntime = curServerBehaviour.getPortalRuntime();

			PortalBundle portalBundle = portalRuntime.getPortalBundle();

			IPath apperServerLog = portalBundle.getApperServerLog();
			IPath portalBundleLog = portalBundle.getPortalBundleLog();

			sysoutFile = apperServerLog.toOSString();
			liferayoutFile = portalBundleLog != null ? portalBundleLog.toOSString() : null;
			syserrFile = null;

			if (systemOut != null) {
				sysOut = systemOut;
			}
			else {
				sysOut = new BladeServerOutputStreamMonitor();
			}

			if (systemErr != null) {
				sysErr = systemErr;
			}
			else {
				sysErr = new BladeServerOutputStreamMonitor();
			}

			startMonitoring();
		}
		catch (Exception e) {
		}
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	public PortalServerBehavior getServerBehaviour() {
		return _serverBehaviour;
	}

	private ILaunch _launch;
	private PortalServerBehavior _serverBehaviour;

}