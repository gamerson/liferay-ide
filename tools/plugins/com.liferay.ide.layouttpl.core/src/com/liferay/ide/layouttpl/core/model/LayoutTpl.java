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

package com.liferay.ide.layouttpl.core.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author Kuo Zhang
 */
public interface LayoutTpl extends CanAddPortletLayouts
{

    ElementType TYPE = new ElementType( LayoutTpl.class );

    // *** Role ***

    @DefaultValue( text = "main" )
    ValueProperty PROP_ROLE = new ValueProperty( TYPE, "Role" );

    Value<String> getRole();
    void setRole( String role );

    // *** Id ***

    @DefaultValue( text = "main-content" )
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );

    Value<String> getId();
    void setId( String id );

    // *** Class Name ***
    @Required
    @DefaultValue( text = "" )
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String className);

    // *** Version ***

    @Type( base = Version.class )
    @Required
    @DefaultValue( text = "6.2" )
    ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" );

    Value<Version> getVersion();
    void setVersion( Version version );
    void setVersion( String version );

}
