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

package com.liferay.ide.installer.tests.checker.file;

import java.io.File;

/**
 * @author Terry Jia
 */
public class AbstractFileChecker implements FileChecker {

	public AbstractFileChecker(File parent, String fileName) {
		_parent = parent;
		_fileName = fileName;
	}

	@Override
	public boolean exists() {
		File file = new File(_parent, _fileName);

		return file.exists();
	}

	private String _fileName;
	private File _parent;

}