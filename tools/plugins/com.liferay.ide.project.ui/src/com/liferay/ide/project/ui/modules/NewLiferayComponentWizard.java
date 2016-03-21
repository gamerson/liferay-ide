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
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentWizard extends SapphireWizard<NewLiferayComponentOp>implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;

    public NewLiferayComponentWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayComponentWizard.class ).wizard() );
    }

    public NewLiferayComponentWizard( final String projectName )
    {
        super( createDefaultOp( projectName ), DefinitionLoader.sdef( NewLiferayComponentWizard.class ).wizard() );
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

            if( messageType == IMessageProvider.ERROR && !CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( "Please enter a project name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private static IProject getSelectedProject()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if( window == null )
        {
            return null;
        }

        ISelection selection = window.getSelectionService().getSelection();

        if( selection == null || selection.isEmpty() )
        {
            return null;
        }

        if( !( selection instanceof IStructuredSelection ) )
        {
            return null;
        }

        Object[] elems = ( (IStructuredSelection) selection ).toArray();

        IProject project = null;

        Object elem = elems[0];

        if( elem instanceof IResource )
        {
            project = ( (IResource) elem ).getProject();
        }
        else if( elem instanceof IJavaProject )
        {
            project = ( (IJavaProject) elem ).getProject();
        }

        return project;
    }

    private static NewLiferayComponentOp createDefaultOp()
    {
        NewLiferayComponentOp newModuleOp = NewLiferayComponentOp.TYPE.instantiate();
        IProject selectedProject = getSelectedProject();

        if( selectedProject != null )
        {
            newModuleOp.setSelectedProjectName( selectedProject.getName() );
        }

        return newModuleOp;
    }

    private static NewLiferayComponentOp createDefaultOp( final String projectName )
    {
        NewLiferayComponentOp newModuleOp = NewLiferayComponentOp.TYPE.instantiate();

        newModuleOp.setProjectName( projectName );

        return newModuleOp;
    }

}