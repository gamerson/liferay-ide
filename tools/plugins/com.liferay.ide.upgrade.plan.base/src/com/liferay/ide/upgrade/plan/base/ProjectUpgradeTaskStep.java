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
import com.liferay.ide.upgrade.plan.base.dialog.ProjectSelectionDialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;

/**
 * @author Terry Jia
 */
public abstract class ProjectUpgradeTaskStep extends AbstractUpgradeTaskStep {

	public IStatus execute(IProgressMonitor progressMonitor) {
		ProjectSelectionDialog dialog = new ProjectSelectionDialog(
			UIUtil.getActiveShell(), getFilter(), selectAllDefault());

		if (dialog.open() == Window.OK) {
			Object[] projects = dialog.getResult();

			return execute((IProject)projects[0], progressMonitor);
		}

		return Status.CANCEL_STATUS;
	}

	protected abstract IStatus execute(IProject project, IProgressMonitor progressMonitor);

	protected ViewerFilter getFilter() {
		return null;
	}

	protected boolean selectAllDefault() {
		return false;
	}

}