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

package com.liferay.ide.upgrade.plan.base;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.base.dialog.ProjectsSelectionDialog;

import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;

/**
 * @author Terry Jia
 */
public abstract class ProjectsUpgradeTaskStep extends AbstractUpgradeTaskStep {

	public void execute(IProgressMonitor progressMonitor) {
		ProjectsSelectionDialog dialog = new ProjectsSelectionDialog(UIUtil.getActiveShell(), getFilter());

		if (dialog.open() == Window.OK) {
			IProject[] projects = Stream.of(
				dialog.getResult()
			).toArray(
				IProject[]::new
			);

			execute(projects, progressMonitor);
		}
	}

	protected abstract void execute(IProject[] projects, IProgressMonitor progressMonitor);

	protected ViewerFilter getFilter() {
		return null;
	}

}