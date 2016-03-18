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

package com.liferay.ide.project.core.modules.template;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;
import com.liferay.ide.project.core.modules.NewModuleOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.ElementList;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Simon Jiang
 */

public abstract class AbstractLiferayComponentOperation implements ILiferayModuleOperation<NewModuleOp>
{
    
    
    protected static final String TEMPLATE_DIR = "templates/"; //$NON-NLS-1$
    
    protected Configuration cfg = new Configuration();
    
    private void initFreeMarker() throws CoreException
    {
        try
        {
            URL templateURL = FileLocator.find( ProjectCore.getDefault().getBundle(), new Path(TEMPLATE_DIR), null);
            cfg.setDirectoryForTemplateLoading( new File( FileLocator.toFileURL( templateURL ).getFile() ) );
            
            cfg.setDefaultEncoding( "UTF-8" );
            cfg.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );
            
        }
        catch( IOException e )
        {
            throw new CoreException(ProjectCore.createErrorStatus( e ) );
        }
    }
    
    public AbstractLiferayComponentOperation( NewModuleOp op )
    {
        this.projectName = op.getProjectName().content( true );
        this.packageName = op.getPackageName().content( true );
        this.className = op.getComponentName().content( true );
        this.templateName = op.getComponentTemplateName().content( true );
        this.serviceName = op.getServiceName().content( true );

        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        for( int i = 0; i < propertyKeys.size(); i++ )
        {
            PropertyKey propertyKey = propertyKeys.get( i );

            this.properties.add(
                propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) +
                    ( i != ( propertyKeys.size() - 1 ) ? "," : "" ) );
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

    protected IFile prepareClassFile() throws CoreException
    {
        IFile file = null;
        try
        {
            IFolder sourceFolder = liferayProject.getSourceFolder( "java" );
            IJavaProject javaProject = JavaCore.create( project );
            IPackageFragment pack = createJavaPackage( javaProject, packageName );

            if( pack == null )
            {
                throw new CoreException( ProjectCore.createErrorStatus( "Can't create package folder" ) );
            }

            String fileName = className + ".java"; //$NON-NLS-1$
            IPath packageFullPath = new Path( packageName.replace( '.', IPath.SEPARATOR ) );
            IPath javaFileFullPath = packageFullPath.append( fileName );
            file = sourceFolder.getFile( javaFileFullPath );
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }

        return file;
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
        try
        {
            if( project != null )
            {
                liferayProject = LiferayCore.create( project );

                if( liferayProject != null )
                {
                    initFreeMarker();

                    IFile srcFile = prepareClassFile();
                    doSourceCodeOperation( srcFile );

                    doNewPropertiesOperation();

                    doMergeResourcesOperation();

                    doMergeBndOperation();

                    project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                }
            }            
        }
        catch( Exception e)
        {
            throw new CoreException(ProjectCore.createErrorStatus( e ) );
        }
    }
    protected abstract String getTemplateFile();
    
    protected abstract void doMergeBndOperation() throws CoreException;

    protected abstract void doMergeResourcesOperation() throws CoreException;

    protected abstract void doNewPropertiesOperation() throws CoreException;

    protected void doSourceCodeOperation(IFile srcFile) throws CoreException
    {
        try( OutputStream fos = new  FileOutputStream( srcFile.getLocation().toFile() ) )
        {

            Template temp = cfg.getTemplate(getTemplateFile());
            
            Map<String, Object> root = getTemplateMap();

            Writer out = new OutputStreamWriter(fos);
            temp.process(root, out);
            fos.flush();  
        }
        catch( IOException | TemplateException e )
        {
            throw new CoreException(ProjectCore.createErrorStatus( e ) );
        }
    }
    

    
    protected abstract Map<String, Object> getTemplateMap();

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