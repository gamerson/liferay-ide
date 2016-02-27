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

import com.liferay.ide.core.ILiferayProject;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectsJob;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;


/**
 * @author Simon Jiang
 */

public abstract class AbstractLiferayModuleOperation implements ILiferayModuleOperation
{

    static IPath templateFolder = ProjectCore.getDefault().getStateLocation().append( "templates-files" );;

    protected String projectName;
    protected String packageName;
    protected String className;
    protected String templateName;
    protected String serviceName;
    protected List<String> properties = new ArrayList<String>();

    protected File[] sourceTemplateFiles;
    protected File[] dependenciesTemplateFiles;
    protected File[] bndTemplateFiles;

    protected IProject project;
    protected ILiferayProject liferayProject;

    protected void createFile( IFile newFile, final byte[] input ) throws CoreException
    {
        if( newFile.getParent() instanceof IFolder )
        {
            CoreUtil.prepareFolder( (IFolder) newFile.getParent() );
        }

        newFile.create( new ByteArrayInputStream( input ), true, null );
    }

    protected void createFileInResouceFolder( IFolder sourceFolder, String filePath, File resourceFile )
        throws CoreException
    {
        final IFile projectFile = getProjectFile( sourceFolder, filePath );

        if( !projectFile.exists() )
        {
            String readContents = FileUtil.readContents( resourceFile, true );
            createFile( projectFile, readContents.getBytes() );
        }
    }

    protected void createJavaFile(
        String changedCode, IFolder sourceFolder, IPackageFragment pack, final String projectName,
        final String packageName, final String className ) throws CoreException
    {
        String fileName = className + ".java"; //$NON-NLS-1$
        IFile file = null;

        pack.createCompilationUnit( fileName, changedCode, true, null );
        byte[] contentBytes = changedCode.getBytes();
        IPath packageFullPath = new Path( packageName.replace( '.', IPath.SEPARATOR ) );
        IPath javaFileFullPath = packageFullPath.append( fileName );
        file = sourceFolder.getFile( javaFileFullPath );

        if( file != null && file.exists() )
        {
            file.setContents( new ByteArrayInputStream( contentBytes ), true, true, null );
        }
        else if( file != null )
        {
            file.create( new ByteArrayInputStream( contentBytes ), true, null );
        }
    }

    protected final IPackageFragment createJavaPackage( IJavaProject javaProject, final String packageName )
    {
        IPackageFragmentRoot packRoot = getSourceFolder( javaProject );

        if( packRoot == null )
        {
            return null;
        }

        IPackageFragment pack = packRoot.getPackageFragment( packageName );

        if( pack == null )
        {
            pack = packRoot.getPackageFragment( "" );
        }

        if( !pack.exists() )
        {
            String packName = pack.getElementName();
            try
            {
                pack = packRoot.createPackageFragment( packName, true, null );
            }
            catch( JavaModelException e )
            {
                ProjectCore.logError( e );
            }
        }

        return pack;
    }

    @Override
    public void doExecute() throws CoreException
    {
        extractTemplates();

        if( project != null )
        {
            ILiferayProject liferayProject = LiferayCore.create( project );

            if( liferayProject != null )
            {

                String result = doSourceCodeOperation();

                IFolder sourceFolder = liferayProject.getSourceFolder( "java" );
                IJavaProject javaProject = JavaCore.create( project );
                IPackageFragment pack = createJavaPackage( javaProject, packageName );

                if( pack == null )
                {
                    throw new CoreException( ProjectCore.createErrorStatus( "Can't create package folder" ) );
                }

                createJavaFile( result, sourceFolder, pack, projectName, packageName, className );

                doNewDependencyOperation();

                doNewPropertiesOperation();

                doMergeResourcesOperation();

                doMergeDependenciesOperation();

                doMergeBndOperation();

                project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );

                IProject[] projects = new IProject[] { project };
                SynchronizeGradleProjectsJob synchronizeJob =
                    new SynchronizeGradleProjectsJob( Arrays.asList( projects ) );
                synchronizeJob.schedule();
            }
        }

    }

    protected void doMergeBndOperation() throws CoreException
    {
    }

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

    protected abstract void doMergeResourcesOperation() throws CoreException;

    protected abstract void doNewDependencyOperation() throws CoreException;

    protected abstract void doNewPropertiesOperation() throws CoreException;

    protected abstract String doSourceCodeOperation() throws CoreException;

    private void extractTemplates()
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

    protected IProject getProject()
    {
        return CoreUtil.getProject( projectName );
    }

    protected IFile getProjectFile( IFolder sourceFolder, String filePath )
    {
        IFile retval = null;

        if( sourceFolder != null )
        {
            retval = sourceFolder.getFile( new Path( filePath ) );
        }

        return retval;
    }

    protected IPackageFragmentRoot getSourceFolder( IJavaProject javaProject )
    {
        try
        {
            for( IPackageFragmentRoot root : javaProject.getPackageFragmentRoots() )
            {
                if( root.getKind() == IPackageFragmentRoot.K_SOURCE )
                {
                    return root;
                }
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( e );
        }
        return null;
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

    @Override
    public void init(
        final String projectName, final String packageName, final String className, final String templateName,
        final String serviceName, final List<String> properties )
    {
        this.projectName = projectName;
        this.packageName = packageName;
        this.className = className;
        this.templateName = templateName;
        this.serviceName = serviceName;
        this.properties = properties;

        project = getProject();
        liferayProject = LiferayCore.create( project );

    }

    protected String readDenpendencies( File file )
    {
        String content = new String( FileUtil.readContents( file, true ) );
        return content.substring( content.lastIndexOf( "{" ) + 2, content.lastIndexOf( "}" ) );
    }
}
