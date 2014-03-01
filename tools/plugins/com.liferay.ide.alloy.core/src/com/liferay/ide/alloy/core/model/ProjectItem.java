/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.alloy.core.model;

import com.liferay.ide.alloy.core.model.internal.ProjectImageService;
import com.liferay.ide.alloy.core.model.internal.ProjectLabelService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;


/**
 * @author Simon Jiang
 */

public interface ProjectItem extends Element
{
    ElementType TYPE = new ElementType( ProjectItem.class );

    // *** Item ***

    @Label( standard = "project Name" )
    @Unique
    @Services
    (
        {
            @Service( impl = ProjectLabelService.class, context = Service.Context.METAMODEL ),
            @Service( impl = ProjectImageService.class, context = Service.Context.METAMODEL )
        }
    )

    ValueProperty PROP_ITEM = new ValueProperty( TYPE, "Item" );

    Value<String> getItem();
    void setItem( String value );
}
