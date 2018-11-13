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

package com.liferay.ide.upgrade.ui.navigator;

import com.liferay.ide.project.core.upgrade.FileProblems;

import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 */
public class ProjectMigrationProblems {

	public ProjectMigrationProblems(IProject project, FileProblems[] fileProblems) {
		_project = project;
		_fileProblems = fileProblems;
	}

	public FileProblems[] getFileProjects() {
		return _fileProblems;
	}

	public String getLabel() {
		return "Liferay Migration Problems";
	}

	public IProject getProject() {
		return _project;
	}

	private FileProblems[] _fileProblems;
	private IProject _project;

}