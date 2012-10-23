/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.CorePlugin;
import com.liferay.ide.core.util.DescriptorHelper;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Version;

/**
 * @author Cindy Li
 */
public class LiferayDescriptorHelper extends DescriptorHelper
{

    public LiferayDescriptorHelper( IProject project )
    {
        super( project );
    }

    protected String getDescriptorVersion()
    {
        ILiferayRuntime runtime = null;

        try
        {
            runtime = ServerUtil.getLiferayRuntime( project );
        }
        catch( CoreException e )
        {
            CorePlugin.logError( e );
        }

        String versionStr = runtime.getPortalVersion();
        Version version = new Version( versionStr );

        int major = version.getMajor();
        int minor = version.getMinor();

        StringBuilder stringBuilder =
            new StringBuilder().append( Integer.toString( major ) + "." + Integer.toString( minor ) + ".0" );
        versionStr = stringBuilder.toString();

        return versionStr;
    }

}
