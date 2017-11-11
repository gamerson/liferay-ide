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

package com.liferay.ide.project.core.model.internal;

import static com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods.supportsTypePlugin;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class SDKLocationValidationService extends ValidationService {

	@Override
	public void dispose() {
		NewLiferayPluginProjectOp op = _op();

		op.property(NewLiferayPluginProjectOp.PROP_PROJECT_NAME).detach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK).detach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PLUGIN_TYPE).detach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER).detach(_listener);

		super.dispose();
	}

	@Override
	protected Status compute() {
		NewLiferayPluginProjectOp op = _op();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content();

		if (!provider.getShortName().equals("ant")) {
			return Status.createOkStatus();
		}

		int countPossibleWorkspaceSDKProjects = SDKUtil.countPossibleWorkspaceSDKProjects();

		if (countPossibleWorkspaceSDKProjects > 1) {
			return StatusBridge.create(ProjectCore.createErrorStatus("This workspace has more than one SDK."));
		}

		Path sdkLocation = op.getSdkLocation().content(true);

		if ((sdkLocation == null) || sdkLocation.isEmpty()) {
			return StatusBridge.create(ProjectCore.createErrorStatus("This sdk location is empty."));
		}

		SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));

		if (sdk != null) {
			IStatus status = sdk.validate(true);

			if (!status.isOK()) {
				return StatusBridge.create(status);
			}
		}
		else {
			return StatusBridge.create(ProjectCore.createErrorStatus("This sdk location is not correct."));
		}

		Path projectLocation = op.getLocation().content();

		String projectName = op.getProjectName().content();

		IPath projectPath = PathBridge.create(projectLocation);

		if (FileUtil.exists(projectPath)) {
			return StatusBridge.create(
				ProjectCore.createErrorStatus(
					"Project(" + projectName + ") is existed in sdk folder, please set new project name."));
		}

		PluginType pluginType = op.getPluginType().content();

		if (pluginType.equals(PluginType.web) && !supportsTypePlugin(op, "web")) {
			StringBuilder sb = new StringBuilder();

			sb.append("The selected Plugins SDK does not support creating new web type plugins. ");
			sb.append("");
			sb.append("Please configure version 7.0 or greater.");

			return Status.createErrorStatus(sb.toString());
		}
		else if (pluginType.equals(PluginType.theme) && !supportsTypePlugin(op, "theme")) {
			StringBuilder sb = new StringBuilder();

			sb.append("The selected Plugins SDK does not support creating theme type plugins. ");
			sb.append("");
			sb.append("Please configure version 6.2 or less or using gulp way.");

			return Status.createErrorStatus(sb.toString());
		}
		else if (pluginType.equals(PluginType.portlet)) {
			IPortletFramework portletFramework = op.getPortletFramework().content();

			Version requiredVersion = new Version(portletFramework.getRequiredSDKVersion());

			Version sdkVersion = new Version(sdk.getVersion());

			if (CoreUtil.compareVersions(requiredVersion, sdkVersion) > 0) {
				StringBuilder sb = new StringBuilder();

				sb.append("Selected portlet framework requires SDK version at least ");
				sb.append("");
				sb.append(requiredVersion);

				return Status.createErrorStatus(sb.toString());
			}
		}
		else if (pluginType.equals(PluginType.ext) && !supportsTypePlugin(op, "ext")) {
			StringBuilder sb = new StringBuilder();

			sb.append("The selected Plugins SDK does not support creating ext type plugins. ");
			sb.append("");
			sb.append("Please try to confirm whether sdk has ext folder.");

			return Status.createErrorStatus(sb.toString());
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		op.property(NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER).attach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PROJECT_NAME).attach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK).attach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PLUGIN_TYPE).attach(_listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}