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

package com.liferay.ide.project.core.workspace;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Terry Jia
 */
public class AdditionalPropertyPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( final Set<String> values )
    {
        values.add( "microsoft.translator.client.id" );
        values.add( "microsoft.translator.client.secret" );
    }

}
