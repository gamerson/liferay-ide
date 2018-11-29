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

package com.liferay.ide.upgrade.task.problem.steps;

import com.liferay.ide.project.core.upgrade.BreakingChangeSelectedProject;
import com.liferay.ide.project.core.upgrade.MigrationProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.AbstractUpgradeTaskStep;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/RemovePreviousResultStep.properties", service = UpgradeTaskStep.class)
public class RemovePreviousResultStep extends AbstractUpgradeTaskStep {

	@Override
	public IStatus execute(IProgressMonitor progressMonitor) {
		boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
			UIUtil.getActiveShell(), "Remove Previous Result?", "All previous results will be deleted.");

		if (openNewLiferayProjectWizard) {
			try {
				MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
					MigrationProblemsContainer.class);

				if (container != null) {
					UpgradeAssistantSettingsUtil.setObjectToStore(MigrationProblemsContainer.class, null);
				}

				BreakingChangeSelectedProject selectedProject = UpgradeAssistantSettingsUtil.getObjectFromStore(
					BreakingChangeSelectedProject.class);

				if (selectedProject != null) {
					UpgradeAssistantSettingsUtil.setObjectToStore(BreakingChangeSelectedProject.class, null);
				}

				IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

				if (projectExplorer != null) {
					CommonNavigator navigator = (CommonNavigator)projectExplorer;

					CommonViewer commonViewer = navigator.getCommonViewer();

					commonViewer.refresh();
				}
			}
			catch (IOException ioe) {
				ProjectUI.logError(ioe);
			}
		}

		return Status.OK_STATUS;
	}

}