/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.project.core.util;

import static com.liferay.ide.eclipse.sdk.ISDKConstants.DEFAULT_WEBCONTENT_FOLDER;
import static com.liferay.ide.eclipse.sdk.ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER;
import static com.liferay.ide.eclipse.sdk.ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER;
import static com.liferay.ide.eclipse.sdk.ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER;
import static com.liferay.ide.eclipse.sdk.ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER;
import static org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FACET_RUNTIME;

import com.liferay.ide.eclipse.core.util.ZipUtil;
import com.liferay.ide.eclipse.project.core.PluginBinaryRecord;
import com.liferay.ide.eclipse.project.core.ProjectRecord;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class ProjectImportUtil {

	/**
	 * @param dataModel
	 * @param pluginBinaryRecord
	 * @param liferaySDK
	 * @return
	 * @throws IOException
	 */
	public static ProjectRecord createPluginProject(
		IDataModel dataModel, PluginBinaryRecord pluginBinaryRecord, SDK liferaySDK ) throws IOException {
		ProjectRecord projectRecord = null;
		if ( !pluginBinaryRecord.isConflicts() ) {
			String displayName = pluginBinaryRecord.getDisplayName();
			String liferayPluginName = pluginBinaryRecord.getLiferayPluginName();
			File binaryFile = pluginBinaryRecord.getBinaryFile();
			IPath projectPath = null;
			IPath sdkPluginProjectFolder = liferaySDK.getLocation();
			BridgedRuntime bridgedRuntime = (BridgedRuntime) dataModel.getProperty( FACET_RUNTIME );

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( bridgedRuntime );
			Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );
			// Create Project
			if ( pluginBinaryRecord.isHook() ) {
				projectPath = liferaySDK.createNewHookProject( displayName, displayName );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append(HOOK_PLUGIN_PROJECT_FOLDER);
			}
			else if ( pluginBinaryRecord.isPortlet() ) {

				projectPath = liferaySDK.createNewPortletProject( displayName, displayName, appServerProperties );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( PORTLET_PLUGIN_PROJECT_FOLDER );
			}
			else if ( pluginBinaryRecord.isTheme() ) {
				projectPath = liferaySDK.createNewThemeProject( displayName, displayName );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( THEME_PLUGIN_PROJECT_FOLDER );
			}
			else if ( pluginBinaryRecord.isLayoutTpl() ) {
				projectPath = liferaySDK.createNewLayoutTplProject( displayName, displayName, appServerProperties );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( LAYOUTTPL_PLUGIN_PROJECT_FOLDER );
			}
		

			// Move the porject to Liferay SDK location
			File tempProjectDir = projectPath.append( liferayPluginName ).toFile();
			// System.out.println( "Source Dir:" + tempProjectDir.getAbsolutePath() );
			File liferayPluginDir = sdkPluginProjectFolder.toFile();
			// System.out.println( "Dest Dir:" + liferayPluginDir.getAbsolutePath() );
			File liferayPluginProjectDir = new File( liferayPluginDir, liferayPluginName );
			FileUtils.copyDirectory( tempProjectDir, liferayPluginProjectDir );

			// Extract the contents
			File docRoot = new File( liferayPluginProjectDir, DEFAULT_WEBCONTENT_FOLDER );
			ZipUtil.unzip( binaryFile, docRoot );

			projectRecord = new ProjectRecord( liferayPluginProjectDir );

		}
		return projectRecord;

	}
}
import static org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FACET_RUNTIME;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import com.liferay.ide.eclipse.core.util.ZipUtil;
import com.liferay.ide.eclipse.project.core.PluginBinaryRecord;
import com.liferay.ide.eclipse.project.core.ProjectRecord;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class ProjectImportUtil {

	/**
	 * @param dataModel
	 * @param pluginBinaryRecord
	 * @param liferaySDK
	 * @return
	 * @throws IOException
	 */
	public static ProjectRecord createPluginProject(
		IDataModel dataModel, PluginBinaryRecord pluginBinaryRecord, SDK liferaySDK ) throws IOException {
		ProjectRecord projectRecord = null;
		if ( !pluginBinaryRecord.isConflicts() ) {
			String displayName = pluginBinaryRecord.getDisplayName();
			String liferayPluginName = pluginBinaryRecord.getLiferayPluginName();
			File binaryFile = pluginBinaryRecord.getBinaryFile();
			IPath projectPath = null;
			IPath sdkPluginProjectFolder = liferaySDK.getLocation();
			BridgedRuntime bridgedRuntime = (BridgedRuntime) dataModel.getProperty( FACET_RUNTIME );

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( bridgedRuntime );
			Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );
			// Create Project
			if ( pluginBinaryRecord.isHook() ) {
				projectPath = liferaySDK.createNewHookProject( displayName, displayName );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( "hooks" );
			}
			else if ( pluginBinaryRecord.isPortlet() ) {

				projectPath = liferaySDK.createNewPortletProject( displayName, displayName, appServerProperties );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( "portlets" );
			}
			else if ( pluginBinaryRecord.isTheme() ) {
				projectPath = liferaySDK.createNewThemeProject( displayName, displayName );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( "themes" );
			}
			else if ( pluginBinaryRecord.isLayoutTpl() ) {
				projectPath = liferaySDK.createNewLayoutTplProject( displayName, displayName, appServerProperties );
				sdkPluginProjectFolder = sdkPluginProjectFolder.append( "layouttpl" );
			}
			System.out.println( "Project path:" + projectPath );
			System.out.println( "SDK Project Folder :" + sdkPluginProjectFolder.toOSString() );

			// Move the porject to Liferay SDK location
			File tempProjectDir = projectPath.append( liferayPluginName ).toFile();
			// System.out.println( "Source Dir:" + tempProjectDir.getAbsolutePath() );
			File liferayPluginDir = sdkPluginProjectFolder.toFile();
			// System.out.println( "Dest Dir:" + liferayPluginDir.getAbsolutePath() );
			File liferayPluginProjectDir = new File( liferayPluginDir, liferayPluginName );
			FileUtils.copyDirectory( tempProjectDir, liferayPluginProjectDir );

			// Extract the contents
			File docRoot = new File( liferayPluginProjectDir, "docroot" );
			ZipUtil.unzip( binaryFile, docRoot );

			projectRecord = new ProjectRecord( liferayPluginProjectDir );

		}
		return projectRecord;

	}
}
