/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.tests;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */

public class LiferayProjectUpgrade611 extends ProjectCoreBase
{


    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.1" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.1.1/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.1-ce-ga2/tomcat-7.0.27" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.1.1";
    }
    public void setupSDKAndRuntim611() throws Exception
    {
        setupPluginsSDKAndRuntime();
    }

    public IProject createNewPluginAntProject(PluginType projectType) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( projectType.name() );
        op.setPluginType( projectType );

        return createAntProject( op );
    }


    public IProject[] createAllPluginTypeAntProject(String prefixProjectName) throws Exception
    {
        List<IProject> projects = new ArrayList<IProject>();

        for (PluginType plugin : PluginType.values())
        {
            if ( !plugin.name().equals( PluginType.web.name() ))
            {
                final NewLiferayPluginProjectOp op = newProjectOp( plugin.name() + "-" + prefixProjectName );
                op.setPluginType( plugin );
                IProject project = createAntProject( op );
                projects.add( project );
            }

        }

        return projects.toArray( new IProject[projects.size()] );
    }
}
