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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.core.portal.LiferayTargetPlatformClasspathContainer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Simon Jiang
 */

public class LiferayTargetPlatformViewFilter extends ViewerFilter
{

    public boolean select( Viewer viewer, Object parentElement, Object element )
    {
        IProject project = null;

        if( element instanceof IJavaProject )
        {
            project = ( (IJavaProject) element ).getProject();
        }
        else if( element instanceof IProject )
        {
            project = (IProject) element;
        }
        if( project != null )
        {
            IJavaProject javaProject = JavaCore.create( project );
            
            if ( javaProject != null )
            {
                try
                {
                    IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
                    for( IClasspathEntry entry : classPathEntries )
                    {
                        if ( entry.getPath().equals( LiferayTargetPlatformClasspathContainer.CONTAINER_PATH ) )
                        {
                            return false;
                        }
                    }
                }
                catch( JavaModelException e )
                {
                }
            }
        }
        return true;
    }

}
