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

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Seiphon Wang
 */
public abstract class AutoCorrectLiferayVersionPropertiesTestBase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void autoCorrectProblems() throws Exception {
		File testFile = temporaryFolder.newFile("liferay-plugin-package.properties");

		File originalFile = getOriginalTestFile();

		Files.copy(originalFile.toPath(), testFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		FileMigrator liferayVersionsProperties = null;

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<FileMigrator>> serviceReferences =
			bundleContext.getServiceReferences(FileMigrator.class, null);

		List<ServiceReference<FileMigrator>> filteredReferences = serviceReferences.stream(
		).filter(
			ref -> {
				Dictionary<String, Object> serviceProperties = ref.getProperties();

				Version version = new Version(getVersion());

				return Optional.ofNullable(
					serviceProperties.get("version")
				).map(
					Object::toString
				).map(
					Version::valueOf
				).filter(
					v -> v.equals(version)
				).isPresent();
			}
		).collect(
			Collectors.toList()
		);

		for (ServiceReference<FileMigrator> serviceReference : filteredReferences) {
			FileMigrator fileMigrator = bundleContext.getService(serviceReference);

			Class<?> clazz = fileMigrator.getClass();

			if (Objects.equals(clazz.getSimpleName(), getImplClassName())) {
				liferayVersionsProperties = fileMigrator;

				break;
			}
		}

		Assert.assertNotNull("Expected that a valid liferayVersionsProperties would be found", liferayVersionsProperties);

		List<UpgradeProblem> upgradeProblems = liferayVersionsProperties.analyze(testFile);

		Assert.assertEquals("Expected to have found exactly one problem.", 1, upgradeProblems.size());

		int problemsFixed = ((AutoFileMigrator)liferayVersionsProperties).correctProblems(testFile, upgradeProblems);

		Assert.assertEquals("Expected to have fixed exactly one problem.", 1, problemsFixed);

		upgradeProblems = liferayVersionsProperties.analyze(testFile);

		Assert.assertEquals("Expected to not find any problems.", 0, upgradeProblems.size());
	}

	public abstract String getImplClassName();

	public abstract File getOriginalTestFile();

	public abstract String getVersion();
}
