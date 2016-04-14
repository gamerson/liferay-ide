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

import com.liferay.ide.project.core.ProjectCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentFriendUrlOperation extends BaseNewLiferayComponentOperation
{

    private static final String TEMPLATE_FILE = "friendlyUrl.ftl";

    private final static String SUPER_CLASS = "DefaultFriendlyURLMapper";
    private final static String EXTENSION_CLASS = "FriendlyURLMapper.class";

    private final static String[] PROPERTIES_LIST = new String[] 
    { 
        "javax.portlet.name=com_liferay_network_utilities_web_portlet_NetworkUtilitiesPortlet" 
    };

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
        List<String> friendUrlProperties = new ArrayList<String>();

        friendUrlProperties.addAll( Arrays.asList( PROPERTIES_LIST ) );
        friendUrlProperties.add(
            "com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/" + className + "/routes.xml" );

        return friendUrlProperties;
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
        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            if( resourceFolder == null || !resourceFolder.exists() )
            {
                createResorcesFolder( project );
            }

            resourceFolder = liferayProject.getSourceFolder( "resources" );

            IFolder metaFolder = resourceFolder.getFolder( "META-INF/friendly-url-routes" );

            final IFile routesXml = metaFolder.getFile( new Path( className + "/routes.xml" ) );

            if( !routesXml.getLocation().toFile().exists() )
            {
                createSampleFile( routesXml, "friendurl-routes.xml" );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
}
