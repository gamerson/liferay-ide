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

package com.liferay.ide.project.core.tests;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class LiferayPortalValueLoaderTests extends ProjectCoreBase {

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Test
	public void loadHookPropertiesFromClass() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		setupPluginsSDKAndRuntime();

		IRuntime runtime = ServerCore.getRuntimes()[0];

		String[] props = _loader(runtime).loadHookPropertiesFromClass();

		Assert.assertNotNull(props);

		Assert.assertEquals("", 142, props.length);
	}

	@Test
	public void loadServerInfoFromClass() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		setupPluginsSDKAndRuntime();

		IRuntime runtime = ServerCore.getRuntimes()[0];

		String info = _loader(runtime).loadServerInfoFromClass();

		Assert.assertNotNull(info);

		Assert.assertEquals("Liferay Portal Community Edition / 6.2.5", info);
	}

	@Test
	public void loadVersionFromClass() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		setupPluginsSDKAndRuntime();

		IRuntime runtime = ServerCore.getRuntimes()[0];

		Version version = _loader(runtime).loadVersionFromClass();

		Assert.assertNotNull(version);

		Assert.assertEquals("6.2.5", version.toString());
	}

	@Before
	public void removeRuntimes() throws Exception {
		super.removeAllRuntimes();
	}

	private LiferayPortalValueLoader _loader(IRuntime runtime) {
		ILiferayRuntime liferayRutime = ServerUtil.getLiferayRuntime(runtime);

		return new LiferayPortalValueLoader(liferayRutime.getUserLibs());
	}

}