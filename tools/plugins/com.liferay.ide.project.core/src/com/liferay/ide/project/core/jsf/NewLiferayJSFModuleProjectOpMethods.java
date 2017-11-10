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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectOpMethods {

	public static final Status execute(NewLiferayJSFModuleProjectOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Creating Liferay JSF project (this process may take several minutes)", 100);

		Status retval = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> projectProvider = op.getProjectProvider().content();

			IStatus status = projectProvider.createNewProject(op, monitor);

			retval = StatusBridge.create(status);

			if (retval.ok()) {
				_updateBuildPrefs(op);
			}
		}
		catch (Exception e) {
			String msg = "Error creating Liferay module project.";

			ProjectCore.logError(msg, e);

			return Status.createErrorStatus(msg + " " + e.getMessage(), e);
		}

		return retval;
	}

	private static void _updateBuildPrefs(NewLiferayJSFModuleProjectOp op) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			prefs.put(ProjectCore.PREF_DEFAULT_JSF_MODULE_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text());

			prefs.flush();
		}
		catch (Exception e) {
			String msg = "Error updating default project build type.";

			ProjectCore.logError(msg, e);
		}
	}

}