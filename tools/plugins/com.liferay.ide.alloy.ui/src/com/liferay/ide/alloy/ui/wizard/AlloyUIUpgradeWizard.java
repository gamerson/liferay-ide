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
package com.liferay.ide.alloy.ui.wizard;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.model.AlloyUIUpgradeOp;
import com.liferay.ide.alloy.core.model.ProjectItem;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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

public class AlloyUIUpgradeWizard extends SapphireWizard<AlloyUIUpgradeOp>
    implements IWorkbenchWizard, INewWizard
{
    private boolean firstErrorMessageRemoved = false;
    public AlloyUIUpgradeWizard(IProject[] projects)
    {
        super( op(projects), DefinitionLoader.sdef( AlloyUIUpgradeWizard.class ).wizard() );
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
                wizardPage.setMessage( "Please enter a project name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
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
            AlloyUIUpgradeOp op = element();
            progressDialog.run( true, true, new AlloyUITaskWithProgress(op));
        }
        catch( Exception e )
        {
            AlloyCore.logError( "Problem runing  alloyUI task.", e );
        }
        super.performPostFinish();




    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private static AlloyUIUpgradeOp op(IProject[] projects)
    {
        AlloyUIUpgradeOp alloyUIOp = AlloyUIUpgradeOp.TYPE.instantiate();
        for(IProject project : projects)
        {
            ProjectItem instance = alloyUIOp.getSelectedProjects().insert();
            instance.setItem( project.getName() );
        }

        return alloyUIOp;
    }
}
