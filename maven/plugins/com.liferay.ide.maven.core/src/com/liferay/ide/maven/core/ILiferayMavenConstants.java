/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.maven.core;

/**
 * @author Gregory Amerson
 */
public interface ILiferayMavenConstants
{

    String BUILD_CSS = "build-css"; //$NON-NLS-1$

    String BUILD_EXT = "build-ext"; //$NON-NLS-1$

    String BUILD_THUMBNAIL = "build-thumbnail"; //$NON-NLS-1$

    String COMPILE_SCOPE = "compile"; //$NON-NLS-1$

    String DEFAULT_PLUGIN_TYPE = "portlet"; //$NON-NLS-1$

    String EXT_PLUGIN_TYPE = "ext"; //$NON-NLS-1$

    String HOOK_PLUGIN_TYPE = "hook"; //$NON-NLS-1$

    String JSP_API_ARTIFACT_ID = "jsp-api"; //$NON-NLS-1$

    String JSP_API_GROUP_ID = "javax.servlet.jsp"; //$NON-NLS-1$

    String JSP_API_VERSION = "2.0"; //$NON-NLS-1$

    String JSP_JSTL_GROUP_ID = "javax.servlet.jsp.jstl"; //$NON-NLS-1$

    String JSTL_ARTIFACT_ID = "jstl"; //$NON-NLS-1$

    String JSTL_VERSION = "1.2"; //$NON-NLS-1$

    String LAYOUTTPL_PLUGIN_TYPE = "layouttpl"; //$NON-NLS-1$

    String LIFERAY_GROUP_ID = "com.liferay.portal"; //$NON-NLS-1$

    String LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID = LiferayMavenCore.PLUGIN_ID + ".configurationProblem";//$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_KEY = "com.liferay.maven.plugins:liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGINS_GROUP_ID = "com.liferay.maven.plugins"; //$NON-NLS-1$

    String MAVEN_GROUP_ARTIFACT_SEPERATOR = ":";//$NON-NLS-1$

    String MAVEN_PLUGIN_CONFIG_KEY = "configuration"; //$NON-NLS-1$

    String MAVEN_PROP_APPSERVER_PORTAL_TLD_DIR = "appserver.portal.tld.dir"; //$NON-NLS-1$

    String MAVEN_PROP_APPSERVER_PORTAL_DIR = "appserver.portal.dir";//$NON-NLS-1$

    String MAVEN_PROP_APPSERVER_PORTAL_LIB_DIR = "appserver.portal.lib.dir";//$NON-NLS-1$

    String MAVEN_PROP_APPSERVER_LIB_GLOBAL_DIR = "appserver.lib.global.dir";//$NON-NLS-1$

    String MAVEN_PROP_APPSERVER_DEPLOY_DIR = "appserver.deploy.dir";//$NON-NLS-1$

    String MAVEN_PROP_LIFERAY_AUTO_DEPLOY_DIR = "liferay.auto.deploy.dir";//$NON-NLS-1$

    String MAVEN_PROP_LIFERAY_VERSION = "liferay.version";//$NON-NLS-1$

    String MAVEN_PROP_LIFERAY_THEME_TYPE = "liferay.theme.type"; //$NON-NLS-1$

    String MAVEN_PROP_LIFERAY_THEME_PARENT = "liferay.theme.parent"; //$NON-NLS-1$

    String PARENT_THEME = "parentTheme"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR = "autoDeployDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_CLASSES_PORTAL_DIR = "appServerClassesPortalDir"; //$NON-NLS-1$
    String PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR = "appServerDeployDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR = "appServerLibGlobalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR = "appServerLibPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR = "appServerPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR = "appServerTldPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_LIFERAY_VERSION = "liferayVersion"; //$NON-NLS-1$

    String PLUGIN_CONFIG_PLUGIN_TYPE = "pluginType"; //$NON-NLS-1$

    String PORTAL_SERVICE_ARTIFACT_ID = "portal-service"; //$NON-NLS-1$

    String PORTLET_API_ARTIFACT_ID = "portlet-api"; //$NON-NLS-1$

    String PORTLET_API_GROUP_ID = "javax.portlet"; //$NON-NLS-1$

    String PORTLET_API_VERSION = "2.0"; //$NON-NLS-1$

    String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;

    String PROVIDED_SCOPE = "provided"; //$NON-NLS-1$

    String RUNTIME_SCOPE = "runtime"; //$NON-NLS-1$

    String SERVLET_API_ARTIFACT_ID = "servlet-api"; //$NON-NLS-1$

    String SERVLET_API_GROUP_ID = "javax.servlet"; //$NON-NLS-1$

    String SERVLET_API_VERSION = "2.4"; //$NON-NLS-1$

    String TEST_SCOPE = "test"; //$NON-NLS-1$

    String THEME_PLUGIN_TYPE = "theme"; //$NON-NLS-1$

    String THEME_TYPE = "themeType";//$NON-NLS-1$

    String UTIL_BRIDGES_ARTIFACT_ID = "util-bridges"; //$NON-NLS-1$

    String UTIL_JAVA_ARTIFACT_ID = "util-java"; //$NON-NLS-1$

    String UTIL_TAGLIB_ARTIFACT_ID = "util-taglib"; //$NON-NLS-1$

}
