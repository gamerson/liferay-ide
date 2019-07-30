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

package com.liferay.ide.project.core.samples;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.samples.internal.SampleUtil;

import java.io.File;

import java.nio.file.Files;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Terry Jia
 */
public class NewSampleOpMethods {

	public static final Status execute(NewSampleOp newSampleOp, ProgressMonitor progressMonitor) {
		Status retval = Status.createOkStatus();

		Throwable errorStack = null;

		String projectName = _getter.get(newSampleOp.getProjectName());

		Path location = _getter.get(newSampleOp.getLocation());

		String liferayVersion = _getter.get(newSampleOp.getLiferayVersion());

		String buildType = _getter.get(newSampleOp.getBuildType());

		String category = _getter.get(newSampleOp.getCategory());

		String sampleName = _getter.get(newSampleOp.getSampleName());

		try {
			java.nio.file.Path samplesCachePath = SampleUtil.getSamplesCachePath();

			String bladeRepoName = "liferay-blade-samples-" + liferayVersion;

			File bladeRepoArchive = new File(samplesCachePath.toFile(), bladeRepoName);

			File sampleFile = new File(bladeRepoArchive, buildType + "/" + category + "/" + sampleName);

			if (sampleFile.exists()) {
				File locationFile = location.toFile();

				locationFile.mkdirs();

				SamplesVisitor visitor = new SamplesVisitor();

				Files.walkFileTree(sampleFile.toPath(), visitor);

				for (java.nio.file.Path path : visitor.getPaths()) {
					File file = path.toFile();

					String fileName = file.getName();

					if (Files.isDirectory(path) && fileName.equals(sampleName)) {
						File dest = new File(locationFile, fileName);

						FileUtil.copyDir(path, dest.toPath());

						File projectFile = new File(locationFile, projectName);

						dest.renameTo(projectFile);

						Job job = new Job("") {

							@Override
							protected IStatus run(IProgressMonitor progressMonitor) {
								org.eclipse.core.runtime.Path projectLocation = new org.eclipse.core.runtime.Path(
									projectFile.getPath());

								try {
									CoreUtil.openProject(projectName, projectLocation, progressMonitor);
								}
								catch (CoreException ce) {
									return ProjectCore.createErrorStatus(ce);
								}

								return org.eclipse.core.runtime.Status.OK_STATUS;
							}

						};

						job.schedule();
					}
				}
			}
		}
		catch (Exception e) {
			errorStack = e;
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}