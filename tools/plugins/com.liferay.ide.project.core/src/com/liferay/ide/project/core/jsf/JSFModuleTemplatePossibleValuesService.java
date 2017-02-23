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

package com.liferay.ide.project.core.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Simon Jiang
 */
public class JSFModuleTemplatePossibleValuesService extends PossibleValuesService
{

    private List<String> possibleValues;

    protected void initPossibleValuesService()
    {
        possibleValues = new ArrayList<String>();

        for( String key : NewLiferayJSFModuleProjectOpMethods.JSF_TEMPLATE_MAP.keySet() )
        {
            possibleValues.add( key );
        }

        Collections.sort( possibleValues );
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( this.possibleValues );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

}
