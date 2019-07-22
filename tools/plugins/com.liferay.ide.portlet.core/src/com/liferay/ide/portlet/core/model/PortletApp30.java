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

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlSchema;

/**
 * @author Terry Jia
 */
@Image(path = "images/obj16/portlet_model_obj.gif")
@XmlBinding(path = "portlet-app")
@XmlNamespace(prefix = "", uri = "http://xmlns.jcp.org/xml/ns/portlet")
@XmlSchema(
	location = "http://xmlns.jcp.org/xml/ns/portlet/portlet-app_3_0.xsd",
	namespace = "http://xmlns.jcp.org/xml/ns/portlet"
)
public interface PortletApp30 extends PortletApp {

	public ElementType TYPE = new ElementType(PortletApp30.class);

}