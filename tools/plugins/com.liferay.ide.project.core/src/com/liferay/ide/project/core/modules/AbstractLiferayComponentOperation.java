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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.sapphire.ElementList;

/**
 * @author Simon Jiang
 */

public abstract class AbstractLiferayComponentOperation implements ILiferayModuleOperation<NewModuleOp>
{

    public AbstractLiferayComponentOperation( NewModuleOp op )
    {
        this.projectName = op.getProjectName().content( true );
        this.packageName = op.getPackageName().content( true );
        this.className = op.getComponentName().content( true );
        this.templateName = op.getComponentTemplateName().content( true );
        this.serviceName = op.getServiceName().content( true );

        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        for( PropertyKey propertyKey : propertyKeys )
        {
            this.properties.add( propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
        }

        this.project = CoreUtil.getProject( projectName );

    }

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
        if( project != null )
        {
            liferayProject = LiferayCore.create( project );

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

                doNewPropertiesOperation();

                doMergeResourcesOperation();

                doMergeBndOperation();

                project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
            }
        }
    }

    protected abstract void doMergeBndOperation() throws CoreException;

    protected abstract void doMergeResourcesOperation() throws CoreException;

    protected abstract void doNewPropertiesOperation() throws CoreException;

    protected abstract String doSourceCodeOperation() throws CoreException;

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
}
