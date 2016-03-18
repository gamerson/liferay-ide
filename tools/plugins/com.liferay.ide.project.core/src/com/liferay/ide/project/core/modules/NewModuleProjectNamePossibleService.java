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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 */

public class NewModuleProjectNamePossibleService extends PossibleValuesService
{

    @Override
    public Status problem( final Value<?> value )
    {
        return Status.createOkStatus();
    }

    @Override
    protected void compute( final Set<String> values )
    {
        try
        {
            IProject[] allProjects = CoreUtil.getAllProjects();

            for( IProject project : allProjects )
            {
                if( CoreUtil.isLiferayProject( project ) &&
                    project.hasNature( "org.eclipse.buildship.core.gradleprojectnature" ) )
                {
                    values.add( project.getName() );
                }
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Get project list error. ", e );
        }
    }
}