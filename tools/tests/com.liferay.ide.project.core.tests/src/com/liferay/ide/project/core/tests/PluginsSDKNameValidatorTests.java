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

package com.liferay.ide.project.core.tests;

import com.liferay.ide.project.core.PluginsSDKProjectRuntimeValidator;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class PluginsSDKNameValidatorTests extends ProjectCoreBase {

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Ignore
	@Test
	public void testSDKProjectsValidator() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String projectName = "Test2";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPluginType(PluginType.portlet);

		IProject portletProject = createAntProject(op);

		PluginsSDKProjectRuntimeValidator validator = new PluginsSDKProjectRuntimeValidator();

		validator.validate(ProjectUtil.getFacetedProject(portletProject));

		IMarker sdkMarker = _getProjectMarkers(
			portletProject, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE,
			PluginsSDKProjectRuntimeValidator.ID_PLUGINS_SDK_NOT_SET);

		Assert.assertNull(sdkMarker);

		String sdkName = SDKUtil.getSDK(portletProject).getName();

		IProjectDescription oldDescription = portletProject.getDescription();

		ProjectDescription newDescripton = new ProjectDescription();

		newDescripton.setName(oldDescription.getName());
		newDescripton.setLocation(ProjectCore.getDefaultStateLocation().append(projectName));
		newDescripton.setBuildSpec(oldDescription.getBuildSpec());
		newDescripton.setNatureIds(oldDescription.getNatureIds());

		portletProject.move(newDescripton, true, new NullProgressMonitor());

		portletProject.open(IResource.FORCE, new NullProgressMonitor());

		validator.validate(ProjectUtil.getFacetedProject(portletProject));

		IMarker newSdkMarker = _getProjectMarkers(
			portletProject, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE,
			PluginsSDKProjectRuntimeValidator.ID_PLUGINS_SDK_NOT_SET);

		Assert.assertNotNull(newSdkMarker);

		SDKUtil.saveSDKNameSetting(portletProject, sdkName);

		validator.validate(ProjectUtil.getFacetedProject(portletProject));

		IMarker resolutionSdkMarker = _getProjectMarkers(
			portletProject, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE,
			PluginsSDKProjectRuntimeValidator.ID_PLUGINS_SDK_NOT_SET);

		Assert.assertNull(resolutionSdkMarker);
	}

	private IMarker _getProjectMarkers(IProject proj, String markerType, String markerSourceId) throws Exception {
		if (proj.isOpen()) {
			IMarker[] markers = proj.findMarkers(markerType, true, IResource.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				if (markerSourceId.equals(marker.getAttribute(IMarker.SOURCE_ID))) {
					return marker;
				}
			}
		}

		return null;
	}

}