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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.FileUpgradeProblem;
import com.liferay.ide.upgrade.problems.core.UpgradeProblemsCorePlugin;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.core.XMLMemento;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {"id=find_upgrade_problems", "order=1", "stepId=find_upgrade_problems", "title=Find Upgrade Problems"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
@SuppressWarnings("restriction")
public class FindUpgradeProblemsTaskStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		List<IProject> projects = _resourceSelection.selectProjects("select projects", true);

		List<String> versions = new ArrayList<>();

		versions.add("7.0");
		versions.add("7.1");

		Job job = new Job("Finding migration problems...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				XMLMemento root = XMLMemento.createWriteRoot("upgradeProblems");

				String upgradePlanName = _upgradePlanner.getCurrentUpgradePlanName();

				root.putString("upgradePlanName", _upgradePlanner.getCurrentUpgradePlanName());

				Stream<IProject> stream = projects.stream();

				stream.forEach(
					project -> {
						File searchFile = FileUtil.getFile(project);

						List<FileUpgradeProblem> fileUpgradeProblems = _fileMigration.findProblems(
							searchFile, versions, monitor);

						if (!fileUpgradeProblems.isEmpty()) {
							_storeInMemento(root, project.getName(), fileUpgradeProblems);
						}
					});

				try {
					UpgradeProblemsCorePlugin upgradeProblemsCorePlugin = UpgradeProblemsCorePlugin.getDefault();

					IPath storageLocation = upgradeProblemsCorePlugin.getStateLocation();

					IPath storagePath = storageLocation.append(upgradePlanName + ".xml");

					FileWriter fileWriter = new FileWriter(storagePath.toFile());

					root.save(fileWriter);
				}
				catch (IOException ioe) {
				}

				_upgradePlanner.dispatch(new UpgradeTaskStepActionDoneEvent(FindUpgradeProblemsTaskStepAction.this));

				return Status.OK_STATUS;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

	private void _storeInMemento(
		XMLMemento xmlMemento, String projectName, List<FileUpgradeProblem> fileUpgradeProblems) {

		XMLMemento projectNode = xmlMemento.createChild("project");

		projectNode.putString("projectName", projectName);

		Map<File, List<FileUpgradeProblem>> fileProblemsMap = new HashMap<>();

		for (FileUpgradeProblem fileUpgradeProblem : fileUpgradeProblems) {
			File file = fileUpgradeProblem.getFile();

			if (fileProblemsMap.containsKey(file)) {
				List<FileUpgradeProblem> fileUpgradeProblemsList = fileProblemsMap.get(file);

				fileUpgradeProblemsList.add(fileUpgradeProblem);
			}
			else {
				List<FileUpgradeProblem> fileUpgradeProblemsList = new ArrayList<>();

				fileUpgradeProblemsList.add(fileUpgradeProblem);

				fileProblemsMap.put(file, fileUpgradeProblemsList);
			}
		}

		for (Entry<File, List<FileUpgradeProblem>> entry : fileProblemsMap.entrySet()) {
			XMLMemento fileUpgradeProblemNode = projectNode.createChild("file");

			File file = entry.getKey();

			fileUpgradeProblemNode.putString("filePath", file.getPath());

			List<FileUpgradeProblem> problems = entry.getValue();

			for (FileUpgradeProblem problem : problems) {
				XMLMemento problemNode = projectNode.createChild("problem");

				problemNode.putString("autoCorrectContext", problem.getAutoCorrectContext());

				File problemFile = problem.getFile();

				problemNode.putString("filePath", problemFile.getPath());

				problemNode.putInteger("endOffset", problem.getEndOffset());
				problemNode.putString("markerId", String.valueOf(problem.getMarkerId()));
				problemNode.putInteger("markerType", problem.getMarkerType());
				problemNode.putInteger("number", problem.getNumber());
				problemNode.putInteger("startOffset", problem.getStartOffset());
				problemNode.putInteger("status", problem.getStatus());
				problemNode.putString("summary", problem.getSummary());
				problemNode.putInteger("lineNumber", problem.getLineNumber());
				problemNode.putString("ticket", problem.getTicket());
				problemNode.putString("title", problem.getTitle());
				problemNode.putString("type", problem.getType());
				problemNode.putString("uuid", problem.getUuid());
				problemNode.putString("version", problem.getVersion());
			}
		}
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}