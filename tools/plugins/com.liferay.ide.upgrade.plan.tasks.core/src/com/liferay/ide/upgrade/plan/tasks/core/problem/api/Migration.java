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

package com.liferay.ide.upgrade.plan.tasks.core.problem.api;

import com.liferay.ide.upgrade.plan.core.Problem;

import java.io.File;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface Migration {

	public List<Problem> findProblems(File projectDir, IProgressMonitor monitor);

	public List<Problem> findProblems(File projectDir, List<String> versions, IProgressMonitor monitor);

	public List<Problem> findProblems(Set<File> files, IProgressMonitor monitor);

	public List<Problem> findProblems(Set<File> files, List<String> versions, IProgressMonitor monitor);

	public int DETAIL_LONG = 1 << 2;

	public int DETAIL_SHORT = 1 << 1;

}