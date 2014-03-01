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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.ProjectAction;
import com.liferay.ide.project.core.model.ProjectItem;
import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.model.ProjectUpgradeOpMethods;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.ElementList;

/**
 * @author Simon Jiang
 */

public class ProjectUpgradeTaskWithProgress implements IRunnableWithProgress
{

    private ProjectUpgradeOp op;

    public ProjectUpgradeTaskWithProgress( ProjectUpgradeOp op )
    {
        this.op = op;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
    {
        ProjectUpgradeJob job = new ProjectUpgradeJob( "Upgrading Liferay Plugin Projects" );
        job.setUser( true );
        job.schedule();
    }

    private class ProjectUpgradeJob extends Job
    {

        public ProjectUpgradeJob( String name )
        {
            super( name );
        }

        @Override
        protected IStatus run( IProgressMonitor monitor )
        {
            try
            {

                ElementList<ProjectItem> projectItems = op.getSelectedProjects();
                ElementList<ProjectAction> projectActions = op.getSelectedActions();
                String runtimeName = op.getRuntimeName().content();

                List<String> projectItemNames = new ArrayList<String>();
                List<String> projectActionItems = new ArrayList<String>();
                for( ProjectItem projectItem : projectItems )
                {
                    projectItemNames.add( projectItem.getItem().content() );
                }

                for( ProjectAction projectAction : projectActions )
                {
                    projectActionItems.add( projectAction.getAction().content() );
                }

                ProjectUpgradeOpMethods.runUpgradeJob( projectItemNames, projectActionItems, runtimeName, monitor );;

                return new Status( Status.OK, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job Finished" );
            }
            catch( Exception ex )
            {
                LiferayProjectCore.logError( "Problem perform alloy upgrade tool.", ex );
                return new Status( Status.ERROR, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job run error" );
            }
        }
    }

}
