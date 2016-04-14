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

import java.io.ByteArrayInputStream;
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

public class NewLiferayComponentFriendUrlOperation extends BaseNewLiferayComponentOperation
{

    private static final String TEMPLATE_FILE = "friendlyUrl.ftl";

    private static final String ROUTES_XML_PART1 = new String( "<?xml version=\"1.0\"?> " );
    private static final String ROUTES_XML_PART2 = new String(
        "<!DOCTYPE routes PUBLIC \"-//Liferay//DTD Friendly URL Routes 7.0.0//EN\" \"http://www.liferay.com/dtd/liferay-friendly-url-routes_7_0_0.dtd\">" );
    private static final String ROUTES_XML_PART3 = new String( "<routes>" );
    private static final String ROUTES_XML_PART4 = new String( "<route>" );
    private static final String ROUTES_XML_PART5 = new String( "<pattern>/{tabs1}</pattern>" );
    private static final String ROUTES_XML_PART6 =
        new String( "<implicit-parameter name=\"mvcPath\">/view.jsp</implicit-parameter>" );
    private static final String ROUTES_XML_PART7 =
        new String( "<implicit-parameter name=\"p_p_state\">normal</implicit-parameter>" );
    private static final String ROUTES_XML_PART8 = new String( "</route>" );
    private static final String ROUTES_XML_PART9 = new String( "</routes>" );

    private final static String SUPER_CLASS = "DefaultFriendlyURLMapper";
    private final static String EXTENSION_CLASS = "FriendlyURLMapper.class";

    private final static String[] PROPERTIES_LIST =
        new String[] { "com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
            "javax.portlet.name=com_liferay_network_utilities_web_portlet_NetworkUtilitiesPortlet" };

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );
        imports.add( "com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper" );
        imports.add( "com.liferay.portal.kernel.portlet.FriendlyURLMapper" );

        return imports;
    }

    @Override
    protected List<String> getProperties()
    {
        List<String> mvcProperties = new ArrayList<String>();
        mvcProperties.addAll( Arrays.asList( PROPERTIES_LIST ) );

        return mvcProperties;
    }

    @Override
    protected String getSuperClass()
    {
        return SUPER_CLASS;
    }

    @Override
    protected String getExtensionClass()
    {
        return EXTENSION_CLASS;
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
        StringBuffer routeBuffer = new StringBuffer();

        routeBuffer.append( ROUTES_XML_PART1 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART2 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART3 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART4 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART5 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART6 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART7 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART8 ).append( System.getProperty( "line.separator" ) );
        routeBuffer.append( ROUTES_XML_PART9 ).append( System.getProperty( "line.separator" ) );

        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            if( resourceFolder == null || !resourceFolder.exists() )
            {
                IJavaProject javaProject = JavaCore.create( project );

                List<IClasspathEntry> existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );
                List<IClasspathEntry> newRawClasspath = new ArrayList<IClasspathEntry>();

                IClasspathAttribute[] attributes =
                    new IClasspathAttribute[] { JavaCore.newClasspathAttribute( "FROM_GRADLE_MODEL", "true" ) };

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
            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources/" ).getFolder( className );

            final IFile routesXml = metaFolder.getFile( new Path( "/routes.xml" ) );

            if( !routesXml.getLocation().toFile().exists() )
            {
                createTemplateFile( routesXml, routeBuffer.toString() );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    public void createTemplateFile( IFile newFile, final String content ) throws CoreException
    {
        if( newFile.getParent() instanceof IFolder )
        {
            CoreUtil.prepareFolder( (IFolder) newFile.getParent() );
        }

        newFile.create( new ByteArrayInputStream( content.getBytes() ), true, null );
    }
}
