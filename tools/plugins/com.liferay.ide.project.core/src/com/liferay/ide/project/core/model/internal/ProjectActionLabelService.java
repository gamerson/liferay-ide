/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

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
