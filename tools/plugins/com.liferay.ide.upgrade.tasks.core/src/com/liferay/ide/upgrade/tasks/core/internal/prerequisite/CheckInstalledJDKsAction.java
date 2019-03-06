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

package com.liferay.ide.upgrade.tasks.core.internal.prerequisite;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.internal.UpgradeTasksCorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {"id=check_installed_jdks", "order=1", "stepId=ensure_compatible_jdk", "title=Check Installed JDKs"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class CheckInstalledJDKsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {

		/*if (getVMInstallTypeId() == null) {
			return JavaRuntime.getDefaultVMInstall();
		}

		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());

			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();

			int size = vmInstalls.length;

			String id = getVMInstallId();

			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId())) {
					return vmInstalls[i];
				}
			}
		}
		catch (Exception e) {

			// ignore

		}*/

		return UpgradeTasksCorePlugin.createErrorStatus("no compatible jdk found");
	}

}