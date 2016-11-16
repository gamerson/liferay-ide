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

import com.liferay.blade.api.MigrationConstants;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.ui.upgrade.animated.FindBreakingChangesPage;
import com.liferay.ide.project.ui.upgrade.animated.Page;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class RefindAction extends SelectionProviderAction
{

    public RefindAction( ISelectionProvider provider )
    {
        super( provider, "Refind breaking changesfor this file" );
    }

    public void run()
    {
        final FindBreakingChangesPage page =
            UpgradeView.getPage( Page.FINDBREACKINGCHANGES_PAGE_ID, FindBreakingChangesPage.class );
        final TreeViewer treeViewer = page.getTreeViewer();

        Object selection = treeViewer.getStructuredSelection().getFirstElement();

        if( selection instanceof FileProblems )
        {
            FileProblems fileProblems = (FileProblems) selection;

            IResource file = MigrationUtil.getIResourceFromFile( fileProblems.getFile() );

            if( file != null )
            {
                MarkerUtil.clearMarkers( file, MigrationConstants.MARKER_TYPE, null );
            }

            new WorkspaceJob( "Auto correcting all of migration problem for this file." )
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor )
                {
                    MigrateProjectHandler migrateHandler = new MigrateProjectHandler();

                    Path path = (Path) file.getLocation();

                    migrateHandler.findMigrationProblems(
                        new Path[] { path }, new String[] { file.getProject().getName() } );

                    return Status.OK_STATUS;
                }
            }.schedule();;
        }
    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        Object element = selection.getFirstElement();

        if( element instanceof FileProblems )
        {
            setEnabled( true );

            return;
        }

        setEnabled( false );
    }
}
