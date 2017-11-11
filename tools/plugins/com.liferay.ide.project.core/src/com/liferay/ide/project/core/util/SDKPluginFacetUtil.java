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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.sdk.core.ISDKConstants;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class SDKPluginFacetUtil {

	public static final IProjectFacet[] LIFERAY_FACETS = {
		IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET, IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET,
		IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET, IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET,
		IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET, IPluginFacetConstants.LIFERAY_WEB_PROJECT_FACET
	};

	public static void configureJavaFacet(
		IFacetedProjectWorkingCopy fpjwc, IProjectFacet requiredFacet, IPreset preset, ProjectRecord projectRecord) {

		Action action = fpjwc.getProjectFacetAction(requiredFacet);

		if (action == null) {
			return;
		}

		Object config = action.getConfig();

		if (!(config instanceof JavaFacetInstallConfig)) {
			return;
		}

		JavaFacetInstallConfig javaConfig = (JavaFacetInstallConfig)config;

		IDataModel dm = (IDataModel)Platform.getAdapterManager().getAdapter(config, IDataModel.class);
		String presetId = preset.getId();

		if (presetId.contains("portlet")) {
			javaConfig.setSourceFolder(new Path(IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER));
			javaConfig.setDefaultOutputFolder(new Path(IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER));

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
				IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
				IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
		}
		else if (presetId.contains("hook")) {
			javaConfig.setSourceFolder(new Path(IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER));
			javaConfig.setDefaultOutputFolder(new Path(IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER));

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
				IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
				IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
		}
		else if (presetId.contains("layouttpl")) {
			_removeSrcFolders(dm, javaConfig);
		}
		else if (presetId.contains("theme")) {
			IPath projectPath = projectRecord.getProjectLocation();

			IPath existingSrcFolder = projectPath.append(IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

			if (existingSrcFolder.toFile().exists()) {
				javaConfig.setSourceFolder(new Path(IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER));
				javaConfig.setDefaultOutputFolder(
					new Path(IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER));

				dm.setStringProperty(
					IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

				dm.setStringProperty(
					IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
			}
			else {
				_removeSrcFolders(dm, javaConfig);
			}
		}
		else if (presetId.contains("web")) {
			javaConfig.setSourceFolder(new Path(IPluginFacetConstants.WEB_PLUGIN_SDK_SOURCE_FOLDER));
			javaConfig.setDefaultOutputFolder(new Path(IPluginFacetConstants.WEB_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER));

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
				IPluginFacetConstants.WEB_PLUGIN_SDK_SOURCE_FOLDER);

			dm.setStringProperty(
				IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
				IPluginFacetConstants.WEB_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
		}
	}

	public static void configureProjectAsRuntimeProject(
			IFacetedProjectWorkingCopy fpjwc, IRuntime runtime, String pluginType, String sdkLocation,
			ProjectRecord projectRecord)
		throws CoreException {

		fpjwc.setTargetedRuntimes(Collections.<IRuntime>emptySet());

		if (runtime != null) {
			fpjwc.setTargetedRuntimes(Collections.singleton(runtime));
		}

		fpjwc.setPrimaryRuntime(runtime);

		// fpjwc.setSelectedPreset(
		// FacetedProjectFramework.DEFAULT_CONFIGURATION_PRESET_ID );

		IFacetedProjectTemplate template = getLiferayTemplateForProject(pluginType);
		IPreset preset = getLiferayPresetForProject(pluginType);

		if (preset == null) {
			throw new CoreException(
				ProjectCore.createErrorStatus(NLS.bind(Msgs.noFacetPreset, fpjwc.getProjectName())));
		}

		Set<IProjectFacetVersion> currentProjectFacetVersions = fpjwc.getProjectFacets();

		Set<IProjectFacet> requiredFacets = template.getFixedProjectFacets();

		for (IProjectFacet requiredFacet : requiredFacets) {
			boolean hasRequiredFacet = false;

			for (IProjectFacetVersion currentFacetVersion : currentProjectFacetVersions) {
				if (currentFacetVersion.getProjectFacet().equals(requiredFacet)) {
					boolean supports = runtime.supports(currentFacetVersion);
					boolean requiredVersion = _isRequiredVersion(currentFacetVersion);

					if (supports && requiredVersion) {
						hasRequiredFacet = true;
					}
					else {
						fpjwc.removeProjectFacet(currentFacetVersion);
					}

					break;
				}
			}

			if (!hasRequiredFacet) {
				IProjectFacetVersion requiredFacetVersion = _getRequiredFacetVersionFromPreset(requiredFacet, preset);

				if (requiredFacetVersion != null) {
					fpjwc.addProjectFacet(requiredFacetVersion);

					if (ProjectUtil.isJavaFacet(requiredFacetVersion)) {
						configureJavaFacet(fpjwc, requiredFacetVersion.getProjectFacet(), preset, projectRecord);
					}
					else if (ProjectUtil.isDynamicWebFacet(requiredFacetVersion)) {
						configureWebFacet(fpjwc, requiredFacetVersion.getProjectFacet(), preset);
					}
				}
			}
			else {
				if (ProjectUtil.isJavaFacet(requiredFacet)) {
					configureJavaFacet(fpjwc, requiredFacet, preset, projectRecord);
				}
				else if (ProjectUtil.isDynamicWebFacet(requiredFacet)) {
					configureWebFacet(fpjwc, requiredFacet, preset);
				}
			}
		}
	}

	public static void configureProjectAsSDKProject(
			IFacetedProjectWorkingCopy fpjwc, String pluginType, String sdkLocation, ProjectRecord projectRecord)
		throws CoreException {

		IFacetedProjectTemplate template = getLiferayTemplateForProject(pluginType);
		IPreset preset = getLiferayPresetForProject(pluginType);

		if (preset == null) {
			throw new CoreException(
				ProjectCore.createErrorStatus(NLS.bind(Msgs.noFacetPreset, fpjwc.getProjectName())));
		}

		IRuntime primaryRuntime = fpjwc.getPrimaryRuntime();

		if (primaryRuntime != null) {
			fpjwc.removeTargetedRuntime(primaryRuntime);
		}

		Set<IProjectFacetVersion> currentProjectFacetVersions = fpjwc.getProjectFacets();

		Set<IProjectFacet> requiredFacets = template.getFixedProjectFacets();

		for (IProjectFacet requiredFacet : requiredFacets) {
			boolean hasRequiredFacet = false;

			for (IProjectFacetVersion currentFacetVersion : currentProjectFacetVersions) {
				if (currentFacetVersion.getProjectFacet().equals(requiredFacet)) {

					// TODO how to check the bundle support status?

					boolean requiredVersion = _isRequiredVersion(currentFacetVersion);

					if (requiredVersion) {
						hasRequiredFacet = true;
					}
					else {
						fpjwc.removeProjectFacet(currentFacetVersion);
					}

					break;
				}
			}

			if (!hasRequiredFacet) {
				IProjectFacetVersion requiredFacetVersion = _getRequiredFacetVersionFromPreset(requiredFacet, preset);

				if (requiredFacetVersion != null) {
					fpjwc.addProjectFacet(requiredFacetVersion);

					if (ProjectUtil.isJavaFacet(requiredFacetVersion)) {
						configureJavaFacet(fpjwc, requiredFacetVersion.getProjectFacet(), preset, projectRecord);
					}
					else if (ProjectUtil.isDynamicWebFacet(requiredFacetVersion)) {
						configureWebFacet(fpjwc, requiredFacetVersion.getProjectFacet(), preset);
					}
				}
			}
			else {
				if (ProjectUtil.isJavaFacet(requiredFacet)) {
					configureJavaFacet(fpjwc, requiredFacet, preset, projectRecord);
				}
				else if (ProjectUtil.isDynamicWebFacet(requiredFacet)) {
					configureWebFacet(fpjwc, requiredFacet, preset);
				}
			}
		}
	}

	public static void configureWebFacet(IFacetedProjectWorkingCopy fpjwc, IProjectFacet requiredFacet, IPreset preset)
		throws CoreException {

		Action action = fpjwc.getProjectFacetAction(requiredFacet);

		if (action != null) {
			IDataModel dm = (IDataModel)action.getConfig();

			if (preset.getId().contains("portlet")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER);
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

				_addDefaultWebXml(fpjwc, dm);
			}
			else if (preset.getId().contains("hook")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER);
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
					IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);

				_addDefaultWebXml(fpjwc, dm);
			}
			else if (preset.getId().contains("ext")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER);

				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

				_addDefaultWebXml(fpjwc, dm);
			}
			else if (preset.getId().contains("layouttpl")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.LAYOUTTPL_PLUGIN_SDK_CONFIG_FOLDER);

				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
					IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

				ProjectUtil.setGenerateDD(dm, false);
			}
			else if (preset.getId().contains("theme")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.THEME_PLUGIN_SDK_CONFIG_FOLDER);
				ProjectUtil.setGenerateDD(dm, false);
			}
			else if (preset.getId().contains("web")) {
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
					IPluginFacetConstants.WEB_PLUGIN_SDK_CONFIG_FOLDER);
				dm.setStringProperty(
					IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
					IPluginFacetConstants.WEB_PLUGIN_SDK_SOURCE_FOLDER);
			}
		}
	}

	public static IPreset getLiferayPresetForProject(String pluginType) {
		IPreset preset = null;

		if ("portlet".equals(pluginType) || "servicebuilder".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_PORTLET_PRESET);
		}
		else if ("hook".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_HOOK_PRESET);
		}
		else if ("ext".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_EXT_PRESET);
		}
		else if ("layouttpl".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_LAYOUTTPL_PRESET);
		}
		else if ("theme".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_THEME_PRESET);
		}
		else if ("web".equals(pluginType)) {
			preset = ProjectFacetsManager.getPreset(IPluginFacetConstants.LIFERAY_WEB_PRESET);
		}

		return preset;
	}

	public static IFacetedProjectTemplate getLiferayTemplateForProject(String pluginType) {
		IFacetedProjectTemplate template = null;

		if ("portlet".equals(pluginType) || "servicebuilder".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_PORTLET_FACET_TEMPLATE_ID);
		}
		else if ("hook".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_HOOK_FACET_TEMPLATE_ID);
		}
		else if ("ext".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_EXT_FACET_TEMPLATE_ID);
		}
		else if ("layouttpl".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_TEMPLATE_ID);
		}
		else if ("theme".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_THEME_FACET_TEMPLATE_ID);
		}
		else if ("web".equals(pluginType)) {
			template = ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_WEB_FACET_TEMPLATE_ID);
		}

		return template;
	}

	private static void _addDefaultWebXml(IFacetedProjectWorkingCopy fpjwc, IDataModel dm) throws CoreException {

		// check for existing web.xml file, if not there, add a default one IDE-110
		// IDE-648

		IPath webinfPath = fpjwc.getProjectLocation().append(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF");

		IPath projectLocation = fpjwc.getProjectLocation();

		if (ProjectUtil.isExtProject(fpjwc.getProject()) || projectLocation.lastSegment().endsWith("-ext")) {
			fpjwc.getProjectLocation().append(IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER);
		}

		if (webinfPath.toFile().exists()) {
			File webXml = webinfPath.append("web.xml").toFile();

			if (!webXml.exists()) {
				ProjectUtil.setGenerateDD(dm, false);

				ProjectUtil.createDefaultWebXml(webXml, fpjwc.getProjectName());

				IProject project = fpjwc.getProject();

				if (project != null) {
					try {
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (Exception e) {
						ProjectCore.logError(e);
					}
				}
			}
		}
	}

	private static IProjectFacetVersion _getRequiredFacetVersionFromPreset(
		IProjectFacet requiredFacet, IPreset preset) {

		Set<IProjectFacetVersion> facets = preset.getProjectFacets();

		for (IProjectFacetVersion facet : facets) {
			if (facet.getProjectFacet().equals(requiredFacet)) {
				return facet;
			}
		}

		return null;
	}

	private static boolean _isRequiredVersion(IProjectFacetVersion facetVersion) {

		// java facet must be at least 1.6

		if (JavaFacet.FACET.equals(facetVersion.getProjectFacet())) {
			try {
				if (CoreUtil.compareVersions(new Version(facetVersion.getVersionString()), new Version(1, 6, 0)) < 0) {
					return false;
				}
			}
			catch (Throwable t) {
			}
		}

		return true;
	}

	private static void _removeSrcFolders(IDataModel dm, JavaFacetInstallConfig javaConfig) {
		dm.setStringProperty(IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME, null);

		dm.setStringProperty(
			IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
			IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);

		List<IPath> srcFolders = javaConfig.getSourceFolders();

		if (!CoreUtil.isNullOrEmpty(srcFolders)) {
			for (IPath srcFolder : srcFolders) {
				javaConfig.removeSourceFolder(srcFolder);
			}
		}
	}

	private static class Msgs extends NLS {

		public static String noFacetPreset;

		static {
			initializeMessages(SDKPluginFacetUtil.class.getName(), Msgs.class);
		}

	}

}