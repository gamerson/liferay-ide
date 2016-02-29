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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.gradle.jarjar.org.apache.commons.lang.WordUtils;

/**
 * @author Simon Jiang
 */

public class LiferayPortletModuleOperation extends AbstractLiferayModuleOperation
{

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
                String updateClassName = StringUtil.replace( updatePackageContent, "_CLASSNAME_", className );
                String viewJsp = StringUtil.replace( updateClassName, "view.jsp", className + "/view.jsp" );
                String portletName = WordUtils.capitalize( className );
                result = StringUtil.replace( viewJsp, "_NAME_", portletName );
            }
        }

        return result;
    }

    @Override
    protected void doNewDependencyOperation() throws CoreException
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void doNewPropertiesOperation() throws CoreException
    {
        List<IFile> projectSrcFiles = new SearchFilesVisitor().searchFiles( project, className + ".java" );

        if( projectSrcFiles.size() > 0 )
        {
            IFile[] srcFiles = projectSrcFiles.toArray( new IFile[0] );
            File srcFile = srcFiles[0].getLocation().toFile();

            if( properties == null || properties.size() < 1 )
            {
                return;
            }

            try(FileInputStream fis = new FileInputStream( srcFile ))
            {
                String content = new String( FileUtil.readContents( fis ) );

                fis.close();

                String fontString = content.substring( 0, content.indexOf( "property" ) );

                String endString = content.substring( content.indexOf( "}," ) + 2 );

                String property = content.substring( content.indexOf( "property" ), content.indexOf( "}," ) );

                property = property.substring( property.indexOf( "{" ) + 1 );

                StringBuilder sb = new StringBuilder();

                sb.append( "property = {\n" );

                if( !CoreUtil.isNullOrEmpty( property ) )
                {
                    property = property.substring( 1 );
                    property = property.substring( 0, property.lastIndexOf( "\t" ) - 1 );
                    property += ",\n";
                    sb.append( property );
                }

                for( String str : properties )
                {
                    sb.append( "\t\t\"" + str + "\",\n" );
                }

                sb.deleteCharAt( sb.toString().length() - 2 );

                sb.append( "\t}," );

                StringBuilder all = new StringBuilder();

                all.append( fontString );
                all.append( sb.toString() );
                all.append( endString );

                String newContent = all.toString();

                if( !content.equals( newContent ) )
                {
                    FileUtil.writeFileFromStream( srcFile, new ByteArrayInputStream( newContent.getBytes() ) );
                }
            }
            catch( Exception e )
            {
                throw new CoreException( ProjectCore.createErrorStatus( e ) );
            }
        }
    }

    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
        // TODO Auto-generated method stub
    }

}
