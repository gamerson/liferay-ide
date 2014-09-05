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

import com.liferay.ide.portlet.core.lfportlet.model.internal.LiferayScriptPossibleValuesService;
import com.liferay.ide.portlet.core.lfportlet.model.internal.PortletStyleValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Simon Jiang
 */
public interface HeaderPortletJavascript extends PortletStyleElement
{
    ElementType TYPE = new ElementType( HeaderPortletJavascript.class );

    @Type( base = Path.class )
    @Label( standard = "Header Portlet Javascript" )
    @XmlBinding( path = "" )
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @Services
    (
        value =
        {
            @Service
            (
                impl = LiferayScriptPossibleValuesService.class,
                params =
                {
                    @Service.Param( name = "type", value = ".js" ),
                }
            ),
            @Service( impl = PortletStyleValidationService.class )
        }
    )
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

    Value<Path> getValue();

    void setValue( String value );

    void setValue( Path value );

}
