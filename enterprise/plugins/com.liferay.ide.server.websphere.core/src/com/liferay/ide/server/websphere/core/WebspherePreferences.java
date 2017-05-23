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

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Greg Amerson
 */
public class WebspherePreferences {

	public static final String ADMIN_CONNECTION_TIMEOUT = "admin.connection.timeout";

	protected final static IEclipsePreferences _defaults = DefaultScope.INSTANCE.getNode(WebsphereCore.PLUGIN_ID);

	public static long getAdminConnectionTimeout() {
		return _defaults.getLong(ADMIN_CONNECTION_TIMEOUT, 3000);
	}
}
