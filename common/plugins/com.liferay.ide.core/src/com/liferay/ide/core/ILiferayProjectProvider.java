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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public interface ILiferayProjectProvider
{
    <T> List<T> getData( String key, Class<T> type, Object... params );

    String getDisplayName();

    int getPriority();

    String getShortName();

    String getProjectType();

    void importProject(String location , IProgressMonitor monitor , String extraOperation );

    ILiferayProject provide( Object adaptable );

    boolean provides( Class<?> type );
}
