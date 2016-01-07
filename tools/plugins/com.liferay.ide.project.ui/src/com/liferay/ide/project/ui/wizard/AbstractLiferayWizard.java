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
import com.liferay.ide.ui.LiferayPerspectiveFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.WizardDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class AbstractLiferayWizard<T extends Element> extends SapphireWizard implements IWorkbenchWizard, INewWizard
{

    public AbstractLiferayWizard( final T op, final DefinitionLoader.Reference<WizardDef> definition )
    {
        super( op, definition );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    protected void addToWorkingSets( IProject newProject ) throws Exception
    {
        if( newProject != null )
        {
            for( final FormComponentPart formPart : part().getPages().get( 0 ).children().all() )
            {
                if( formPart instanceof WorkingSetCustomPart )
                {
                    final WorkingSetCustomPart workingSetPart = (WorkingSetCustomPart) formPart;
                    final IWorkingSet[] workingSets = workingSetPart.getWorkingSets();

                    if( !CoreUtil.isNullOrEmpty( workingSets ) )
                    {
                        PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets( newProject, workingSets );
                    }
                }
            }
        }
    }

    protected void openLiferayPerspective( IProject newProject )
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        final IConfigurationElement element = new DelegateConfigurationElement( null )
        {

            @Override
            public String getAttribute( String aName )
            {
                if( aName.equals( "finalPerspective" ) )
                {
                    return LiferayPerspectiveFactory.ID;
                }

                return super.getAttribute( aName );
            }
        };

        BasicNewProjectResourceWizard.updatePerspective( element );

        // select and reveal
        BasicNewResourceWizard.selectAndReveal( newProject, workbench.getActiveWorkbenchWindow() );
    }

}
