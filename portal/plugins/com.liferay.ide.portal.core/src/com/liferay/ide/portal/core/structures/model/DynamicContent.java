/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.portal.core.structures.model;

import com.liferay.ide.portal.core.structures.model.internal.CDATAValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Tao Tao
 */
public interface DynamicContent extends Element
{

    ElementType TYPE = new ElementType( DynamicContent.class );

    // *** LanguageID ***

    @Label( standard = "language id" )
    @XmlBinding( path = "@dynamic-content" )
    ValueProperty PROP_DYNAMIC_CONTENT = new ValueProperty( TYPE, "language id" ); //$NON-NLS-1$

    Value<String> getLanguageID();

    // *** Value ***

    @Label( standard = "value" )
    @XmlBinding( path = "" )
    @CustomXmlValueBinding( impl = CDATAValueBinding.class )
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" ); //$NON-NLS-1$

    Value<String> getValue();

    void setValue( String value );

    // *** Options ***

    @Type( base = Option.class )
    @Label( standard = "options" )
    @XmlListBinding
    (
        mappings =
        {
            @XmlListBinding.Mapping
            (
                element = "option",
                type = Option.class
            )
        }
    )
    ListProperty PROP_OPTIONS = new ListProperty( TYPE, "Options" ); //$NON-NLS-1$

    ElementList<Option> getOptions();
}
