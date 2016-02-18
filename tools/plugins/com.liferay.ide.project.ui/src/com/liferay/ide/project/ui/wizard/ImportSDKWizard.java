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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.SDKImportOp;

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
public class ImportSDKWizard extends SapphireWizard<SDKImportOp>implements IWorkbenchWizard, INewWizard
{

    private static final String INITIAL_MESSAGE = "Please select sdk path to import.";

    private String title;
    private boolean supressedFirstErrorMessage = false;

    public ImportSDKWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( ImportSDKWizard.class ).wizard() );
    }

    public ImportSDKWizard( final String newTitle )
    {
        super( createDefaultOp(), DefinitionLoader.sdef( ImportSDKWizard.class ).wizard() );
    }

    private static SDKImportOp createDefaultOp()
    {
        return SDKImportOp.TYPE.instantiate();
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];

            final String message = wizardPage.getMessage();

            if( CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( INITIAL_MESSAGE );
            }

            if( wizardPage.getMessageType() == IMessageProvider.ERROR && !supressedFirstErrorMessage )
            {
                supressedFirstErrorMessage = true;

                wizardPage.setMessage( INITIAL_MESSAGE );
            }
        }
        if( title != null )
        {
            this.getContainer().getShell().setText( title );
        }

        return wizardPages;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

}
