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

package com.liferay.ide.eclipse.server.ee.websphere.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;

/**
 * @author Greg Amerson
 */
public class WASConnectionManager {

	private static WASConnectionManager instance = new WASConnectionManager();

	private static Map<String, WsadminConnection> adminConnectionMap = new HashMap<String, WsadminConnection>();

	static WASConnectionManager getInstance() {
		return instance;
	}

	private WASConnectionManager() {
	}

	public void closeConnection(IServer server) {
		closeAdminConnection(buildKey(server));
	}

	private String buildKey(IServer server) {
		return server.getId() + "#" + server.getHost();
	}

	public void closeAdminConnection(IServerWorkingCopy server) {
		closeAdminConnection(buildKey(server));
	}

	private String buildKey(IServerWorkingCopy server) {
		return server.getId() + "#" + server.getHost();
	}

	public synchronized WsadminConnection getAdminConnection(IServer server, boolean create) {
		String key = buildKey(server);

		WsadminConnection connection = adminConnectionMap.get(key);

		if (connection != null) {
			if (connection.isConnectionValid()) {
				return connection;
			}

			// connection was no good so create a new one
			connection.close();

			adminConnectionMap.remove(key);
		}

		if (create) {
			connection = new WsadminConnection(server);
			adminConnectionMap.put(key, connection);
		}

		return connection;
	}

	public WsadminConnection getAdminConnection(IServerWorkingCopy server, boolean create) {
		return getAdminConnection(server.getOriginal(), create);
	}

	protected void closeAdminConnection(String serverKey) {
		WsadminConnection connection = adminConnectionMap.get(serverKey);

		if (connection != null) {
			connection.close();

			adminConnectionMap.remove(serverKey);
		}
	}

}
