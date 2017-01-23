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
 * @author Andy Wu
 */
public abstract class AbstractLiferayProjectImporter implements ILiferayProjectImporter
{
    private String buildType;
    private int priority;

    public AbstractLiferayProjectImporter()
    {
    }

    public String getBuildType()
    {
        return buildType;
    }

    public void setBuildType( String buildType )
    {
        this.buildType = buildType;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority( int priority )
    {
        this.priority = priority;
    }

}
