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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.tp.LiferayTargetPlatform;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class LiferayGradleWorkspaceProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> {

	private ServiceTracker<LiferayTargetPlatform, LiferayTargetPlatform> _tracker;

	public LiferayGradleWorkspaceProjectProvider() {
		super(new Class<?>[] {IProject.class});

		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

		_tracker = new ServiceTracker<>(bundleContext, LiferayTargetPlatform.class, null);
		_tracker.open();
	}

	@Override
	public IStatus createNewProject(NewLiferayWorkspaceOp op, IProgressMonitor monitor) throws CoreException {
		IPath location = PathBridge.create(op.getLocation().content());
		String wsName = op.getWorkspaceName().toString();

		IPath wsLocation = location.append(wsName);

		StringBuilder sb = new StringBuilder();

		sb.append("-b ");
		sb.append("\"");
		sb.append(wsLocation.toFile().getAbsolutePath());
		sb.append("\" ");
		sb.append("");
		sb.append("init");

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (BladeCLIException bclie) {
			return ProjectCore.createErrorStatus(bclie);
		}

		String workspaceLocation = location.append(wsName).toPortableString();
		boolean initBundle = op.getProvisionLiferayBundle().content();
		String bundleUrl = op.getBundleUrl().content(false);

		return importProject(workspaceLocation, monitor, initBundle, bundleUrl);
	}

	@Override
	public IStatus importProject(String location, IProgressMonitor monitor, boolean initBundle, String bundleUrl) {
		try {
			final IStatus importJob = GradleUtil.importGradleProject(new File(location), monitor);

			if (!importJob.isOK() || (importJob.getException() != null)) {
				return importJob;
			}

			IPath path = new Path(location);

			IProject project = CoreUtil.getProject(path.lastSegment());

			final IFile gradlePropertiesFile = project.getFile("gradle.properties");

			String targetPlatformSetting = getTargetPlatformSetting(gradlePropertiesFile);

			if (targetPlatformSetting != null) {
				LiferayTargetPlatform targetPlatform = _tracker.getService();

				targetPlatform.setTargetDefinition(targetPlatformSetting);
				targetPlatform.createTargetPlatformProject(monitor);
			}

			if (initBundle) {
				if (bundleUrl != null) {

					String bundleUrlProp = LiferayWorkspaceUtil.LIFERAY_WORKSPACE_BUNDLE_URL + "=" + bundleUrl;

					String separator = System.getProperty("line.separator", "\n");

					String content = FileUtil.readContents(gradlePropertiesFile.getContents());

					String newContent = content + separator + bundleUrlProp;

					gradlePropertiesFile.setContents(
						new ByteArrayInputStream(newContent.getBytes()), IResource.FORCE, monitor);
				}

				GradleUtil.runGradleTask(project, "initBundle", monitor);

				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
		catch (Exception e) {
			return GradleCore.createErrorStatus("import Liferay workspace project error", e);
		}

		return Status.OK_STATUS;
	}

	private String getTargetPlatformSetting(final IFile gradlePropertiesFile) throws CoreException, IOException {
		Properties gradleProperties = new Properties();

		InputStream contents = gradlePropertiesFile.getContents(true);

		gradleProperties.load(contents);

		contents.close();

		String targetPlatformValue = gradleProperties.getProperty("liferay.workspace.target.platform");
		return targetPlatformValue;
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		ILiferayProject retval = null;

		if (adaptable instanceof IProject) {
			final IProject project = (IProject)adaptable;

			if (LiferayWorkspaceUtil.isValidWorkspace(project)) {
				return new LiferayWorkspaceProject(project);
			}
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		// TODO validation gradle project location

		return retval;
	}

}