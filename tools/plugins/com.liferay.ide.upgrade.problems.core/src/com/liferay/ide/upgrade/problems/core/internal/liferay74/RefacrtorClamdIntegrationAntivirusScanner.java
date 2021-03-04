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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Refactor Clamd integration to use Clamd remote service and remove portal",
		"problem.summary=properties configuration for AntivirusScanner selection and hook support for AntivirusScanner registration in favor of AntivirusScanner OSGi integration",
		"problem.tickets=LPS-122280", "problem.section=#remove-clamd-integration-antivirusscanner-properties", "problem.version=7.4", 
		"version=7.4"
	},
	service = FileMigrator.class
)
public class RefacrtorClamdIntegrationAntivirusScanner extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("dl.store.antivirus.impl");
	}

}