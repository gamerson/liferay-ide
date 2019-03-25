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

package com.liferay.ide.gradle.core.tests;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.gradle.core.tests.util.GradleTestUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectBase;

import java.io.File;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class GradleProjectTests extends ProjectBase {

	@Test
	public void getOutputJar() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("get-output-jar");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		IPath outputJar = gradleProject.getOutputBundle(false, npm);

		assertFileExists(outputJar);

		File file = outputJar.toFile();

		Files.deleteIfExists(file.toPath());

		assertFileNotExists(outputJar);

		outputJar = gradleProject.getOutputBundle(true, npm);

		assertFileExists(outputJar);

		deleteProject(ips);
	}

	@Test
	public void getSymbolicName() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("get-symbolic-name");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		Assert.assertEquals("com.liferay.test.bsn", gradleProject.getSymbolicName());

		deleteProject(ips);
	}

	@Test
	public void hasGradleBundlePluginDetection() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("test-gradle");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, gradleProject.getProject());

		Assert.assertNotNull(bundleProject);

		Assert.assertEquals(LiferayGradleProject.class, bundleProject.getClass());

		deleteProject(ips);
	}

	@Test
	public void testAddGradleDependency() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("test-gradle-dependency");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		Artifact artifact = new Artifact("com.liferay.portal", "com.liferay.portal.kernel", "2.6.0", "complie", null);

		IProject project = gradleProject.getProject();

		IFile gradileFile = project.getFile("build.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(FileUtil.getFile(gradileFile));

		List<Artifact> existDependencies = updater.getDependencies("*");

		Assert.assertFalse(existDependencies.contains(artifact));

		IProjectBuilder gradleProjectBuilder = gradleProject.adapt(IProjectBuilder.class);

		gradleProjectBuilder.updateDependencies(project, Arrays.asList(artifact));

		GradleDependencyUpdater dependencyUpdater = new GradleDependencyUpdater(FileUtil.getFile(gradileFile));

		List<Artifact> updatedDependencies = dependencyUpdater.getDependencies("*");

		Assert.assertTrue(updatedDependencies.contains(artifact));

		deleteProject(ips);
	}

	@Test
	public void toolingApiCustomModel() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("custom-model");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		CustomModel customModel = LiferayGradleCore.getToolingModel(CustomModel.class, gradleProject.getProject());

		Assert.assertNotNull(customModel);

		Assert.assertFalse(customModel.hasPlugin("not.a.plugin"));

		Assert.assertTrue(customModel.hasPlugin("aQute.bnd.gradle.BndWorkspacePlugin"));

		deleteProject(ips);
	}

}