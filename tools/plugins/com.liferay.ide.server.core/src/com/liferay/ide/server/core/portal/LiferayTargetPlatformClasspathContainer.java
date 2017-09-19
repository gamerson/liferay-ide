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

package com.liferay.ide.server.core.portal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * @author Simon Jiang
 */

public class LiferayTargetPlatformClasspathContainer implements IClasspathContainer
{

    public static final IPath CONTAINER_PATH = new Path( "com.liferay.ide.targetplatform.classpath.container" );
    public static final String CONTAINER_ID = "com.liferay.ide.targetplatform.classpath.container"; //$NON-NLS-1$

    private IClasspathEntry[] entries;

    private final IPath path;

    public LiferayTargetPlatformClasspathContainer( IPath path, IClasspathEntry[] entries )
    {
        this.path = path;
        this.entries = entries;
    }

    public String getDescription()
    {
        return "Liferay Target Platform";
    }

    public int getKind()
    {
        return IClasspathContainer.K_APPLICATION;
    }

    public synchronized IClasspathEntry[] getClasspathEntries()
    {
        return entries;
    }

    public IPath getPath()
    {
        return path;
    }

}
