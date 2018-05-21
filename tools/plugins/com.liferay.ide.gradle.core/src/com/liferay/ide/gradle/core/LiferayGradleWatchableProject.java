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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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
			ScopedPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(getProject()), ProjectCore.PLUGIN_ID);

			boolean contains = store.contains("enableWatch");

			if (contains) {
				return store.getBoolean("enableWatch");
			}
			else {
				return true;
			}
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
					GradleUtil.runGradleTask(getProject(), new String[] {
						"watch"
					}, new String[] {
						"--continuous"
					}, monitor);
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