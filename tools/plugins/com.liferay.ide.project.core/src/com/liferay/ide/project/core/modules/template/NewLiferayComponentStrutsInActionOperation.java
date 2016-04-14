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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentStrutsInActionOperation extends BaseNewLiferayComponentOperation
{

    private static final String STRUTS_TEMPLATE_FILE = "strutsinaction.ftl";

    private final static String STRUTS_SUPER_CLASSES = "BaseStrutsAction ";
    private final static String STRUTS_EXTENSION_CLASSES = "StrutsAction.class";

    public NewLiferayComponentStrutsInActionOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );
        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.struts.BaseStrutsAction" );
        imports.add( "com.liferay.portal.kernel.struts.StrutsAction" );
        imports.add( "javax.servlet.RequestDispatcher" );
        imports.add( "javax.servlet.ServletContext" );
        imports.add( "javax.servlet.http.HttpServletRequest" );
        imports.add( "javax.servlet.http.HttpServletResponse" );
        imports.add( "org.osgi.service.component.annotations.Reference" );

        return imports;
    }

    @Override
    protected List<String> getProperties()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );

        imports.add( "path=/portal/" + className );

        return imports;
    }

    @Override
    protected String getExtensionClass()
    {
        return STRUTS_EXTENSION_CLASSES;
    }

    @Override
    protected String getSuperClass()
    {
        return STRUTS_SUPER_CLASSES;
    }

    @Override
    protected String getTemplateFile()
    {
        return STRUTS_TEMPLATE_FILE;
    }

    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
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

            final IFile initJsp = metaFolder.getFile( new Path( className + "/html/init.jsp" ) );

            if( !initJsp.getLocation().toFile().exists() )
            {
                CoreUtil.createEmptyFile( initJsp );
            }

            final IFile viewJsp = metaFolder.getFile( new Path( className + "/html/portal/blade.jsp" ) );

            if( !viewJsp.getLocation().toFile().exists() )
            {
                CoreUtil.createEmptyFile( viewJsp );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
}
