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
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.model;


import com.liferay.ide.layouttpl.core.model.internal.PortletColumnsListener;
import com.liferay.ide.layouttpl.core.model.internal.PortletLayoutClassNameDefaultValueService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 *
 */
public interface PortletLayout extends Element
{
    ElementType TYPE = new ElementType( PortletLayout.class );

    // *** Portlet Columns ***

    @Type( base = PortletColumn.class )
    @Listeners( PortletColumnsListener.class )
    ListProperty PROP_PORTLET_COLUMNS = new ListProperty( TYPE, "PortletColumns" );

    ElementList<PortletColumn> getPortletColumns();

    // *** Class Name ***

    @Required
    @Service( impl = PortletLayoutClassNameDefaultValueService.class )
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String className);

}
