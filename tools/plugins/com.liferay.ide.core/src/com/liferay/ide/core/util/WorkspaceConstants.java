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

package com.liferay.ide.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Terry Jia
 */
@SuppressWarnings("serial")
public class WorkspaceConstants {

	public static final String BUNDLE_ARTIFACT_NAME_PROPERTY = "liferay.workspace.bundle.artifact.name";

	public static final String BUNDLE_URL_CE_7_0 =
		"https://releases-cdn.liferay.com/portal/7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip";

	public static final String BUNDLE_URL_CE_7_1 =
		"https://releases-cdn.liferay.com/portal/7.1.3-ga4/liferay-ce-portal-tomcat-7.1.3-ga4-20190508171117552.tar.gz";

	public static final String BUNDLE_URL_CE_7_2 =
		"https://releases-cdn.liferay.com/portal/7.2.1-ga2/liferay-ce-portal-tomcat-7.2.1-ga2-20191111141448326.tar.gz";

	public static final String BUNDLE_URL_PROPERTY = "liferay.workspace.bundle.url";

	public static final String DEFAULT_BUNDLE_ARTIFACT_NAME = "portal-tomcat-bundle";

	public static final String DEFAULT_EXT_DIR = "ext";

	public static final String DEFAULT_HOME_DIR = "bundles";

	public static final String DEFAULT_MODULES_DIR = "modules";

	public static final String DEFAULT_PLUGINS_SDK_DIR = "plugins-sdk";

	public static final String DEFAULT_THEMES_DIR = "themes";

	public static final String DEFAULT_WARS_DIR = "wars";

	public static final String EXT_DIR_PROPERTY = "liferay.workspace.ext.dir";

	public static final String HOME_DIR_PROPERTY = "liferay.workspace.home.dir";

	public static final String LIFERAY_HOME_PROPERTY = "liferayHome";

	public static final String[] LIFERAY_VERSIONS = {"7.0", "7.1", "7.2", "7.3"};

	public static final String MODULES_DIR_PROPERTY = "liferay.workspace.modules.dir";

	public static final String PLUGINS_SDK_DIR_PROPERTY = "liferay.workspace.plugins.sdk.dir";

	public static final String[] SPRING_FRAMEWORK = {"Portlet MVC For Spring", "Spring Portlet MVC"};

	public static final String[] SPRING_FRAMEWORK_DEPENDENCIES = {"Embedded", "Provided"};

	public static final String[] SPRING_VIEW_TYPE = {"Jsp", "Thymeleaf"};

	public static final String TARGET_PLATFORM_INDEX_SOURCES_PROPERTY = "target.platform.index.sources";

	public static final String TARGET_PLATFORM_VERSION_PROPERTY = "liferay.workspace.target.platform.version";

	public static final String THEMES_DIR_PROPERTY = "liferay.workspace.themes.dir";

	public static final String WARS_DIR_PROPERTY = "liferay.workspace.wars.dir";

	public static final Map<String, String[]> liferayTargetPlatformVersions = new HashMap<String, String[]>() {
		{
			put("7.2", new String[] {"7.2.1", "7.2.0"});
			put("7.1", new String[] {"7.1.3", "7.1.2", "7.1.1", "7.1.0"});
			put("7.0", new String[] {"7.0.6"});
		}
	};
	public static final Map<String, String> springDependenciesInjectors = new HashMap<String, String>() {
		{
			put("DS", new String("ds"));
			put("Spring", new String("spring"));
		}
	};
	public static final Map<String, String> springFrameworkDependeices = new HashMap<String, String>() {
		{
			put(SPRING_FRAMEWORK_DEPENDENCIES[0], new String("embedded"));
			put(SPRING_FRAMEWORK_DEPENDENCIES[1], new String("provided"));
		}
	};
	public static final Map<String, String> springFrameworks = new HashMap<String, String>() {
		{
			put(SPRING_FRAMEWORK[0], new String("portletmvc4spring"));
			put(SPRING_FRAMEWORK[1], new String("springportletmvc"));
		}
	};
	public static final Map<String, String> springViewTypes = new HashMap<String, String>() {
		{
			put(SPRING_VIEW_TYPE[0], new String("jsp"));
			put(SPRING_VIEW_TYPE[1], new String("thymeleaf"));
		}
	};

}