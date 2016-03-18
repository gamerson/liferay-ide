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

package com.liferay.ide.project.core.modules.template;

import com.liferay.ide.project.core.modules.NewModuleOp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Jiang
 */

public class LiferayDSComponentServiceOperation extends LiferayDSComponentAbstractOperation
{
    private String serviceClassName;
    
    public LiferayDSComponentServiceOperation( NewModuleOp op )
    {
        super( op );
        
        if ( serviceName != null )
        {
            int servicePos = serviceName.lastIndexOf( "." );
            
            serviceClassName = serviceName.substring( servicePos + 1 );
        }
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );

        imports.add( serviceName );

        return imports;
    }

    @Override
    protected String getSuperClass()
    {
        return serviceClassName;
    }

    @Override
    protected String getExtensionClass()
    {
        return serviceClassName + ".class";
    }
}