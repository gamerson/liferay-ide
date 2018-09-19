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

package com.liferay.ide.installer.tests.checker.process;

/**
 * @author Ashley Yuan
 * @author Terry Jia
 */
public class ProcessFactory {

	public static ProcessChecker processCheckerLinux(String processName) {
		return new LinuxProcessChecker(processName);
	}

	public static ProcessChecker processCheckerMacox(String processName) {
		return new MacosProcessChecker(processName);
	}

	public static ProcessChecker processCheckerWin(String processName) {
		return new WindowsProcessChecker(processName);
	}

}