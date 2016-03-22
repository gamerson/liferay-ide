/*******************************************************************************
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
 ******************************************************************************/

package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Simon Jiang
 */
@CustomXmlRootBinding( value = LiferayPortletRootElementController.class )
@Image( path = "images/eview16/portlet_app_hi.gif" )
@XmlBinding( path = "liferay-portlet-app" )
public interface LiferayPortletXml70 extends LiferayPortletXml
{

    ElementType TYPE = new ElementType( LiferayPortletXml70.class );

    // *** Liferay Portlet *** /

    @Type( base = LiferayPortlet70.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet", type = LiferayPortlet70.class ) )
    ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" );

    ElementList<LiferayPortlet> getPortlets();

}
