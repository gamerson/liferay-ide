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

package com.liferay.ide.core.event;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Charles Wu
 */
public class ProjectsChangeEvent {

	public ProjectsChangeEvent(IProject project, Set<IPath> affectedResources) {
		_project = project;
		_affectedResources = affectedResources;
	}

	public Set<IPath> getAffectedResources() {
		return _affectedResources;
	}

	public IProject getProject() {
		return _project;
	}

	private final Set<IPath> _affectedResources;
	private final IProject _project;

}