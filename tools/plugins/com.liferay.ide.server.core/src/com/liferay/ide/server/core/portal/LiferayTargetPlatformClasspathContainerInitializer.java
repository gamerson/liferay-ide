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

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Simon Jiang
 */

public class LiferayTargetPlatformClasspathContainerInitializer extends ClasspathContainerInitializer
{

    public void initialize( IPath containerPath, IJavaProject project )
    {
        final LiferayTargetPlatformManager debugManager = getManager();
        if( debugManager.isLiferayTargetPlatformClasspathContainer( containerPath ) )
        {
            try
            {
                IClasspathEntry[] entries =
                    debugManager.getLiferayTargetPlatformClasspathEntries( project, new NullProgressMonitor() );
                IPath path = new Path( LiferayTargetPlatformClasspathContainer.CONTAINER_ID );
                JavaCore.setClasspathContainer(
                    path, new IJavaProject[] { project },
                    new IClasspathContainer[] { new LiferayTargetPlatformClasspathContainer( path, entries ) }, null );

            }
            catch( Exception ex )
            {
            }
        }
    }

    public boolean canUpdateClasspathContainer( IPath containerPath, IJavaProject project )
    {
        return true;
    }

    public void requestClasspathContainerUpdate(
        IPath containerPath, final IJavaProject project, final IClasspathContainer containerSuggestion )
    {
        try
        {
            getManager().getLiferayTargetPlatformClasspathEntries( project, new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
            LiferayServerCore.logError( e );
        }
    }

    private LiferayTargetPlatformManager getManager()
    {
        return LiferayServerCore.getDefault().getDebugManager();
    }
}
