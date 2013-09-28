/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.adt.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.core.util.FileUtil;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ADTUtil
{

    /*
     * IDE-1179 check if there exist project template unzipped files under the same directory as project template zip
     * file. if that's true, directly copy libraries to appropriate project location. if not, unzip the project 
     * template file first.
     */
    public static void addLiferayMobileSdkLibraries( final IProject project )
    {
        final File projectTemplate = ADTCore.getProjectTemplateFile();

        final IPath projectTemplateDir = new Path( projectTemplate.getAbsolutePath() ).removeFileExtension();

        if( !projectTemplateDir.toFile().exists() )
        {
            projectTemplateDir.toFile().mkdir();

            try
            {
                final String topLevelDir = ZipUtil.getFirstZipEntryName( projectTemplate );

                // unzip project template into the same directory.
                ZipUtil.unzip( projectTemplate, topLevelDir, projectTemplateDir.toFile(), new NullProgressMonitor() );
            }
            catch( Exception e )
            {
                ADTCore.logError( "Error unzipping Liferay mobile Sdk libraries", e );
            }
        }

        // Copy Liferay sdk mobile libraries to non Android Liferay project.
        File srcLibraryFile = null;
        File destDir = null;

        for( Path p : getLiferayMobileSdkLibraries() )
        {
            srcLibraryFile = projectTemplateDir.append( p ).toFile();

            if( srcLibraryFile.exists() && srcLibraryFile != null )
            {
                destDir = project.getLocation().append( p.removeLastSegments( 1 ) ).toFile();

                if( !destDir.exists() && destDir != null )
                {
                    destDir.mkdir();
                }

                FileUtil.copyFileToDir( srcLibraryFile, destDir );
            }
        }

        try
        {
            project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
            ADTCore.logError( "Error refreshing local project.", e );
        }
    }

    public static int extractSdkLevel( String content )
    {
        return Integer.parseInt( content.substring( content.indexOf( "API " ) + 4, content.indexOf( ":" ) ) );
    }

    private static Path[] getLiferayMobileSdkLibraries()
    {
        return new Path[] { new Path( "/libs/liferay-android-sdk.jar" ),
            new Path( "/libs/liferay-android-sdk.jar.properties" ),
            new Path( "/libs/src/liferay-android-sdk-sources.jar" ) };
    }

    public static boolean hasLiferayMobileSdkLibraries( final IProject project )
    {
        final IWorkspaceRoot wr = project.getWorkspace().getRoot();

        for( Path p : getLiferayMobileSdkLibraries() )
        {
            if( !wr.exists( new Path( project.getName() ).append( p ) ) )
            {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings( "restriction" )
    public static boolean isAndroidProject( final IProject project )
    {
        boolean retval = false;

        try
        {
            retval = project.hasNature( com.android.ide.eclipse.adt.AdtConstants.NATURE_DEFAULT );
        }
        catch( CoreException e )
        {
            ADTCore.logError( "Error opening project.", e );
        }

        return retval;
    }

}
