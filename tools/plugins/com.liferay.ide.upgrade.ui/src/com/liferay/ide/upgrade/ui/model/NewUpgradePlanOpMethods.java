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

package com.liferay.ide.upgrade.ui.model;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.upgrade.ui.importer.ImportedProjectImporter;
import com.liferay.ide.upgrade.ui.importer.Importer;
import com.liferay.ide.upgrade.ui.importer.LiferayWorkspaceGradleImporter;
import com.liferay.ide.upgrade.ui.importer.LiferayWorkspaceGradleWithSDKImporter;
import com.liferay.ide.upgrade.ui.util.UpgradeUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Terry Jia
 */
public class NewUpgradePlanOpMethods {

	public static final Status execute(NewUpgradePlanOp op, ProgressMonitor progressMonitor) {
		IPath location = PathBridge.create(SapphireUtil.getContent(op.getLocation()));

		Importer importer = null;

		if (UpgradeUtil.isAlreadyImported(location)) {
			importer = new ImportedProjectImporter(location);
		}
		else if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(location)) {
			importer = new LiferayWorkspaceGradleImporter(location);
		}
		else if (SDKUtil.isValidSDKLocation(location)) {
			importer = new LiferayWorkspaceGradleWithSDKImporter(location);
		}
		else {
			return Status.createErrorStatus("Unsupported project type");
		}

		final Importer importer2 = importer;

		Job importJob = new Job("Importing project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				importer2.doBefore(monitor);

				importer2.doImport(monitor);

				return org.eclipse.core.runtime.Status.OK_STATUS;
			}

		};

		importJob.schedule();

		// TODO need to generate Upgrade Plan here

		return Status.createOkStatus();
	}

}