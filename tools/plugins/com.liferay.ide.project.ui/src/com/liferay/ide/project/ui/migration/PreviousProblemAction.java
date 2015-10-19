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

import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class PreviousProblemAction extends SelectionProviderAction implements IAction
{
    private IStructuredSelection _selection;
    private final CommonViewer _viewer;
    private final MigrationViewTreeUtil _treeUtil;

    protected PreviousProblemAction( CommonViewer viewer, MigrationViewTreeUtil treeUtil )
    {
        super( viewer, "Previous problem" );

        setImageDescriptor( ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "icons/e16/up.gif" ) );
        setDisabledImageDescriptor( ProjectUI.imageDescriptorFromPlugin(
            ProjectUI.PLUGIN_ID, "icons/e16/up_dis.gif" ) );
        setToolTipText( "Previous problem" );
        setEnabled( false );

        _viewer = viewer;
        _treeUtil = treeUtil;
    }

    public void selectionChanged( IStructuredSelection selection )
    {
        final Object element = selection.getFirstElement();

        if( element instanceof IFile )
        {
            setEnabled( true );

            _selection = selection;
        }
        else
        {
            setEnabled( false );

            _selection = null;
        }
    }

    @Override
    public void run()
    {
        if( _selection != null )
        {
            final Object element = _selection.getFirstElement();

            if( element instanceof IFile )
            {
                _viewer.expandAll();

                final IFile file = (IFile) element;

                final StructuredSelection structuredSelection =
                    new StructuredSelection( _treeUtil.getPreResource( file ) );

                _viewer.setSelection( structuredSelection, true );
            }
        }
    }
}