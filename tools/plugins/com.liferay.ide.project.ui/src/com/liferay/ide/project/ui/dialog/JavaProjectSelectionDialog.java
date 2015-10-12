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

package com.liferay.ide.project.ui.dialog;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andy Wu
 */
public class JavaProjectSelectionDialog extends ProjectSelectionDialog
{

    class JavaElementContentProvider extends StandardJavaElementContentProvider
    {
        public Object[] getChildren( Object element )
        {
            if( element instanceof IJavaModel )
            {
                IJavaModel model = (IJavaModel) element;
                Set<IJavaProject> set = new HashSet<IJavaProject>();
                try
                {
                    IJavaProject[] projects = model.getJavaProjects();
                    for( int i = 0; i < projects.length; i++ )
                    {
                        set.add( projects[i] );
                    }
                }
                catch( JavaModelException jme )
                {
                    // ignore
                }
                return set.toArray();
            }
            return super.getChildren( element );
        }
    }

    /**
     * Constructor
     *
     * @param parentShell
     * @param projectsWithSpecifics
     */
    public JavaProjectSelectionDialog( Shell parentShell, ViewerFilter filter )
    {
        super( parentShell , filter );
        setTitle( "Project Selection" );
        setMessage( "Select project" );
    }

    @Override
    protected IContentProvider getContentProvider()
    {
        return new JavaElementContentProvider();
    }

}
