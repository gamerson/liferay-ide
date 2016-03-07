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

package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewModuleOp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Simon Jiang
 */

public class LiferayMvcPortletModuleOperation extends LiferayPortletModuleOperation
{

    public LiferayMvcPortletModuleOperation( NewModuleOp op )
    {
        super( op );
    }

    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
        File[] tempateFiles = getTempateFiles( templateName, "jsp" );

        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            if( resourceFolder == null || !resourceFolder.exists() )
            {
                IJavaProject javaProject = JavaCore.create( project );

                List<IClasspathEntry> existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );
                List<IClasspathEntry> newRawClasspath = new ArrayList<IClasspathEntry>();

                IClasspathAttribute[] attributes =
                    new IClasspathAttribute[] { JavaCore.newClasspathAttribute( "FROM_GRADLE_MODEL", "true" ) }; //$NON-NLS-1$ //$NON-NLS-2$

                IClasspathEntry resourcesEntry = JavaCore.newSourceEntry(
                    project.getFullPath().append( "src/main/resources" ), new IPath[0], new IPath[0], null,
                    attributes );

                newRawClasspath.add( resourcesEntry );

                for( IClasspathEntry entry : existingRawClasspath )
                {
                    newRawClasspath.add( entry );
                }

                javaProject.setRawClasspath(
                    newRawClasspath.toArray( new IClasspathEntry[0] ), new NullProgressMonitor() );

                project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );

            }

            resourceFolder = liferayProject.getSourceFolder( "resources" );
            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources" );

            for( File file : tempateFiles )
            {
                final IPath templateCachePath = new Path( file.getAbsolutePath() );
                final String fileName = templateCachePath.segment( templateCachePath.segmentCount() - 1 );
                createFileInResouceFolder( metaFolder, className + "/" + fileName, file );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
}
