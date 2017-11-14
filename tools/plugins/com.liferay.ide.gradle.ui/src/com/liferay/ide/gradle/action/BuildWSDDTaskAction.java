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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Andy Wu
 */
public class BuildWSDDTaskAction extends GradleTaskAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			IFile gradleBuildFile = null;

			Object elem = elems[0];

			if (elem instanceof IFile) {
				gradleBuildFile = (IFile)elem;

				project = gradleBuildFile.getProject();
			}
			else if (elem instanceof IProject) {
				project = (IProject)elem;

				gradleBuildFile = project.getFile("build.gradle");
			}
		}

		IFile serviceFile = project.getFile("service.xml");

		if (!serviceFile.exists()) {
			MessageDialog.openError(
				UIUtil.getActiveShell(), "Build WSDD Error",
				"Can't find service.xml in " + project.getName() + " project");

			return;
		}

		boolean hasGradleWorkspace = false;
		boolean insideWorkspace = false;

		try {
			hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
		}
		catch (Exception e) {
			MessageDialog.openError(UIUtil.getActiveShell(), "Build WSDD Error", e.getMessage());

			return;
		}

		if (hasGradleWorkspace) {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			IPath path = workspaceProject.getLocation();

			String location = path.toPortableString();

			IPath projectLocation = project.getLocation();

			if (projectLocation.toPortableString().startsWith(location)) {
				insideWorkspace = true;
			}
		}

		if (insideWorkspace) {
			_checkInsideWorkapce(action);
		}
		else {
			_checkStandalone(action);
		}
	}

	@Override
	protected String getGradleTask() {
		return "buildWSDD";
	}

	private void _addStandaloneScript(IFile file) throws Exception {
		String separator = System.getProperty("line.separator", "\n");

		String content = FileUtil.readContents(
			BuildWSDDTaskAction.class.getResourceAsStream("/META-INF/script_standalone.gradle"));

		IPath location = file.getLocation();

		Files.write(
			location.toFile().toPath(), (separator + separator + content).getBytes(), StandardOpenOption.APPEND);

		file.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	private void _addWorkapceScript(IFile file) throws Exception {
		String originalContent = FileUtil.readContents(file.getContents());

		String content = FileUtil.readContents(
			BuildWSDDTaskAction.class.getResourceAsStream("/META-INF/script_workspace.gradle"));

		content = content.replace("{content}", originalContent);

		IPath location = file.getLocation();

		Files.write(location.toFile().toPath(), content.getBytes(), StandardOpenOption.WRITE);

		file.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	private void _checkInsideWorkapce(IAction action) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		IFile settingsFile = workspaceProject.getFile("settings.gradle");

		if (!settingsFile.exists()) {
			MessageDialog.openError(
				UIUtil.getActiveShell(), "Build WSDD Error",
				"Can't find settings.gradle in " + workspaceProject.getName() + " project");

			return;
		}

		try {
			String content = FileUtil.readContents(settingsFile.getContents());

			if (content.contains("com.liferay.gradle.plugins.wsdd.builder.WSDDBuilderPlugin") &&
				content.contains("com.liferay.gradle.plugins.service.builder.ServiceBuilderPlugin")) {

				super.run(action);
			}
			else {
				boolean result = MessageDialog.openConfirm(
					UIUtil.getActiveShell(), "Build WSDD Info",
					workspaceProject.getName() +
						" project doesn't have WSDD builder config in settings.gradle, do you want to add it and ru" +
							"n buid-wsdd ?");

				if (result) {
					_addWorkapceScript(settingsFile);

					super.run(action);
				}
			}
		}
		catch (Exception e) {
			MessageDialog.openError(UIUtil.getActiveShell(), "Build WSDD Error", e.getMessage());
		}
	}

	private void _checkStandalone(IAction action) {
		IFile buildFile = project.getFile("build.gradle");

		if (!buildFile.exists()) {
			MessageDialog.openError(
				UIUtil.getActiveShell(), "Build WSDD Error",
				"Can't find build.gradle in " + project.getName() + " project");

			return;
		}

		try {
			String content = FileUtil.readContents(buildFile.getContents());

			if (content.contains("com.liferay.gradle.plugins.wsdd.builder") &&
				content.contains("com.liferay.portal.tools.wsdd.builder")) {

				super.run(action);
			}
			else {
				boolean result = MessageDialog.openConfirm(
					UIUtil.getActiveShell(), "Build WSDD Info",
					project.getName() +
						" project doesn't have WSDD builder config in build.gradle, do you want to add it and run bu" +
							"id-wsdd ?");

				if (result) {
					_addStandaloneScript(buildFile);

					super.run(action);
				}
			}
		}
		catch (Exception e) {
			MessageDialog.openError(UIUtil.getActiveShell(), "Build WSDD Error", e.getMessage());
		}
	}

}