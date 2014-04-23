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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.ProjectItem;
import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.util.ProjectUpgradeJob;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Simon Jiang
 */

public class ProjectUpgradeWizard extends SapphireWizard<ProjectUpgradeOp>
    implements IWorkbenchWizard, INewWizard
{
    private boolean firstErrorMessageRemoved = false;
    public ProjectUpgradeWizard(IProject[] projects)
    {
        super( op(projects), DefinitionLoader.sdef( ProjectUpgradeWizard.class ).wizard() );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( !firstErrorMessageRemoved && wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];

            final String message = wizardPage.getMessage();
            final int messageType = wizardPage.getMessageType();

            if( messageType == IMessageProvider.ERROR && ! CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( message, SapphireWizardPage.ERROR ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    @Override
    protected void performPostFinish()
    {
        ProgressMonitorDialog  progressDialog = new  ProgressMonitorDialog (  Display.getCurrent().getActiveShell());
        try
        {
            final ProjectUpgradeOp op = element();
            progressDialog.run( true, true, new IRunnableWithProgress()
            {

                @Override
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    ProjectUpgradeJob job = new ProjectUpgradeJob( "Upgrading Liferay Plugin Projects", op);
                    job.setUser( true );
                    job.schedule();
                }

            });
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Problem runing  alloyUI task.", e );
        }
        super.performPostFinish();




    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private static ProjectUpgradeOp op(IProject[] projects)
    {
        ProjectUpgradeOp projectUpgradeOp = ProjectUpgradeOp.TYPE.instantiate();
        for(IProject project : projects)
        {
            ProjectItem instance = (ProjectItem) projectUpgradeOp.getSelectedProjects().insert();
            instance.setName( project.getName() );
        }

        return projectUpgradeOp;
    }
}
