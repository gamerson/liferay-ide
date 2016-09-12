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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.IgnoredProblemsContainer;
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Lovett Li
 */
public class IgnoreAlways extends SelectionProviderAction implements IAction
{

    public IgnoreAlways( ISelectionProvider provider )
    {
        super( provider, "Ingore Always" );
    }

    @Override
    public void run()
    {
        final Problem problem = MigrationUtil.getProblemFromSelection( getSelection() );
        storeIgnoredProblem( problem );

        try
        {
            final MigrationProblemsContainer mpContainer = MigrationUtil.getMigrationProblemsContainer();
            final MigrationProblems[] projectProblem = mpContainer.getProblemsArray();

            new WorkspaceJob( "Auto correcting migration problem." )
            {
                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor )
                {
                    try
                    {
                        for( MigrationProblems pProblem : projectProblem )
                        {
                            FileProblems[] fProblems = pProblem.getProblems();

                            for( FileProblems fp : fProblems )
                            {
                                List<Problem> problems = fp.getProblems();
                                Iterator<Problem> iterator = problems.iterator();

                                while( iterator.hasNext() )
                                {
                                    Problem p = iterator.next();

                                    if( p.getTicket().equals( problem.getTicket() ) )
                                    {
                                        new IgnoreAction().run( p, getSelectionProvider() );
                                    }
                                }
                            }
                        }

                        refreshCommonViewer();
                    }
                    catch( Exception e )
                    {
                        return ProjectUI.createErrorStatus( "Unable to ignore always problem", e );
                    }

                    return Status.OK_STATUS;
                }
            }.schedule();

        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        final Object element = getStructuredSelection().getFirstElement();

        if( element instanceof Problem && !CoreUtil.empty( ( (Problem) element ).getTicket() ) )
        {
            setEnabled( true );
        }
        else
        {
            setEnabled( false );
        }

    }

    private void storeIgnoredProblem( Problem problem )
    {
        try
        {
            IgnoredProblemsContainer ipContainer = MigrationUtil.getIgnoredProblemsContainer();

            if( ipContainer == null )
            {
                ipContainer = new IgnoredProblemsContainer();
            }

            ipContainer.add( problem );

            UpgradeAssistantSettingsUtil.setObjectToStore( IgnoredProblemsContainer.class, ipContainer );
        }
        catch( IOException e )
        {
        }
    }

    protected void refreshCommonViewer()
    {
        final MigrationView mv = (MigrationView) UIUtil.showView( MigrationView.ID );

        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                mv.getCommonViewer().setInput( CoreUtil.getWorkspaceRoot() );
            }
        } );
    }

}
