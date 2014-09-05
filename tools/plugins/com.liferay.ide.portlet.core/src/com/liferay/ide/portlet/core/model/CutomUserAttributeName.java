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
 *
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface CutomUserAttributeName extends Element
{

    ElementType TYPE = new ElementType( CutomUserAttributeName.class );

    // *** CustomAttributeName ***

    @Label( standard = "Custom Attribute Name" )
    @XmlBinding( path = "" )
    ValueProperty PROP_CUSTOM_ATTRIBUTE_NAME = new ValueProperty( TYPE, "CustomAttributeName" ); //$NON-NLS-1$

    Value<String> getCustomAttributeName();

    void setCustomAttributeName( String value );

}
