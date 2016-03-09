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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class LiferayDSComponentActivatorOperation extends LiferayDSComponentAbstractOperation
{

    public LiferayDSComponentActivatorOperation( NewModuleOp op )
    {
        super( op );
    }

    @Override
    protected void doMergeBndOperation() throws CoreException
    {
        try
        {
            List<IFile> projectbndFiles = new SearchFilesVisitor().searchFiles( project, "bnd.bnd" );

            if( projectbndFiles.size() > 0 )
            {
                IFile[] projectBndFiles = projectbndFiles.toArray( new IFile[0] );

                File projectBndFile = projectBndFiles[0].getLocation().toFile();

                String contents = FileUtil.readContents( projectBndFile, true );
                StringBuffer bndContents = new StringBuffer();

                if( contents.contains( "Bundle-Activator" ) )
                {
                    String[] updateBndContents = FileUtil.readLinesFromFile( projectBndFile, true );

                    for( String bndConfiguration : updateBndContents )
                    {
                        if( bndConfiguration.contains( "Bundle-Activator:" ) )
                        {
                            int activatorStart = bndConfiguration.lastIndexOf( ":" );
                            String bndInitName = bndConfiguration.substring( 0, activatorStart );

                            StringBuffer bndUpdate = new StringBuffer( bndInitName );
                            bndContents.append( bndUpdate.append( packageName + "." + className ).toString() );
                            bndContents.append( System.getProperty( "line.separator" ) );
                        }
                        else
                        {
                            bndContents.append( bndConfiguration );
                        }
                    }
                }
                else
                {
                    bndContents.append( "Bundle-Activator: " ).append( packageName + "." + className );
                    bndContents.append( System.getProperty( "line.separator" ) );
                    bndContents.append( contents );
                }

                String newContent = bndContents.toString();
                FileUtil.writeFileFromStream( projectBndFile, new ByteArrayInputStream( newContent.getBytes() ) );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    @Override
    protected String doSourceCodeOperation() throws CoreException
    {
        String result = "";
        File[] tempateFiles = getTempateFiles( templateName, "java" );

        if( tempateFiles != null )
        {
            for( File file : tempateFiles )
            {
                String content = FileUtil.readContents( file, true );

                String updatePackageContent = StringUtil.replace( content, "_package_", packageName );
                result = StringUtil.replace( updatePackageContent, "_CLASSNAME_", className );
            }
        }

        return result;
    }
}
