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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;

import java.io.File;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class LiferayDSComponentServiceOperation extends LiferayDSComponentPortletOperation
{

    public LiferayDSComponentServiceOperation( NewModuleOp op )
    {
        super( op );
    }

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
                String importLib = StringUtil.replace( updateClassName, "_SERVICE_FULL_", serviceName );
                final int serviceClassStart = serviceName.lastIndexOf( "." );
                String simpleServiceName = serviceName.substring( serviceClassStart + 1 );
                result = StringUtil.replace( importLib, "_SERVICE_SHORT_", simpleServiceName );
            }
        }
        return result;
    }
}
