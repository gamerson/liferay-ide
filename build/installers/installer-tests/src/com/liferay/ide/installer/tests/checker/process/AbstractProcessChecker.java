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

import com.liferay.ide.installer.tests.util.CommandHelper;

import java.io.IOException;

/**
 * @author Terry Jia
 */
public abstract class AbstractProcessChecker implements ProcessChecker {

	public AbstractProcessChecker(String processName) {
		_processName = processName;
	}

	public boolean waitProcess() throws Exception {
		long timeout = System.currentTimeMillis() + 120 * 1000;

		boolean finished = false;

		while (true) {
			if (System.currentTimeMillis() > timeout) {
				break;
			}

			Thread.sleep(1000);

			if (!checkProcess()) {
				finished = true;

				break;
			}
		}

		Thread.sleep(1000);

		return finished;
	}

	protected boolean checkProcess(String cmd) throws IOException {
		String result = CommandHelper.execWithResult(cmd);

		if (result.contains(_processName)) {
			return true;
		}

		return false;
	}

	private String _processName;

}