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

package com.liferay.ide.project.ui.dialog;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;

/**
 * @author Andy Wu
 */
public interface SelectRuntimeOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( SelectRuntimeOp.class );

    @Services
    (
        value =
        {
            @Service( impl = Liferay7xRuntimePossibleValuesService.class ),
            @Service( impl = Liferay7xRuntimeDefaultValueService.class ),
            @Service( impl = Liferay7xRuntimeValidationService.class )
        }
    )
    @Required
    ValueProperty PROP_7X_RUNTIME = new ValueProperty( TYPE, "7xRuntime" );

    Value<String> get7xRuntime();
    void set7xRuntimeName( String value );

    @Services
    (
        value =
        {
            @Service( impl = Liferay62xRuntimePossibleValuesService.class ),
            @Service( impl = Liferay62xRuntimeDefaultValueService.class ),
            @Service( impl = Liferay62xRuntimeValidationService.class )
        }
    )
    @Required
    ValueProperty PROP_62X_RUNTIME = new ValueProperty( TYPE, "62xRuntime" );

    Value<String> get62xRuntime();
    void set62xRuntime( String value );
}
