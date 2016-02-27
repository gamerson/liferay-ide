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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.AbstractLiferayModuleOperation;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.NewModuleOp;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectsJob;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.gradle.jarjar.org.apache.commons.io.FileUtils;

/**
 * @author Simon Jiang
 */

public abstract class LiferayAbstractGradleModuleOperation extends AbstractLiferayModuleOperation
{
    static IPath templateFolder = ProjectCore.getDefault().getStateLocation().append( "templates-files" );;
    
    public LiferayAbstractGradleModuleOperation( NewModuleOp op )
    {
        super( op );
    }

    @Override
    public void doExecute() throws CoreException
    {
        extractTemplates();
        
        super.doExecute();

        IProject[] projects = new IProject[] { project };
        SynchronizeGradleProjectsJob synchronizeJob = new SynchronizeGradleProjectsJob( Arrays.asList( projects ) );
        synchronizeJob.schedule();
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
    protected void doNewDependencyOperation() throws CoreException
    {
    }

    @Override
    protected void doNewPropertiesOperation() throws CoreException
    {
    }

    @Override
    protected void doMergeDependenciesOperation() throws CoreException
    {
        try
        {
            File[] gradleFiles = getTempateFiles( templateName, "gradle" );
            List<IFile> projectGradleFiles = new SearchFilesVisitor().searchFiles( project, "build.gradle" );

            if( projectGradleFiles.size() > 0 )
            {
                IFile[] projectGradleFile = projectGradleFiles.toArray( new IFile[0] );

                File projectDenpendencyFile = projectGradleFile[0].getLocation().toFile();

                String projectDenpendencyContent = readDenpendencies( projectDenpendencyFile );
                String newDenpendencyContent = readDenpendencies( gradleFiles[0].getAbsoluteFile() );

                final String dependencyFileContent =
                    new String( FileUtil.readContents( projectDenpendencyFile, true ) );
                String dependencyHead =
                    dependencyFileContent.substring( 0, dependencyFileContent.lastIndexOf( "dependencies" ) );
                String dependencyEnd = dependencyFileContent.substring(
                    dependencyFileContent.lastIndexOf( "}" ) + 1, dependencyFileContent.length() );

                if( newDenpendencyContent != null && projectDenpendencyContent != null )
                {
                    String[] newDenpendencies = newDenpendencyContent.split( "\n" );
                    StringBuilder preNewContent = new StringBuilder();

                    preNewContent.append( dependencyHead );
                    preNewContent.append( "dependencies {" );
                    preNewContent.append( projectDenpendencyContent );

                    for( String newDenpendency : newDenpendencies )
                    {
                        if( !newDenpendency.isEmpty() && !projectDenpendencyContent.contains( newDenpendency ) )
                        {
                            preNewContent.append( newDenpendency );
                        }
                    }
                    preNewContent.append( "}" );
                    preNewContent.append( dependencyEnd );

                    if( !dependencyFileContent.equals( preNewContent ) )
                    {

                        String newContent = preNewContent.toString();
                        FileUtil.writeFileFromStream(
                            projectDenpendencyFile, new ByteArrayInputStream( newContent.getBytes() ) );

                    }
                }
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
    
    protected String readDenpendencies( File file )
    {
        String content = new String( FileUtil.readContents( file, true ) );
        return content.substring( content.lastIndexOf( "{" ) + 2, content.lastIndexOf( "}" ) );
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
