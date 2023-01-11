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

package com.liferay.ide.project.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.liferay.ide.project.core.modules.IComponentTemplate;
import com.liferay.ide.project.core.modules.LiferayComponentTemplateReader;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings("rawtypes")
public class ProjectCore extends Plugin {

	public static final String LIFERAY_PROJECT_MARKER_TYPE = "com.liferay.ide.project.core.LiferayProjectMarker";

	public static final String PLUGIN_ID = "com.liferay.ide.project.core";

	public static final String PREF_CREATE_NEW_PORLET = "create-new-portlet";

	public static final String PREF_DEFAULT_JSF_MODULE_PROJECT_BUILD_TYPE_OPTION =
		"project-jsf-module-default-build-type-option";

	public static final String PREF_DEFAULT_JSF_MODULE_PROJECT_MAVEN_GROUPID =
		"default-jsf-module-project-maven-groupid";

	public static final String PREF_DEFAULT_LIFERAY_VERSION_OPTION = "default-liferay-version-option";

	public static final String PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION =
		"project-module-fragment-default-build-type-option";

	public static final String PREF_DEFAULT_MODULE_PROJECT_BUILD_TYPE_OPTION =
		"project-module-default-build-type-option";

	public static final String PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID = "default-module-project-maven-groupid";

	public static final String PREF_DEFAULT_PLUGIN_PROJECT_BUILD_TYPE_OPTION =
		"project-plugin_default-build-type-option";

	public static final String PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID = "default-plugin-project-maven-groupid";

	// The key of default project build type for creating a new liferay plug in project

	public static final String PREF_DEFAULT_SPRING_MVC_PORTLET_PROJECT_BUILD_TYPE_OPTION =
		"project-spring-mvc-portlet-default-build-type-option";

	public static final String PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION =
		"project-workspace-default-build-type-option";

	public static final String PREF_INCLUDE_SAMPLE_CODE = "include-sample-code";

	public static final String USE_PROJECT_SETTINGS = "use-project-settings";

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(PLUGIN_ID, e);
	}

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String pluginId, Throwable t) {
		return new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message, String id) {
		return new Status(IStatus.WARNING, id, message);
	}

	public static IStatus createWarningStatus(String message, String id, Exception e) {
		return new Status(IStatus.WARNING, id, message, e);
	}

	public static IComponentTemplate getComponentTemplate(String templateName) {
		for (IComponentTemplate template : getComponentTemplates()) {
			if (templateName.equals(template.getShortName())) {
				return template;
			}
		}

		return null;
	}

	public static IComponentTemplate[] getComponentTemplates() {
		if (_componentTemplateReader == null) {
			_componentTemplateReader = new LiferayComponentTemplateReader();
		}

		return _componentTemplateReader.getComponentTemplates();
	}

	public static ProjectCore getDefault() {
		return _plugin;
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, e));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static void logWarning(String msg) {
		logError(createWarningStatus(msg));
	}


	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	private static LiferayComponentTemplateReader _componentTemplateReader;
	private static ProjectCore _plugin;
}