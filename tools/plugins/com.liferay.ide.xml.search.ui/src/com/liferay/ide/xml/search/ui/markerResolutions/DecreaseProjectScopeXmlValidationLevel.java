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

package com.liferay.ide.xml.search.ui.markerResolutions;

import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

/**
 * @author Kuo Zhang
 */
public class DecreaseProjectScopeXmlValidationLevel implements IMarkerResolution2 {

	public DecreaseProjectScopeXmlValidationLevel() {
	}

	@Override
	public String getDescription() {
		return _message;
	}

	@Override
	public Image getImage() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		URL url = plugin.getBundle().getEntry("/icons/arrow_down.png");

		return ImageDescriptor.createFromURL(url).createImage();
	}

	@Override
	public String getLabel() {
		return _message;
	}

	@Override
	public void run(IMarker marker) {
		String liferayPluginValidationType = marker.getAttribute(
			XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, null);

		if (liferayPluginValidationType == null) {
			liferayPluginValidationType = marker.getAttribute(
				XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE_OLD, null);
		}

		if (liferayPluginValidationType != null) {
			ValidationPreferences.setProjectScopeValidationLevel(
				marker.getResource().getProject(), liferayPluginValidationType, -1);
			ComponentUtil.validateFile((IFile)marker.getResource(), new NullProgressMonitor());
		}
	}

	private static final String _message = "Disable this type of validation in current project";

}