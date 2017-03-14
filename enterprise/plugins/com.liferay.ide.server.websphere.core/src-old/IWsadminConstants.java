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

import com.liferay.ide.eclipse.server.ee.websphere.core.WebsphereCore;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Greg Amerson
 */
public interface IWsadminConstants {

	public static final IEclipsePreferences defaultPrefs = new DefaultScope().getNode(WebsphereCore.PLUGIN_ID);

	String ATTR_WEBSPHERE_RUNTIME_ID = "wsadmin-websphere-runtime-id";

	long CONNECTION_TIMEOUT = defaultPrefs.getLong("wsadmin.connection.timeout", 0);

	String DEFAULT_ARGS = defaultPrefs.get("wsadmin.default.args", "");

	String WSADMIN_KEY_CONNECTED = defaultPrefs.get("wsadmin.key.connected", "");

	String WSADMIN_KEY_NOT_CONNECTED = defaultPrefs.get("wsadmin.key.not.connected", "");

	String WSADMIN_MESSAGE_KEY_PATTERN = defaultPrefs.get("wsadmin.message.key.pattern", "");

}
