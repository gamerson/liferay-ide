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
package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.wizard.ProjectUpgradeWizard;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class ProjectUpgradeToolAction extends AbstractObjectAction
{

    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            ArrayList<IProject> projectList = new ArrayList<IProject>();
            for(Iterator<?> it = ((IStructuredSelection) fSelection).iterator(); it.hasNext();)
            {
                IProject project = null;
                Object o = it.next();
                if( o instanceof IProject)
                {
                    project = ( (IProject) o ).getProject() ;
                    if( project != null && project.isAccessible() && !projectList.contains( project )
                           && SDKUtil.isSDKProject( project ))
                    {
                        projectList.add(project);
                    }
                }

            }
            if( projectList != null )
            {
                final ProjectUpgradeWizard wizard = new ProjectUpgradeWizard( projectList.toArray( new IProject[projectList.size()] ) );
                new WizardDialog( getDisplay().getActiveShell(), wizard ).open();
            }

        }
    }
}
