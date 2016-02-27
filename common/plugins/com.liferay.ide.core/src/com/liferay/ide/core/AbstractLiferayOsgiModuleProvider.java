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

package com.liferay.ide.core;

/**
 * @author Simon Jiang
 */
public abstract class AbstractLiferayOsgiModuleProvider
    implements ILiferayOsgiModuleProvider, Comparable<ILiferayOsgiModuleProvider>
{

    private boolean isDefault;
    private String projectType;
    private String shortName;

    public int compareTo( ILiferayOsgiModuleProvider provider )
    {
        if( provider != null )
        {
            return this.projectType.compareTo( provider.getProjectType() );
        }

        return 0;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getProjectType()
    {
        return this.projectType;
    }

    public boolean isDefault()
    {
        return this.isDefault;
    }

    public void setDefault( boolean isDefault )
    {
        this.isDefault = isDefault;
    }

    public void setProjectType( String type )
    {
        this.projectType = type;
    }
}
