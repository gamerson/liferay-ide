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

package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RefactorClamdIntegrationAntivirusScannerTest extends APITestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RefactorClamdIntegrationAntivirusScanner";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

}
