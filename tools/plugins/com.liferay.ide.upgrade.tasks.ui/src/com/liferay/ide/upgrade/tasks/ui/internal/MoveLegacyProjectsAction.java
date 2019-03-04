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

package com.liferay.ide.upgrade.tasks.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.ImportSDKProjectsOp;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {"id=move_legacy_projects", "order=1", "stepId=move_legacy_projects", "title=Move Legacy Projects"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class MoveLegacyProjectsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (targetProjectLocation == null) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no target project configured for current plan.");
		}

		Path pluginsSDKLoaction = targetProjectLocation.resolve("plugins-sdk");

		if (FileUtil.notExists(pluginsSDKLoaction.toFile())) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no plugins sdk folder in " + pluginsSDKLoaction);
		}

		final AtomicInteger returnCode = new AtomicInteger();

		ImportSDKProjectsOp sdkProjectsImportOp = ImportSDKProjectsOp.TYPE.instantiate();

		UIUtil.sync(
			() -> {
				Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

				ImportSDKProjectsWizard importSDKProjectsWizard = new ImportSDKProjectsWizard(
					sdkProjectsImportOp, currentProjectLocation);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				Shell shell = workbenchWindow.getShell();

				WizardDialog wizardDialog = new WizardDialog(shell, importSDKProjectsWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			ElementList<ProjectNamedItem> projects = sdkProjectsImportOp.getSelectedProjects();

			Stream<ProjectNamedItem> stream = projects.stream();

			stream.map(
				projectNamedItem -> _getter.get(projectNamedItem.getLocation())
			).map(
				location -> Paths.get(location)
			).forEach(
				source -> {
					int beginIndex = source.getNameCount() - 2;
					int endIndex = source.getNameCount();

					Path subpath = source.subpath(beginIndex, endIndex);

					Path newLocation = pluginsSDKLoaction.resolve(subpath);

					File sourceFile = source.toFile();

					try {
						FileUtils.copyDirectory(sourceFile, newLocation.toFile());
					}
					catch (IOException ioe) {
						UpgradeTasksUIPlugin.logError(
							"Copy project " + source + " failed, please clear the folder and try again", ioe);
					}

					org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(newLocation.toString());

					try {
						IProject newProject = CoreUtil.openProject(sourceFile.getName(), path, progressMonitor);

						_addNaturesToProject(newProject, JavaCore.NATURE_ID, progressMonitor);
					}
					catch (CoreException ce) {
					}
				}
			);
		}

		return Status.OK_STATUS;
	}

	private void _addNaturesToProject(IProject project, String natureId, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = project.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + 1];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		newNatures[newNatures.length - 1] = natureId;

		description.setNatureIds(newNatures);

		project.setDescription(description, monitor);
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

	@Reference
	private UpgradePlanner _upgradePlanner;

}