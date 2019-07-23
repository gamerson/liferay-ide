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

package com.liferay.ide.portlet.core.model.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Terry Jia
 */
public class PortletDefinitionRootElementController extends StandardRootElementController {

	@Override
	protected RootElementInfo getRootElementInfo() {
		if (_rootElementInfo == null) {
			Map<String, String> schemas = new HashMap<>();

			Document doc = _getDocument();

			Element documentElement = doc.getDocumentElement();

			String uri = documentElement.getNamespaceURI();

			String namespace = uri;

			String schema = documentElement.getAttribute("xsi:schemaLocation");

			String[] s = schema.split(" ");

			schemas.put(s[0], s[1]);

			_rootElementInfo = new RootElementInfo(namespace, "", "portlet-app", schemas);
		}

		return _rootElementInfo;
	}

	private Document _getDocument() {
		Resource resource = resource().root();

		RootXmlResource rootXmlResource = resource.adapt(RootXmlResource.class);

		return rootXmlResource.getDomDocument();
	}

	private RootElementInfo _rootElementInfo;

}