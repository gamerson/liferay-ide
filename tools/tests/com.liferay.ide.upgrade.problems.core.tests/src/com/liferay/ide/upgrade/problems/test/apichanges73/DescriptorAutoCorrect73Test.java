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

package com.liferay.ide.upgrade.problems.test.apichanges73;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.AutoCorrectDescriptorTestBase;

/**
 * @author Seiphon Wang
 */
public class DescriptorAutoCorrect73Test extends AutoCorrectDescriptorTestBase {

	@Override
	public String getImplClassName() {
		return "Liferay73DescriptorVersion";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/liferay-portlet.xml");
	}

	@Override
	public String getVersion() {
		return "7.3";
	}

}
