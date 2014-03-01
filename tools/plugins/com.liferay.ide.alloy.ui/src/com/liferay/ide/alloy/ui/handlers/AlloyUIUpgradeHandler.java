/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.alloy.ui.handlers;

import com.liferay.ide.alloy.core.util.AlloyUtil;
import com.liferay.ide.alloy.ui.wizard.AlloyUIUpgradeWizard;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.AbstractEnabledHandler;


/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class AlloyUIUpgradeHandler extends AbstractEnabledHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getCurrentSelection( event );
        ArrayList<IProject> projectList = new ArrayList<IProject>();
        if ( selection instanceof IStructuredSelection)
        {
            for(Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();)
            {
                IProject project = null;
                Object o = it.next();
                if( o instanceof IJavaProject)
                {
                    project = ( (IJavaProject) o ).getProject() ;
                }
                AlloyUtil.addLiferaySDKProject(project, projectList);
            }
        }
        final AlloyUIUpgradeWizard wizard = new AlloyUIUpgradeWizard( projectList.toArray( new IProject[projectList.size()] ) );
        new WizardDialog( HandlerUtil.getActiveShellChecked( event ), wizard ).open();

        return null;
    }

    @Override
    public boolean isEnabled()
    {
        if (AlloyUtil.getAllLiferaySDKProject().length > 0 )
        {
            return true;
        }

        return false;
    }


}
