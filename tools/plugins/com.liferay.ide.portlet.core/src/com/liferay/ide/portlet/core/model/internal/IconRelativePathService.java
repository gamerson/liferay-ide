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
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.model.LiferayPortletApp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;


/**
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public class IconRelativePathService extends RelativePathService
{

    @Override
    public List<Path> roots()
    {
        final List<Path> roots = new ArrayList<Path>();
        final LiferayPortletApp liferayPortletApp = context( LiferayPortletApp.class );

        if( liferayPortletApp != null )
        {
            final IProject project = liferayPortletApp.adapt( IProject.class );
            final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    final IPath location = container.getLocation();

                    if( location != null )
                    {
                        roots.add( new Path( location.toPortableString() ) );
                    }
                }
            }
        }

        return roots;
    }
}
