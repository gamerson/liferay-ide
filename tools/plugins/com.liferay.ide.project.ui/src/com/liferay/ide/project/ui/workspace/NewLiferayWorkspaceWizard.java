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

package com.liferay.ide.project.ui.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.WorkspaceNameValidationService;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.wizard.AbstractLiferayWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;

/**
 * @author Andy Wu
 */

public class NewLiferayWorkspaceWizard extends AbstractLiferayWizard<NewLiferayWorkspaceOp>
{

    private boolean firstErrorMessageRemoved = false;

    public NewLiferayWorkspaceWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayWorkspaceWizard.class ).wizard() );
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
                try
                {
                    if( LiferayWorkspaceUtil.hasLiferayWorkspace() )
                    {
                        wizardPage.setMessage(
                            WorkspaceNameValidationService.hasLiferayWorkspaceMsg, SapphireWizardPage.ERROR );
                    }
                    else
                    {
                        wizardPage.setMessage( "Please enter the workspace name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
                    }
                }
                catch(CoreException e)
                {
                    wizardPage.setMessage( LiferayWorkspaceUtil.multiWorkspaceError , SapphireWizardPage.ERROR ); //$NON-NLS-1$
                }

                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    @Override
    protected void performPostFinish()
    {
        super.performPostFinish();

        final NewLiferayWorkspaceOp op = element().nearest( NewLiferayWorkspaceOp.class );

        final IProject newProject = CoreUtil.getProject( op.getWorkspaceName().content() );

        try
        {
            addToWorkingSets( newProject );
        }
        catch( Exception ex )
        {
            ProjectUI.logError( "Unable to add project to working set", ex );
        }

        openLiferayPerspective( newProject );
    }

    private static NewLiferayWorkspaceOp createDefaultOp()
    {
        return NewLiferayWorkspaceOp.TYPE.instantiate();
    }

}
