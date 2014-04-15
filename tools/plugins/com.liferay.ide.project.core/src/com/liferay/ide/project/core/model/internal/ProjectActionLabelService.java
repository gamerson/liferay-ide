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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Simon Jiang
 */

public final class ProjectActionLabelService extends ValueLabelService
{

    private final static Map<String,String> actionMaps = new  HashMap<String,String>();

    static
    {
        actionMaps.put( "RuntimeUpgrade", "Update targeted runtime to setting" );
        actionMaps.put( "MetadataUpgrade", "Update all deployment descriptor metadata" );
        actionMaps.put( "ServicebuilderUpgrade", "Rebuild Services for service-builder projects" );
        actionMaps.put( "AlloyUIExecute", "Run Liferay Alloy UI Upgrade tool" );
    }

    public static Map<String,String> getProjectActions()
    {
        return actionMaps;
    }

    public static String getProjectAction(String actionName)
    {
        return actionMaps.get( actionName );
    }

    @Override
    public String provide( final String value )
    {
        if( value != null )
        {
           return getProjectAction( value );
        }
        return value;
    }


}
