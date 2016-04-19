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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

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

        imports.add( "path=/portal/" + componentClassName );

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
                createResorcesFolder( project );
            }

            resourceFolder = liferayProject.getSourceFolder( "resources" );

            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources" );

            final IFile initJsp = metaFolder.getFile( new Path( componentClassName + "/html/init.jsp" ) );

            if( !initJsp.getLocation().toFile().exists() )
            {
                createSampleFile( initJsp, "strutsinaction-init.jsp" );
            }

            final IFile viewJsp = metaFolder.getFile( new Path( componentClassName + "/html/portal/" + componentClassName + ".jsp" ) );

            if( !viewJsp.getLocation().toFile().exists() )
            {
                createSampleFile( viewJsp, "strutsinaction-blade.jsp" );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
}
