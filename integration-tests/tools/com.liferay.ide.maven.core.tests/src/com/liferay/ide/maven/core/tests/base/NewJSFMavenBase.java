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

package com.liferay.ide.maven.core.tests.base;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.test.project.core.base.NewModuleOpBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.JobHelpers.IJobMatcher;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public abstract class NewJSFMavenBase extends NewModuleOpBase<NewLiferayJSFModuleProjectOp> {

	@Override
	protected String provider() {
		return "maven-jsf";
	}

	@Override
	protected void verifyProject(IProject project) {
		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);

		IPath outputBundle;

		try {
			outputBundle = bundleProject.getOutputBundle(true, npm);

			assertFileExists(outputBundle);

			assertFileSuffix(outputBundle, shape());
		}
		catch (CoreException ce) {
			failTest(ce);
		}
	}

	protected void verifyProjectFiles(String projectName) {
		assertProjectFileNotExists(projectName, "build.gradle");
		assertProjectFileExists(projectName, "pom.xml");
	}

	@Override
	protected void waitForBuildAndValidation() {
		JobHelpers.waitForJobs(BuildJobMatcher.INSTANCE, 5 * 60 * 1000);
	}

	private static class BuildJobMatcher implements IJobMatcher {

		public static final IJobMatcher INSTANCE = new BuildJobMatcher();

		public boolean matches(Job job) {
			Class<?> clazz = job.getClass();

			String className = clazz.getName();

			if ((job instanceof WorkspaceJob) || className.matches("(.*\\.AutoBuild.*)") ||
				className.endsWith("JREUpdateJob")) {

				return true;
			}

			return false;
		}

	}

}