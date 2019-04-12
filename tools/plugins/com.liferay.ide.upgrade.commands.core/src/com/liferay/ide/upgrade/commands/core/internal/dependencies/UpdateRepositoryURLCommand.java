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

package com.liferay.ide.upgrade.commands.core.internal.dependencies;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.commands.core.dependencies.UpdateRepositoryURLCommandKeys;
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = "id=" + UpdateRepositoryURLCommandKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class UpdateRepositoryURLCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		IProject project = projects.get(0);

		File settingsGradle = FileUtil.getFile(project.getFile("settings.gradle"));

		String contents = FileUtil.readContents(settingsGradle, true);

		String oldRepositoryURLPrefix = "https://repository-cdn.liferay.com/";

		if (contents.contains(oldRepositoryURLPrefix)) {
			contents = contents.replaceAll(oldRepositoryURLPrefix, "https://repository-cdn.liferay.com/");

			try {
				FileUtil.writeFile(settingsGradle, contents, project.getName());
			}
			catch (CoreException ce) {
			}
		}
		else {
			_messagePrompt.postInfo("No need to update", "There is no need to update for this project.");

			return Status.CANCEL_STATUS;
		}

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(project)));

		return Status.OK_STATUS;
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}