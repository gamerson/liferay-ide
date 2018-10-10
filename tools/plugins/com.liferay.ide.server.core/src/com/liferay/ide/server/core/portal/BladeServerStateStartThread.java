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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class BladeServerStateStartThread {

	// delay before pinging starts

	/**
	 * Create a new PingThread.
	 *
	 * @param server
	 * @param url
	 * @param maxPings  the maximum number of times to try pinging, or -1 to
	 *                  continue forever
	 * @param behaviour
	 */
	public BladeServerStateStartThread(IServer server, PortalServerBehavior behaviour) {
		_server = server;
		_behaviour = behaviour;
		_mointorProcess = behaviour.getProcess();

		_bladeServer = (PortalServer)_server.loadAdapter(PortalServer.class, null);

		int serverStartTimeout = server.getStartTimeout();

		if (serverStartTimeout < (_defaultTimeout / 1000)) {
			_timeout = _defaultTimeout;
		}
		else {
			_timeout = serverStartTimeout * 1000;
		}

		_liferayHomeUrl = _bladeServer.getPortalHomeUrl();

		Thread t = new Thread("Liferay Blade Server Start Thread") {

			public void run() {
				_startedTime = System.currentTimeMillis();
				startMonitor();
			}

		};

		t.setDaemon(true);
		t.start();
	}

	// delay between pings

	/**
	 * Tell the pinging to stop.
	 */
	public void stop() {
		_stop = true;
	}

	/**
	 * Ping the server until it is started. Then set the server state to
	 * STATE_STARTED.
	 */
	protected void startMonitor() {
		long currentTime = 0;

		try {
			Thread.sleep(_pingDelay);
		}
		catch (Exception e) {
		}
		while (!_stop) {
			try {
				currentTime = System.currentTimeMillis();

				if ((currentTime - _startedTime) > _timeout) {
					try {
						_server.stop(true);
						((IBladeServerStartStreamsProxy)_mointorProcess.getStreamsProxy()).terminate();
						_behaviour.triggerCleanupEvent(_mointorProcess);
					}
					catch (Exception e) {
					}

					_stop = true;

					break;
				}

				URLConnection conn = _liferayHomeUrl.openConnection();

				conn.setReadTimeout(_pingInterval);

				((HttpURLConnection)conn).setInstanceFollowRedirects(false);
				int code = ((HttpURLConnection)conn).getResponseCode();

				if (!_stop && (code != 404)) {
					Thread.sleep(200);
					_behaviour.setServerStarted();
					_stop = true;
				}

				Thread.sleep(1000);
			}
			catch (Exception e) {
				if (!_stop) {
					try {
						Thread.sleep(_pingInterval);
					}
					catch (InterruptedException ie) {
					}
				}
			}
		}
	}

	private static int _pingDelay = 2000;
	private static int _pingInterval = 250;

	private PortalServerBehavior _behaviour;
	private PortalServer _bladeServer;
	private long _defaultTimeout = 15 * 60 * 1000;
	private URL _liferayHomeUrl;
	private IProcess _mointorProcess;
	private IServer _server;
	private long _startedTime;
	private boolean _stop = false;
	private long _timeout = 0;

}