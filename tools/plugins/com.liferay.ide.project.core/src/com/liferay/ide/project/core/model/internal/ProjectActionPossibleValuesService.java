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

package com.liferay.ide.project.core.model.internal;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */

public final class ProjectActionPossibleValuesService extends PossibleValuesService
{

    private final static String[] actions= {"RuntimeUpgrade","MetadataUpgrade","ServicebuilderUpgrade","AlloyUIExecute"};

    @Override
    protected void compute( final Set<String> values )
    {
        for( String action : actions)
        {
            values.add( action );
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();

    }

    public boolean isCaseSensitive()
    {
        return false;
    }

    @Override
    public boolean ordered()
    {
       return true;
    }

    @Override
    public org.eclipse.sapphire.modeling.Status problem( Value<?> value )
    {
        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

}
