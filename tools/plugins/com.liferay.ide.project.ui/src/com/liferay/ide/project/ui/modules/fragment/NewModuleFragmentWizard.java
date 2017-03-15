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

package com.liferay.ide.project.ui.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.ui.IWorkbench;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewModuleFragmentWizard extends BaseProjectWizard<NewModuleFragmentOp>
{
    public NewModuleFragmentWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewModuleFragmentWizard.class ).wizard() );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    @Override
    protected void performPostFinish()
    {
        super.performPostFinish();

        final NewModuleFragmentOp op = element().nearest( NewModuleFragmentOp.class );

        final IProject project = CoreUtil.getProject( op.getProjectName().content() );

        try
        {
            addToWorkingSets( project );

        }
        catch( Exception ex )
        {
            ProjectUI.logError( "Unable to add project to working set", ex );
        }

        openLiferayPerspective( project );
    }

    private static NewModuleFragmentOp createDefaultOp()
    {
        return NewModuleFragmentOp.TYPE.instantiate();
    }

}
