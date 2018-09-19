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

import com.liferay.ide.installer.tests.util.FileUtil;
import com.liferay.ide.installer.tests.util.InstallerUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Jia
 */
public class InstallersTest {

	@Test
	public void checkDevStudioCEInstallerFiles() {
		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioCEWinFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioCELinuxFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioCEMacosFile()));
	}

	@Test
	public void checkDevStudioDXPInstallerFiles() {
		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioDXPWinFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioDXPLinuxFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getDevStudioDXPMacosFile()));
	}

	@Test
	public void checkProjectSDKInstallerFiles() {
		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getProjectSdkWinFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getProjectSdkLinuxFile()));

		Assertions.assertTrue(FileUtil.exsits(InstallerUtil.getProjectSdkMacosFile()));
	}

}