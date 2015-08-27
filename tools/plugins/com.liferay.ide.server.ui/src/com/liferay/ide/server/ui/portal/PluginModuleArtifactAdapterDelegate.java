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
package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.ILaunchable;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;
import org.eclipse.wst.server.core.util.WebResource;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "rawtypes" )
public class PluginModuleArtifactAdapterDelegate extends ModuleArtifactAdapterDelegate implements IAdapterFactory
{

    public Object getAdapter( Object adaptableObject, Class adapterType )
    {
        return null;
    }

    public Class[] getAdapterList()
    {
        return new Class[] { ILaunchable.class };
    }

    private IModule getModule( IProject project )
    {
        final IModule[] modules = ServerUtil.getModules( "liferay.plguin" );

        for( IModule module : modules )
        {
            if( project == null || project.equals( module.getProject() ) )
            {
                return module;
            }
        }

        return null;
    }

    @Override
    public IModuleArtifact getModuleArtifact( Object obj )
    {
        IProject project = null;

       if( obj instanceof IProject )
       {
           project = (IProject) obj;
       }
       else if( obj instanceof IAdaptable )
       {
           project = (IProject) ( (IAdaptable) obj ).getAdapter( IProject.class );
       }

       if( project != null )
       {
            if( isPlugin( project ) )
            {
                return new WebResource( getModule( project ), project.getProjectRelativePath() );
            }
        }

        return null;
    }

    private boolean isPlugin( IProject project )
    {
        return LiferayCore.create( IWebProject.class, project ) != null;
    }

}
