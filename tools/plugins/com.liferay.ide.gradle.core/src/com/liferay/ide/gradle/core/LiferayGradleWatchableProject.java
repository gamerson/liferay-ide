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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.IWatchableProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleWatchableProject extends LiferayGradleProject implements IWatchableProject {

	public LiferayGradleWatchableProject(IProject project) {
		super(project);
	}

	@Override
	public boolean enable() {
		IFile buildFile = getProject().getFile("build.gradle");

		boolean watchable = GradleUtil.isWatchable(buildFile);

		if (watchable) {
			IEclipsePreferences projectScope = new ProjectScope(getProject()).getNode(ProjectCore.PLUGIN_ID);

			return projectScope.getBoolean("enableWatch", true);
		}

		return false;
	}
	
	@Override
	public void unwatch() {
		Job[] jobs = Job.getJobManager().find(_watch_job_name);

		if (ListUtil.isNotEmpty(jobs)) {
			jobs[0].cancel();
		}
	}

	@Override
	public void watch() {
		Job[] jobs = Job.getJobManager().find(_watch_job_name);

		if (ListUtil.isNotEmpty(jobs)) {
			return;
		}

		Job job = new Job(_watch_job_name) {

			public boolean belongsTo(Object family) {
				return _watch_job_name.equals(family);
			}

			@Override
			public IStatus run(IProgressMonitor monitor) {
				IStatus status = Status.OK_STATUS;

				try {
					// Do not use this way as the buildship issue but need to add it back when it be fixed, see:
					// https://github.com/eclipse/buildship/issues/703

					// GradleUtil.runGradleTask(getProject(), new String[] {
					// "watch"
					// }, new String[] {
					// "--continuous"
					// }, monitor);

					ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

					ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(
						"org.eclipse.ui.externaltools.ProgramLaunchConfigurationType");

					IPath basedirLocation = getProject().getLocation();

					String newName = launchManager.generateLaunchConfigurationName(basedirLocation.lastSegment());

					ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, newName);

					String gwFileName = "gradlew";

					if (CoreUtil.isWindows()) {
						gwFileName = "gradlew.bat";
					}

					workingCopy.setAttribute(
						"org.eclipse.ui.externaltools.ATTR_LOCATION", basedirLocation.append(gwFileName).toOSString());
					workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS", "watch --continuous");
					workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", basedirLocation.toOSString());

				
					workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", Boolean.FALSE);
					workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", Boolean.FALSE);
					workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", Boolean.FALSE);

					workingCopy.launch("run", monitor);
				}
				catch (CoreException e) {
					status = GradleCore.createErrorStatus(e);
				}

				return status;
			}

		};

		job.setSystem(true);

		job.schedule();
	}

	private final String _watch_job_name = "watching on " + getProject().getName();

}