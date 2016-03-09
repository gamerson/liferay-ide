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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */

public abstract class LiferayDSComponentAbstractOperation extends AbstractLiferayComponentOperation
{

    static IPath templateFolder = ProjectCore.getDefault().getStateLocation().append( "templates-files" );;

    public LiferayDSComponentAbstractOperation( NewModuleOp op )
    {
        super( op );
    }

    @Override
    public void doExecute() throws CoreException
    {
        extractTemplates();

        super.doExecute();
    }

    @Override
    protected void doMergeBndOperation() throws CoreException
    {
    }

    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
    }

    @Override
    protected void doNewPropertiesOperation() throws CoreException
    {
    }

    protected void extractTemplates()
    {
        if( !templateFolder.toFile().exists() )
        {
            try
            {
                final File templateClassFolder = templateFolder.toFile();
                IPath bladeJarPath = BladeCLI.getBladeCLIPath();

                try(ZipFile zip = new ZipFile( bladeJarPath.toFile() ))
                {
                    File temp = File.createTempFile( "templates", ".zip" );
                    FileUtil.writeFileFromStream( temp, zip.getInputStream( zip.getEntry( "templates.zip" ) ) );

                    if( !templateClassFolder.exists() )
                    {
                        ZipUtil.unzip( temp, templateClassFolder );
                    }
                }
                catch( IOException e )
                {
                    ProjectCore.logError( e );
                }
            }
            catch( BladeCLIException e )
            {
                ProjectCore.logError( e );
            }
        }
    }

    protected File[] getTempateFiles( final String templateName, final String fileType )
    {
        List<File> templates = new ArrayList<File>();

        final Collection<File> files = FileUtils.listFiles( templateFolder.toFile(), new String[] { fileType }, true );

        if( files != null )
        {
            final IPath templateTypePath = templateFolder.append( "standalone" ).append( templateName );

            for( File file : files )
            {
                final IPath templateCachePath = new Path( file.getAbsolutePath() );

                if( templateTypePath.isPrefixOf( templateCachePath ) )
                {
                    templates.add( file );
                }
            }
        }

        return templates.toArray( new File[0] );
    }
}
