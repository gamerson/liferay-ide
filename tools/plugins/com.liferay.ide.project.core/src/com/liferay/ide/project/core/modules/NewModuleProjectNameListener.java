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

import com.liferay.ide.core.ILiferayOsgiModuleProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class NewModuleProjectNameListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        NewModuleOp op = op(event);
        
        final String projectName = op.getProjectName().content( true );

        if( projectName == null || CoreUtil.isNullOrEmpty( projectName.trim() ) )
        {
            return;
        }
        
        IProject project = CoreUtil.getProject( projectName );
        
        if ( project != null )
        {
            ILiferayOsgiModuleProvider moduleProvider = LiferayCore.getModuleProvider( project );
            op.setModuleProvider( moduleProvider.getProjectType() );
        }
    }

    protected NewModuleOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewModuleOp.class );
    }
}
