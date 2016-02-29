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

package com.liferay.ide.gradle.core.modules;

import java.util.Set;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */
public class NewModuleProjectNameDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String defaultProjecName = "";

        Value<String> selectedProject = op().getSelectedProjectName();

        if( selectedProject != null )
        {
            String selectedProjcetName = selectedProject.content( true );

            PossibleValuesService projectNamePossibleService =
                op().getProjectName().service( PossibleValuesService.class );

            Set<String> projectNameList = projectNamePossibleService.values();

            if( projectNameList != null && projectNameList.size() > 0 )
            {
                if( selectedProjcetName != null && projectNameList.contains( selectedProjcetName ) )
                {
                    defaultProjecName = selectedProjcetName;
                }
                else
                {
                    String[] projectNames = projectNameList.toArray( new String[0] );
                    defaultProjecName = projectNames[0];
                }
            }
        }

        return defaultProjecName;
    }

    private NewModuleOp op()
    {
        return context( NewModuleOp.class );
    }
}
