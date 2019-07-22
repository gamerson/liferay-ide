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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PortletApp30;
import com.liferay.ide.project.ui.SapphireEditorForXml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.ElementType;
import org.eclipse.ui.PartInitException;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("serial")
public class PortletXmlEditor extends SapphireEditorForXml {

	public PortletXmlEditor() {
		super(_typeMap);
	}

	@Override
	protected void createFormPages() throws PartInitException {
		addDeferredPage(1, "Overview", "portlet-app.editor");
	}

	private static Map<String, ElementType> _typeMap = new HashMap<String, ElementType>() {
		{
			put("http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd", PortletApp.TYPE);
			put("http://xmlns.jcp.org/xml/ns/portlet", PortletApp30.TYPE);
		}
	};

}