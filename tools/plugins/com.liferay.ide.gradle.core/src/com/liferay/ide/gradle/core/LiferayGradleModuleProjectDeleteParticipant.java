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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteArguments;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

/**
 * @author Simon Jiang
 */
public class LiferayGradleModuleProjectDeleteParticipant extends DeleteParticipant {

	public LiferayGradleModuleProjectDeleteParticipant() {
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor monitor, CheckConditionsContext context)
		throws OperationCanceledException {

		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		return new RmoveModulePostChange(_deleteProject);
	}

	@Override
	public Change createPreChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		return new RmoveModulePreChange(_deleteProject);
	}

	@Override
	public String getName() {
		return null;
	}

	public abstract class RemoveModuleChange extends Change {

		public RemoveModuleChange(IProject project) {
			deleteProject = project;
		}

		@Override
		public Object getModifiedElement() {
			return deleteProject;
		}

		@Override
		public String getName() {
			return "Remove module from workspace project watch list '" + deleteProject.getName() + "'";
		}

		@Override
		public void initializeValidationData(IProgressMonitor monitor) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		public abstract Change perform(IProgressMonitor monitor) throws CoreException;

		protected IProject deleteProject;

	}

	public class RmoveModulePostChange extends RemoveModuleChange {

		public RmoveModulePostChange(IProject project) {
			super(project);
		}

		@Override
		public Change perform(IProgressMonitor monitor) throws CoreException {
			if (_liferayWorkspaceProject == null) {
				return null;
			}

			if (ListUtil.isNotEmpty(_projectsToWatch)) {
				_liferayWorkspaceProject.watch(_projectsToWatch);
			}

			return null;
		}

	}

	public class RmoveModulePreChange extends RemoveModuleChange {

		public RmoveModulePreChange(IProject project) {
			super(project);
		}

		@Override
		public Change perform(IProgressMonitor monitor) throws CoreException {
			if (_liferayWorkspaceProject == null) {
				return null;
			}

			IProject workspaceProject = _liferayWorkspaceProject.getProject();

			if (!_projectsToWatch.contains(workspaceProject)) {
				Stream<IProject> watchStream = _projectsToWatch.stream();

				_projectsToWatch = watchStream.filter(
					project -> !project.equals(deleteProject)
				).collect(
					Collectors.toSet()
				);
			}
			else if (deleteProject.equals(workspaceProject)) {
				_projectsToWatch.remove(deleteProject);
			}

			String jobName = workspaceProject.getName() + ":watch";

			IJobManager jobManager = Job.getJobManager();

			Job[] jobs = jobManager.find(jobName);

			if (ListUtil.isNotEmpty(jobs)) {
				Job job = jobs[0];

				job.cancel();

				try {
					job.join();
				}
				catch (InterruptedException ie) {
				}
			}

			return null;
		}

	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject)) {
			return false;
		}

		DeleteArguments arguments = getArguments();

		if (!arguments.getDeleteProjectContents()) {
			return false;
		}

		_deleteProject = (IProject)element;

		try {
			boolean gradleProject = GradleUtil.isGradleProject(_deleteProject);

			if (!gradleProject) {
				return false;
			}

			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			_liferayWorkspaceProject = LiferayCore.create(IWorkspaceProject.class, workspaceProject);

			if (_liferayWorkspaceProject != null) {
				_projectsToWatch = new HashSet<>(_liferayWorkspaceProject.watching());

				_childProjects = new HashSet<>(_liferayWorkspaceProject.getChildProjects());

				if (ListUtil.contains(_projectsToWatch, workspaceProject)) {
					if (_deleteProject.equals(workspaceProject)) {
						return true;
					}

					return _childProjects.contains(_deleteProject);
				}

				return _projectsToWatch.contains(_deleteProject);
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	private Set<IProject> _childProjects;
	private IProject _deleteProject;
	private IWorkspaceProject _liferayWorkspaceProject;
	private Set<IProject> _projectsToWatch;

}