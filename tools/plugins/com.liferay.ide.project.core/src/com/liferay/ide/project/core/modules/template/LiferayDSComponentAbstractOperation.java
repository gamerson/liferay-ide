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

import com.liferay.ide.project.core.modules.NewModuleOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public abstract class LiferayDSComponentAbstractOperation extends AbstractLiferayComponentOperation
{
    private static final String TEMPLATE_FILE = "template.ftl";
    public LiferayDSComponentAbstractOperation( NewModuleOp op )
    {
        super( op );
    }

    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "org.osgi.service.component.annotations.Component" );
        
        return imports;
    }

    protected List<String> getProperties()
    {
        List<String> properties = new ArrayList<String>();

        for( String property : super.properties )
        {
            properties.add( property );
        }
        return properties;
    }

    protected abstract String getSuperClass();
    
    protected String getExtensionClass()
    {
        return null;
    }
    
    protected Map<String, Object> getTemplateMap()
    {
        Map<String, Object> root = new HashMap<String, Object>();

        root.put( "importlibs", getImports() );
        root.put( "properties", getProperties() );
        root.put( "packagename", packageName );
        root.put( "classname", className );
        root.put( "projectname", projectName );
        root.put( "supperclass", getSuperClass() );
        root.put( "extensionclass", getExtensionClass() );
        root.put( "componenttype", templateName );

        return root;
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
    protected void doNewPropertiesOperation() throws CoreException
    {
    }
    
    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

}
