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
package com.liferay.ide.project.core;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

/**
 * @author Terry Jia
 */
public class PluginModulelDelegate extends ProjectModule
{

    public PluginModulelDelegate( IProject project )
    {
        super( project );
    }

    @Override
    public IModuleResource[] members() throws CoreException
    {
        final List<IModuleResource> retval = new ArrayList<IModuleResource>();
        final IModuleResource[] members = super.members();
        //final ILiferayProject pluginProject = LiferayCore.create( ILiferayProject.class, getProject() );

        for( IModuleResource moduleResource : members )
        {
//            if( pluginProject.filterResource(
//                moduleResource.getModuleRelativePath().append( moduleResource.getName() ) ) )
//            {
//               continue;
//            }

            retval.add( moduleResource );
        }

        return retval.toArray( new IModuleResource[0] );
    }

}
