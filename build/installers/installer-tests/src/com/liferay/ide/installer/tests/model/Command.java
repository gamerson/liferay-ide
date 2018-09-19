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

package com.liferay.ide.installer.tests.model;

import com.liferay.ide.installer.tests.util.CommandHelper;
import com.liferay.ide.installer.tests.util.InstallerUtil;

/**
 * @author Terry Jia
 */
public class Command {

	public Command(Installer installer, String[] args) {
		_installer = installer;
		_args = args;
		_mode = "unattended";
	}

	public Command(Installer installer, String[] args, String mode) {
		_installer = installer;
		_mode = mode;
	}

	public void run() throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append(_installer.getFullName());

		sb.append(" --mode");
		sb.append(" ");
		sb.append(_mode);

		//TODO need to do more research on proxy settings

		sb.append(" --proxyhttps nothing");

		for (String arg : _args) {
			sb.append(" ");
			sb.append(arg);
		}

		CommandHelper.exec(InstallerUtil.getOutputDir(), sb.toString());
	}

	private String[] _args;
	private Installer _installer;
	private String _mode;

}