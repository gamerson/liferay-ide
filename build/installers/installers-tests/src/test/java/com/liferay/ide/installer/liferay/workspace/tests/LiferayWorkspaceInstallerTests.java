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

package com.liferay.ide.installer.liferay.workspace.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceInstallerTests {

	public final static String VERSION = "1.7.1";

	public static File windowsInstaller;

	public static File linuxInstaller;

	public static File macInstaller;

	@BeforeClass
	public static void beforeClass() {
		File current = new File("");

		File parent = new File(current.getAbsolutePath()).getParentFile();

		File outputs = new File(parent, "outputs");

		assertTrue(outputs.exists());

		String windowsInstallerName = "LiferayWorkspace-" + VERSION + "-windows-installer.exe";

		windowsInstaller = new File(outputs, windowsInstallerName);

		assertTrue(windowsInstaller.exists());

		String liunxInstallerName = "LiferayWorkspace-" + VERSION + "-linux-x64-installer.run";

		linuxInstaller = new File(outputs, liunxInstallerName);

		assertTrue(linuxInstaller.exists());

		String macInstallerName = "LiferayWorkspace-" + VERSION + "-osx-installer.dmg";

		macInstaller = new File(outputs, macInstallerName);

		assertTrue(macInstaller.exists());
	}

	@Test
	public void installWithDefault() {
	}

}
