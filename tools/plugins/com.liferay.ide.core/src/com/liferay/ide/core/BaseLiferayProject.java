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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Gregory Amerson
 */
public abstract class BaseLiferayProject implements ILiferayProject
{

    private IProject project;

    public BaseLiferayProject( IProject project )
    {
        this.project = project;
    }

    public <T> T adapt( Class<T> adapterType )
    {
        final ILiferayProjectAdapter[] adapters = LiferayCore.getProjectAdapters();

        if( ! CoreUtil.isNullOrEmpty( adapters ) )
        {
            for( ILiferayProjectAdapter adapter : adapters )
            {
                T adapted = adapter.adapt( this, adapterType );

                if( adapted != null )
                {
                    return adapted;
                }
            }
        }

        return null;
    }

    protected boolean filterResource( IPath resourcePath, String[] ignorePaths )
    {
        if( resourcePath == null || resourcePath.segmentCount() < 1 )
        {
            return false;
        }

        for( String ignorePath : ignorePaths )
        {
            if( resourcePath.segment( 0 ).equals( ignorePath ) )
            {
                return true;
            }
        }

        return false;
    }

    public IProject getProject()
    {
        return this.project;
    }

    public IFolder getSourceFolder( String classification )
    {
        final List<IFolder> folders = CoreUtil.getSourceFolders( JavaCore.create( project ) );

        if( !CoreUtil.isNullOrEmpty( folders ) )
        {
            return folders.get( 0 );
        }

        return null;
    }

    @Override
    public IFolder[] getSourceFolders()
    {
        IFolder[] retval = null;

        try
        {
            final IProject project = getProject();

            if( project != null )
            {
                final IJavaProject javaproject = JavaCore.create( project );

                if( javaproject != null  )
                {
                    if( !javaproject.isOpen() )
                    {
                        javaproject.open( new NullProgressMonitor() );
                    }

                    retval = CoreUtil.getSourceFolders( javaproject ).toArray( new IFolder[0] );
                }
            }
        }
        catch( JavaModelException e )
        {
            LiferayCore.logWarning( e );
        }

        return retval;
    }

    @Override
    public java.nio.file.Path[] getExternalUserLibs()
    {
        return null;
    }
}
