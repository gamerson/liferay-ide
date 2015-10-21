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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;

/**
 * @author Lovett Li
 */
public class MigrationTreeActionProvider extends CommonActionProvider
{
    private ICommonViewerSite viewerSite;

    public MigrationTreeActionProvider()
    {
        super();
    }


    @Override
    public void fillContextMenu( IMenuManager menu )
    {
        final Object selection = getFirstSelectedElement();

        if( selection instanceof IFile )
        {
            menu.add( new Separator() );
            menu.add( new MarkDoneAllAction( viewerSite.getSelectionProvider()) );
            menu.add( new MarkUnDoneAllAction( viewerSite.getSelectionProvider()) );
            menu.add( new IgnoreAllAction( viewerSite.getSelectionProvider()) );
        }
    }

    private Object getFirstSelectedElement()
    {
        final Object selection = getContext().getSelection();

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection sSelection = (IStructuredSelection) selection;

            return sSelection.getFirstElement();
        }

        return null;
    }

    public void init( ICommonActionExtensionSite site )
    {
        super.init( site );

         viewerSite = site.getViewSite();
    }

}
