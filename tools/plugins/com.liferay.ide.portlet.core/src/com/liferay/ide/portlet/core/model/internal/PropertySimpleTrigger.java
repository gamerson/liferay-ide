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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface PropertySimpleTrigger extends ISimpleTrigger
{

    ElementType TYPE = new ElementType( PropertySimpleTrigger.class );


    // *** Property Key ***

    @Label( standard = "Property Key" )
    @XmlBinding( path = "" )
    @Required
    ValueProperty PROP_PROPERTY_KEY = new ValueProperty( TYPE, "PropertyKey" ); //$NON-NLS-1$

    Value<String> getPropertyKey();

    void setPropertyKey( String value );

}
