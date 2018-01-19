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

import org.junit.Assert;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class GradleProjectTests {

	@BeforeClass
	public static void deleteAllWorkspaceProjects()
		throws Exception {

		Util.deleteAllWorkspaceProjects();
	}

	@Test
	public void getSymbolicName()
		throws Exception {

		LiferayGradleProject gradleProject =
			Util.fullImportGradleProject("projects/getSymbolicName");

		Assert.assertNotNull(gradleProject);

		NullProgressMonitor monitor = new NullProgressMonitor();

		IPath outputJar = gradleProject.getOutputBundle(false, monitor);

		if (outputJar != null && outputJar.toFile().exists()) {
			outputJar = gradleProject.getOutputBundle(true, monitor);
		}

		Assert.assertTrue(outputJar.toFile().exists());

		Assert.assertEquals("com.liferay.test.bsn", gradleProject.getSymbolicName());
	}

	@Test
	public void getOutputJar()
		throws Exception {

		LiferayGradleProject gradleProject =
			Util.fullImportGradleProject("projects/getOutputJar");

		Assert.assertNotNull(gradleProject);

		NullProgressMonitor monitor = new NullProgressMonitor();

		IPath outputJar = gradleProject.getOutputBundle(false, monitor);

		Assert.assertNotNull(outputJar);

		if (outputJar.toFile().exists()) {
			outputJar.toFile().delete();
		}

		Assert.assertTrue(!outputJar.toFile().exists());

		outputJar = gradleProject.getOutputBundle(true, monitor);

		Assert.assertNotNull(outputJar);

		Assert.assertTrue(outputJar.toFile().exists());
	}

	/*
	 * @Test public void gradleProjectProviderCache() throws Exception { final
	 * int[] consolesAdded = new int[1]; IConsoleListener consoleListener = new
	 * IConsoleListener() {
	 * @Override public void consolesRemoved( IConsole[] consoles ) { }
	 * @Override public void consolesAdded( IConsole[] consoles ) {
	 * consolesAdded[0]++; } };
	 * ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(
	 * consoleListener );; LiferayGradleProject gradleProject =
	 * fullImportGradleProject( "projects/cacheTest" ); assertNotNull(
	 * gradleProject ); IBundleProject bundleProject = LiferayCore.create(
	 * IBundleProject.class, gradleProject.getProject() ); assertNotNull(
	 * bundleProject ); assertEquals( LiferayGradleProject.class,
	 * bundleProject.getClass() ); assertEquals( 1, consolesAdded[0] );
	 * bundleProject = LiferayCore.create( IBundleProject.class,
	 * gradleProject.getProject() ); assertNotNull( bundleProject );
	 * assertEquals( LiferayGradleProject.class, bundleProject.getClass() );
	 * assertEquals( 1, consolesAdded[0] ); IFile buildFile =
	 * gradleProject.getProject().getFile( "build.gradle" ); String
	 * buildFileContents = CoreUtil.readStreamToString( buildFile.getContents(
	 * true ), true ); String updatedContents = buildFileContents.replaceAll(
	 * "apply plugin: 'org.dm.bundle'", "" ); buildFile.setContents( new
	 * ByteArrayInputStream( updatedContents.getBytes() ), IResource.FORCE, new
	 * NullProgressMonitor() ); final Object lock = new Object();
	 * IGradleModelListener gradleModelListener = new IGradleModelListener() {
	 * @Override public <T> void modelChanged( GradleProject project, Class<T>
	 * type, T model ) { synchronized( lock ) { lock.notify(); } } };
	 * gradleProject.addModelListener( gradleModelListener );
	 * gradleProject.requestGradleModelRefresh(); synchronized( lock ) {
	 * lock.wait(); } bundleProject = LiferayCore.create( IBundleProject.class,
	 * gradleProject.getProject() ); assertNull( bundleProject ); assertEquals(
	 * 2, consolesAdded[0] ); buildFile.setContents( new ByteArrayInputStream(
	 * buildFileContents.getBytes() ), IResource.FORCE, new
	 * NullProgressMonitor() ); gradleProject.requestGradleModelRefresh();
	 * synchronized( lock ) { lock.wait(); } bundleProject = LiferayCore.create(
	 * IBundleProject.class, gradleProject.getProject() ); assertNotNull(
	 * bundleProject ); assertEquals( 3, consolesAdded[0] ); }
	 */

	@Test
	public void hasGradleBundlePluginDetection()
		throws Exception {

		final LiferayGradleProject gradleProject =
			Util.fullImportGradleProject("projects/biz.aQute.bundle");

		Assert.assertNotNull(gradleProject);

		final IBundleProject[] bundleProject = new IBundleProject[1];

		WorkspaceJob job = new WorkspaceJob("") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
				throws CoreException {

				bundleProject[0] = LiferayCore.create(
					IBundleProject.class, gradleProject.getProject());
				return Status.OK_STATUS;
			}
		};

		job.schedule(5000);
		job.join();

		Assert.assertNotNull(bundleProject[0]);

		Assert.assertEquals(LiferayGradleProject.class, bundleProject[0].getClass());
	}

	@Test
	public void testThemeProjectPluginDetection()
		throws Exception {

		NewLiferayModuleProjectOp op =
			NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("gradle-theme-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("theme");

		op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		IProject project = CoreUtil.getProject("gradle-theme-test");

		Assert.assertNotNull(project);

		Util.waitForBuildAndValidation();

		IBundleProject bundleProject =
			LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);
	}

	@Test
	public void toolingApiCustomModel()
		throws Exception {

		LiferayGradleProject gradleProject =
			Util.fullImportGradleProject("projects/customModel");

		Assert.assertNotNull(gradleProject);

		CustomModel customModel = GradleCore.getToolingModel(
			CustomModel.class, gradleProject.getProject());

		Assert.assertNotNull(customModel);

		Assert.assertFalse(customModel.hasPlugin("not.a.plugin"));

		Assert.assertTrue(
			customModel.hasPlugin("org.dm.gradle.plugins.bundle.BundlePlugin"));
	}

	@Test
	public void testAddGradleDependency()
		throws Exception {

		LiferayGradleProject gradleProject = Util.fullImportGradleProject(
			"projects/GradleDependencyTestProject");
		String[][] gradleDependencies = new String[][] {
			{
				"com.liferay.portal", "com.liferay.portal.kernel", "2.6.0"
			}
		};

		GradleDependency gd = new GradleDependency(
			gradleDependencies[0][0], gradleDependencies[0][1],
			gradleDependencies[0][2]);

		Assert.assertNotNull(gradleProject);

		IProject project = gradleProject.getProject();

		IFile gradileFile = project.getFile("build.gradle");

		GradleDependencyUpdater updater =
			new GradleDependencyUpdater(gradileFile.getLocation().toFile());

		List<GradleDependency> existDependencies = updater.getAllDependencies();

		Assert.assertFalse(existDependencies.contains(gd));

		IProjectBuilder gradleProjectBuilder =
			gradleProject.adapt(IProjectBuilder.class);

		gradleProjectBuilder.updateProjectDependency(
			project, Arrays.asList(gradleDependencies));

		GradleDependencyUpdater dependencyUpdater =
			new GradleDependencyUpdater(gradileFile.getLocation().toFile());

		List<GradleDependency> updatedDependencies =
			dependencyUpdater.getAllDependencies();

		Assert.assertTrue(updatedDependencies.contains(gd));
	}

}
