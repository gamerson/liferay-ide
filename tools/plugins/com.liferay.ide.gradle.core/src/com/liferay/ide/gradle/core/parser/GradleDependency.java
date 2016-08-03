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

package com.liferay.ide.gradle.core.parser;

import java.util.Map;

/**
 * @author Lovett Li
 */
public class GradleDependency
{

    private String group;
    private String name;
    private String version;

    public GradleDependency( Map<String, String> dep )
    {
        setGroup( dep.get( "group" ) );
        setName( dep.get( "name" ) );
        setVersion( dep.get( "version" ) );
    }

    public GradleDependency( String group, String name, String version )
    {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;
        }

        GradleDependency gd = (GradleDependency) obj;

        if( gd.getGroup() == null || gd.getName() == null || gd.getVersion() == null )
        {
            return false;
        }
        if( gd.getGroup().equals( group ) && gd.getName().equals( name ) && gd.getVersion().equals( version ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
