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

package com.liferay.ide.installer.tests;

import com.liferay.ide.installer.tests.checker.app.AppChecker;
import com.liferay.ide.installer.tests.checker.app.AppCheckerFactory;
import com.liferay.ide.installer.tests.checker.file.FileChecker;
import com.liferay.ide.installer.tests.checker.file.FileCheckerFactory;
import com.liferay.ide.installer.tests.checker.process.ProcessChecker;
import com.liferay.ide.installer.tests.checker.process.ProcessFactory;
import com.liferay.ide.installer.tests.model.Command;
import com.liferay.ide.installer.tests.model.Installer;
import com.liferay.ide.installer.tests.model.LiferayProjectSdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * @author Terry Jia
 */
public class LiferayProjectSdkTest {

	@EnabledOnOs(OS.LINUX)
	@Test
	public void quickInstallOnLinux() {
		Assertions.assertTrue(true);
	}

	@EnabledOnOs(OS.WINDOWS)
	@Test
	public void quickInstallOnWindows() throws Exception {
		Installer installer = new LiferayProjectSdk(Installer.WINDOWS);

		String[] args = new String[0];

		Command command = new Command(installer, args);

		command.run();

		String processName = installer.getFullName();

		ProcessChecker processChecker = ProcessFactory.processCheckerWin(processName);

		Assertions.assertTrue(processChecker.checkProcess());
		Assertions.assertTrue(processChecker.waitProcess());

		FileChecker tokenChecker = FileCheckerFactory.tokenChecker();

		Assertions.assertFalse(tokenChecker.exists());

		AppChecker bladeChecker = AppCheckerFactory.bladeChecker();

		Assertions.assertTrue(bladeChecker.installed());

		AppChecker bndChecker = AppCheckerFactory.bndChecker();

		Assertions.assertTrue(bndChecker.installed());

		AppChecker gwChecker = AppCheckerFactory.gwChecker();

		Assertions.assertTrue(gwChecker.installed());

		AppChecker jpmChecker = AppCheckerFactory.jpmChecker();

		Assertions.assertTrue(jpmChecker.installed());
	}

}