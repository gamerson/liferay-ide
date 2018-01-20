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

package com.liferay.ide.project.core.modules;

import aQute.bnd.osgi.Domain;

import com.liferay.ide.project.core.ProjectCore;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLITests {

	@Test
	public void bladeCLICreateProject() throws Exception {
		Path temp = Files.createTempDirectory("path with spaces");

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(temp.toAbsolutePath().toString());
		sb.append("\" ");
		sb.append("-t mvc-portlet ");
		sb.append("foo");

		BladeCLI.execute(sb.toString());

		Assert.assertTrue(new File(temp.toFile(), "foo/build.gradle").exists());
	}

	@Test
	public void bladeCLIExecute() throws Exception {
		String[] output = BladeCLI.execute("help");

		Assert.assertNotNull(output);

		Assert.assertTrue(output.length > 0);

		for (String line : output) {
			if (line.contains("[null]")) {
				Assert.fail("Output contains [null]");
			}
		}
	}

	@Test
	public void bladeCLIProjectTemplates() throws Exception {
		String[] projectTemplates = BladeCLI.getProjectTemplates();

		Assert.assertNotNull(projectTemplates);

		Assert.assertTrue(projectTemplates[0], projectTemplates[0].startsWith("activator"));

		Assert.assertTrue(
			projectTemplates[projectTemplates.length - 1],
			projectTemplates[projectTemplates.length - 1].startsWith("war-mvc-portlet"));
	}

	@After
	public void setBladeURLefaultPreferences() {
		IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

		String defaultValue = defaults.get(BladeCLI.BLADE_CLI_REPO_URL, "");

		prefs.put(BladeCLI.BLADE_CLI_REPO_URL, defaultValue);

		try {
			prefs.flush();
		}
		catch (BackingStoreException bse) {
		}
	}

	@Test
	public void testBundleFileIsFromBundle() throws Exception {
		IPath path = BladeCLI.getBladeCLIPath();

		IPath stateLocation = ProjectCore.getDefault().getStateLocation();

		Assert.assertFalse(stateLocation.isPrefixOf(path));
	}

	@Test
	public void testBundleFileIsValid() throws Exception {
		IPath path = BladeCLI.getBladeCLIPath();

		File bladeFile = path.toFile();

		Assert.assertTrue(bladeFile.exists());

		Domain domain = Domain.domain(bladeFile);

		Assert.assertTrue(domain.getBundleVersion().startsWith("2"));

		Assert.assertFalse(domain.getBundleVersion().startsWith("3"));
	}

	@Test
	public void testUpdate1xWillFail() throws Exception {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

		prefs.put(BladeCLI.BLADE_CLI_REPO_URL, "https://releases.liferay.com/tools/blade-cli/1.x/");

		prefs.flush();

		String latestVersion = null;

		try {
			latestVersion = Domain.domain(BladeCLI.fetchBladeJarFromRepo(false)).getBundleVersion();
		}
		catch (Exception e) {
		}

		Assert.assertNull(latestVersion);
	}

	@Ignore
	@Test
	public void testUpdateBladeFromCloudbees() throws Exception {
		IPath originalPath = BladeCLI.getBladeCLIPath();

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

		prefs.put(
			BladeCLI.BLADE_CLI_REPO_URL,
			"https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/");

		prefs.flush();

		File latestBladeJar = BladeCLI.fetchBladeJarFromRepo(false);

		Version latestVersionFromRepo = new Version(Domain.domain(latestBladeJar).getBundleVersion());

		Domain bladeFromBundle = Domain.domain(originalPath.toFile());

		if (latestVersionFromRepo.compareTo(new Version(bladeFromBundle.getBundleVersion())) > 0) {
			BladeCLI.addToLocalInstance(latestBladeJar);

			Assert.assertEquals(
				new Version(Domain.domain(BladeCLI.getBladeCLIPath().toFile()).getBundleVersion()),
				new Version(Domain.domain(latestBladeJar).getBundleVersion()));
		}
	}

}