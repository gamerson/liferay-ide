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

package com.liferay.ide.server.ui;

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ModuleLabelDecorator;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Terry Jia
 */
@SuppressWarnings( { "restriction" } )
public class LiferayServerModuleStatusLabelDecorator extends ModuleLabelDecorator
{

    public LiferayServerModuleStatusLabelDecorator()
    {
        super();
    }

    @Override
    public Image decorateImage( Image image, Object element )
    {
        IModule module = null;
        Image bundleImage = image;

        if( element instanceof IModule )
        {
            module = (IModule) element;
        }
        else if( element instanceof ModuleServer )
        {
            IModule[] modules = ( (ModuleServer) element ).module;
            module = modules[modules.length - 1];
        }

        if( module != null )
        {
            IProject project= module.getProject();

            try
            {
                IMarker[] markers = project.findMarkers( LiferayServerCore.BUNDLE_COMPILE_MARKER_TYPE, false, 0 );

                if( markers != null && markers.length > 0 )
                {
                    bundleImage = getMissingImage();
                }
            }
            catch( CoreException e )
            {
            }
            

            return PlatformUI.getWorkbench().getDecoratorManager().decorateImage( bundleImage, project );
        }

        return null;
    }

    private Image getMissingImage()
    {
        return ImageResource.getImage( ImageResource.IMG_PROJECT_MISSING );
    }

}
