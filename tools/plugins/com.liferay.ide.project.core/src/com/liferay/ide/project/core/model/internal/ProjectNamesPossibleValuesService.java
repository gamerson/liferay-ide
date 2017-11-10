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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class ProjectNamesPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		for (IProject project : CoreUtil.getAllProjects()) {
			if ((project != null) && project.isAccessible()) {
				values.add(project.getName());
			}
		}
	}

}