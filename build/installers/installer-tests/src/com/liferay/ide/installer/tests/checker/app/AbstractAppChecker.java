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

package com.liferay.ide.installer.tests.checker.app;

import com.liferay.ide.installer.tests.util.CommandHelper;

import java.io.File;
import java.io.IOException;

/**
 * @author Terry Jia
 */
public class AbstractAppChecker implements AppChecker {

	public AbstractAppChecker(String cmd, String exceptResult) {
		_cmd = cmd;
		_exceptResult = exceptResult;
		_filePaths = new String[0];
	}

	public AbstractAppChecker(String cmd, String exceptResult, String[] filePaths) {
		_cmd = cmd;
		_exceptResult = exceptResult;
		_filePaths = filePaths;
	}

	@Override
	public boolean filesExist() {
		for (String filePath : _filePaths) {
			File file = new File(filePath);

			if (!file.exists()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean installed() throws IOException {
		String output = CommandHelper.execWithResult(_cmd);

		String outputError = CommandHelper.execWithResult(_cmd, true);

		if (output.contains(_exceptResult) || outputError.contains(_exceptResult)) {
			return true;
		}

		return false;
	}

	private String _cmd;
	private String _exceptResult;
	private String[] _filePaths;

}