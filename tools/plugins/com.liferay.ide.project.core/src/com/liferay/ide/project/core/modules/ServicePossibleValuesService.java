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

package com.liferay.ide.project.core.modules;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 */

public class ServicePossibleValuesService extends PossibleValuesService
{
    @Override
    public Status problem( final Value<?> value )
    {
        return Status.createOkStatus();
    }
   
    @Override
    protected void compute( final Set<String> values )
    {
        values.addAll( Arrays.asList( NewLiferayModuleProjectOpMethods.getServices() ) );
    }
}
