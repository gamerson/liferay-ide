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
package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.def.DefinitionLoader;


/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectWizard extends BaseProjectWizard<NewLiferayJSFModuleProjectOp>
{

    public NewLiferayJSFModuleProjectWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayJSFModuleProjectWizard.class ).wizard() );
    }

    @Override
    protected void performPostFinish()
    {
        super.performPostFinish();

        final List<IProject> projects = new ArrayList<IProject>();

        final NewLiferayJSFModuleProjectOp op = element().nearest( NewLiferayJSFModuleProjectOp.class );

        ElementList<ProjectName> projectNames = op.getProjectNames();

        for( ProjectName projectName : projectNames )
        {
            final IProject newProject = CoreUtil.getProject( projectName.getName().content() );

            if( newProject != null )
            {
                projects.add( newProject );
            }
        }

        for( final IProject project : projects )
        {
            try
            {
                addToWorkingSets( project );

            }
            catch( Exception ex )
            {
                ProjectUI.logError( "Unable to add project to working set", ex );
            }
        }

        if ( projects.size() > 0 )
        {
            final IProject finalProject = projects.get(0);

            openLiferayPerspective( finalProject );
        }
     }

    private static NewLiferayJSFModuleProjectOp createDefaultOp()
    {
        return NewLiferayJSFModuleProjectOp.TYPE.instantiate();
    }

}
