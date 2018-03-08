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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.base.PureTomcat70Support;
import com.liferay.ide.ui.liferay.base.SdkSupport;
import com.liferay.ide.ui.liferay.base.ServerRunningSupport;
import com.liferay.ide.ui.liferay.base.ServerSupport;
import com.liferay.ide.ui.liferay.base.TomcatSupport;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
public class Tomcat70DeployTests extends TomcatDeployTests {

	@ClassRule
	public static RuleChain chain = RuleChain.outerRule(
		getServer()).around(new TomcatSupport(bot, getServer())).around(new SdkSupport(bot, getServer())).around(new ServerRunningSupport(bot, getServer()));

	public static ServerSupport getServer() {
		if ((server == null) || !(server instanceof PureTomcat70Support)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Test
	public void deployFragment() {
		super.deployFragment();
	}

	@Test
	public void deployModule() {
		super.deployModule();
	}

	@Test
	public void deployPluginPortlet() {
		super.deployPluginPortlet();
	}

	@Test
	public void deployWar() {
		super.deployWar();
	}

}