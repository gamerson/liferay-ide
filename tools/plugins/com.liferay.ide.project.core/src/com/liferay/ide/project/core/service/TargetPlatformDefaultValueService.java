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

package com.liferay.ide.project.core.service;

import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Terry Jia
 */
public class TargetPlatformDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String[] latestTargetPlatformVersions = WorkspaceConstants.liferayVersionsToTargetPlatformVersions.get("7.1");

		String retval = latestTargetPlatformVersions[0];

		IScopeContext[] prefContexts = {DefaultScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService prefService = Platform.getPreferencesService();

		String defaultLiferayVersion = prefService.getString(
			ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_LIFERAY_VERSION_OPTION, null, prefContexts);

		if (defaultLiferayVersion != null) {
			retval = WorkspaceConstants.liferayVersionsToTargetPlatformVersions.get(defaultLiferayVersion)[0];
		}

		return retval;
	}

}