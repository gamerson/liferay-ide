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

package com.liferay.ide.server.core.gogo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Terry Jia
 */
public class GogoBundleDeployer {

	private static Map<String, GogoBundleDeployer> _instance = null;

	public static GogoBundleDeployer getInstance(String host, int port) {

		if (_instance == null) {
			_instance = new HashMap<String, GogoBundleDeployer>();
		}

		GogoBundleDeployer deployer = _instance.get(host + port);
		if (deployer == null) {

			deployer = new GogoBundleDeployer(host, port);
			_instance.put(host + port, deployer);
		}

		return deployer;
	}

	private static int _getState(String state) {
		if ("Active".equals(state)) {
			return Bundle.ACTIVE;
		}
		else if ("Starting".equals(state)) {
			return Bundle.STARTING;
		}
		else if ("Resolved".equals(state)) {
			return Bundle.RESOLVED;
		}
		else if ("Stopping".equals(state)) {
			return Bundle.STOPPING;
		}
		else if ("Installed".equals(state)) {
			return Bundle.INSTALLED;
		}
		else if ("Uninstalled".equals(state)) {
			return Bundle.UNINSTALLED;
		}

		return -1;
	}

	/**
	 * Return the string array with trim
	 */
	private static String[] _split(String string, String regex) {
		String[] lines = string.split(regex);

		String[] newLines = new String[lines.length];

		for (int i = 0; i < lines.length; i++) {
			newLines[i] = lines[i].trim();
		}

		return newLines;
	}

	/**
	 * The content should be the result from GogoShell by "lb -s"
	 * The format should be:
	 * START LEVEL 20\r\n
	 *    ID|State      |Level|Symbolic name\r\n
	 *     0|Active     |    0|org.eclipse.osgi (3.10.200.v20150831-0856)\r\n
	 *     1|Active     |    6|com.liferay.portal.startup.monitor (1.0.2)\r\n
	 *     ......
	 *   146|Active     |   10|com.liferay.asset.tags.service (2.0.2)
	 * We will get id, state, symbolicName, version for bundles by parsing:
	 * 146|Active     |   10|com.liferay.asset.tags.service (2.0.2)
	 */
	private static BundleDTO[] _parseBundleInfos(String content) {
		String[] lines = _split(content, "\r\n");

		if (lines.length < 3) {
			return new BundleDTO[0];
		}

		String[] newLines = new String[lines.length - 2];

		System.arraycopy(lines, 2, newLines, 0, newLines.length);

		BundleDTO[] bundles = new BundleDTO[newLines.length];

		for( int i = 0;i < bundles.length; i++) {
			BundleDTO bundle = new BundleDTO();

			String line = newLines[i];

			String[] infos = _split(line, "\\|");

			bundle.id = Long.parseLong(infos[0]);
			bundle.state = _getState(infos[1]);
			bundle.symbolicName = infos[3].substring(0, infos[3].indexOf("(")).trim();
			bundle.version = infos[3].substring(infos[3].indexOf("(") + 1, infos[3].indexOf(")")).trim();

			bundles[i] = bundle;
		}

		return bundles;
	}

	private String _host;

	private int _port;

	public GogoBundleDeployer() {
		_host = "localhost";
		_port = 11311;
	}

	public GogoBundleDeployer(String host, int port) {
		_host = host;
		_port = port;
	}

	public BundleDTO getBundle(long bid)
		throws IOException {

		try (GogoTelnetClient client = new GogoTelnetClient(_host, _port)) {
			String result = client.send("lb -s ", true);

			if ("No matching bundles found".equals(result)) {
				return null;
			}

			BundleDTO[] bundles = _parseBundleInfos(result);

			for (BundleDTO bundle : bundles) {
				if (bundle.id == bid) {
					return bundle;
				}
			}
		}

		return null;
	}

	public BundleDTO getBundle(String bsn)
		throws IOException {

		try (GogoTelnetClient client = new GogoTelnetClient(_host, _port)) {
			String result = client.send("lb -s ", true);

			if ("No matching bundles found".equals(result)) {
				return null;
			}

			BundleDTO[] bundles = _parseBundleInfos(result);

			for (BundleDTO bundle : bundles) {
				if (bundle.symbolicName.equals(bsn)) {
					return bundle;
				}
			}
		}

		return null;
	}

	public String run(String command)
		throws IOException {

		try (GogoTelnetClient client = new GogoTelnetClient(_host, _port)) {
			String response = client.send(command, true);
			return response;
		}
	}
}
