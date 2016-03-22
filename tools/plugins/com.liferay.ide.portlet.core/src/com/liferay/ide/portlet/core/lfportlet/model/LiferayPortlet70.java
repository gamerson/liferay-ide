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
 *******************************************************************************/
package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
@Image( path = "images/elcl16/portlet_16x16.png" )
public interface LiferayPortlet70 extends LiferayPortlet
{
    ElementType TYPE = new ElementType( LiferayPortlet70.class );

    // portlet-data-handler-class?
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.exportimport.kernel.lar.PortletDataHandler" )
    @Label( standard = "Portlet Data Handler Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "portlet-data-handler-class" )
    ValueProperty PROP_PORTLET_DATA_HANDLER_CLASS = new ValueProperty( TYPE, "PortletDataHandlerClass" );

}