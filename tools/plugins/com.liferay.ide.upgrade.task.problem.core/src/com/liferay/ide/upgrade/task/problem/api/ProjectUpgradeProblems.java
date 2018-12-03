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

package com.liferay.ide.upgrade.task.problem.api;

import com.liferay.ide.upgrade.plan.api.Summary;

import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 */
public class ProjectUpgradeProblems implements Summary {

	public ProjectUpgradeProblems(IProject project, FileProblems[] fileProblems) {
		_project = project;
		_fileProblems = fileProblems;
	}

	@Override
	public String doDetail() {
		StringBuffer sb = new StringBuffer();

		sb.append(_project);
		sb.append("<br />");
		sb.append("It has " + _fileProblems.length + " file(s) need to be solved.");
		sb.append("<br />");

		for (FileProblems problem : _fileProblems) {
			sb.append(problem.getFile());
			sb.append("<br />");
		}

		return sb.toString();
	}

	@Override
	public String doLabel() {
		return _project.getName();
	}

	public FileProblems[] getFileProjects() {
		return _fileProblems;
	}

	public IProject getProject() {
		return _project;
	}

	private FileProblems[] _fileProblems;
	private IProject _project;

}