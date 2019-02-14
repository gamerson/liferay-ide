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

package com.liferay.ide.upgrade.problems.core;

import org.eclipse.core.runtime.Plugin;

import org.osgi.framework.BundleContext;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.problems.core";

	public static UpgradeProblemsCorePlugin getDefault() {
		return _plugin;
	}

	public UpgradeProblemsCorePlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	private static UpgradeProblemsCorePlugin _plugin;

}