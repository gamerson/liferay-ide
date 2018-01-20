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

package com.liferay.ide.project.core.tests.modules;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.MigrationProblemsContainer;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.io.InputStream;

import java.util.List;

import org.eclipse.core.runtime.IPath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lovett Li
 */
public class UpgradeAssistantSettingsUtilTests {

	@Before
	public void beforeTest() throws Exception {
		File jsonFile = _stateLocation.append("Liferay7UpgradeAssistantSettings.json").toFile();

		if (jsonFile.exists()) {
			Assert.assertTrue(jsonFile.delete());
		}

		File existingFile = _stateLocation.append("MigrationProblemsContainer.json").toFile();

		InputStream input = UpgradeAssistantSettingsUtilTests.class.getResourceAsStream(
			"files/MigrationProblemsContainer.json");

		FileUtil.writeFile(existingFile, input);

		input.close();
	}

	@Test
	public void readExistingMigrationProblemsContainer() throws Exception {
		MigrationProblemsContainer migrationProblemsContainer = UpgradeAssistantSettingsUtil.getObjectFromStore(
			MigrationProblemsContainer.class);

		Assert.assertNotNull(migrationProblemsContainer);

		MigrationProblems[] migrationProblems = migrationProblemsContainer.getProblemsArray();

		Assert.assertEquals("", 2, migrationProblems.length);

		FileProblems[] fileProblems = migrationProblems[0].getProblems();

		Assert.assertNotNull(fileProblems);

		Assert.assertEquals("", 18, fileProblems.length);

		List<Problem> problems = fileProblems[0].getProblems();

		Assert.assertNotNull(problems);

		Assert.assertEquals("", 9, problems.size());
	}

	@Test
	public void readTest() throws Exception {
		writeTest();

		Liferay7UpgradeAssistantSettings settings = UpgradeAssistantSettingsUtil.getObjectFromStore(
			Liferay7UpgradeAssistantSettings.class);

		Assert.assertNotNull(settings);

		PortalSettings portalSettings = settings.getPortalSettings();

		Assert.assertNotNull(portalSettings);

		Assert.assertEquals("Previous Location 6.2", portalSettings.getPreviousLiferayPortalLocation());
		Assert.assertEquals("New Portal Location 7.0", portalSettings.getNewLiferayPortalLocation());
		Assert.assertEquals("New Portal Name 7.0", portalSettings.getNewName());
	}

	@Test
	public void writeTest() throws Exception {
		UpgradeAssistantSettingsUtil.setObjectToStore(
			Liferay7UpgradeAssistantSettings.class, createLiferay7UpgradeAssistantSettingsObject());

		Assert.assertTrue(FileUtil.exists(_stateLocation.append("Liferay7UpgradeAssistantSettings.json")));
	}

	protected Liferay7UpgradeAssistantSettings createLiferay7UpgradeAssistantSettingsObject() {
		PortalSettings portalSettings = new PortalSettings(
			"Previous Location 6.2", "New Portal Name 7.0", "New Portal Location 7.0", "Portal Settings");

		Liferay7UpgradeAssistantSettings settings = new Liferay7UpgradeAssistantSettings();

		settings.setPortalSettings(portalSettings);

		return settings;
	}

	private static final IPath _stateLocation = ProjectCore.getDefault().getStateLocation();

}