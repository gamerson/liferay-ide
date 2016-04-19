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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentWizard extends SapphireWizard<NewLiferayComponentOp>implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;
    private IProject initialProject;

    public NewLiferayComponentWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayComponentWizard.class ).wizard() );
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
        if( selection != null && !selection.isEmpty() )
        {
            final Object element = selection.getFirstElement();

            if( element instanceof IResource )
            {
                initialProject = ( (IResource) element ).getProject();
            }
            else if( element instanceof IJavaProject )
            {
                initialProject = ( (IJavaProject) element ).getProject();
            }
            else if( element instanceof IJavaElement )
            {
                initialProject = ( (IJavaElement) element ).getResource().getProject();
            }

            if( initialProject != null )
            {
                element().setProjectName( initialProject.getName() );
            }
        }
    }

    private static NewLiferayComponentOp createDefaultOp()
    {
        return NewLiferayComponentOp.TYPE.instantiate();
    }

}