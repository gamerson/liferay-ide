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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Gregory Amerson
 */
public class NewLiferayComponentProjectNameDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        final IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            boolean isLiferayProject = CoreUtil.isLiferayProject( project );
            
            if ( isLiferayProject )
            {
                ILiferayProject liferayProject = LiferayCore.create(project);
                if ( liferayProject instanceof IBundleProject )
                {
                    return project.getName();
                }                    
            }
        }
        return null;
    }

}
