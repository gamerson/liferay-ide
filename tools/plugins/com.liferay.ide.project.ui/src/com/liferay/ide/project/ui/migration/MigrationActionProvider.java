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

import com.liferay.ide.core.util.CoreUtil;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Gregory Amerson
 */
public class MigrationActionProvider extends CommonActionProvider
{

    private SelectionProviderAction _autoCorrectAction;
    private SelectionProviderAction _ignoreAction;
    private SelectionProviderAction _markDoneAction;
    private SelectionProviderAction _markUndoneAction;
    private SelectionProviderAction _openAction;

    public MigrationActionProvider()
    {
        super();
    }

    private void addListeners( CommonViewer cv )
    {
        cv.addOpenListener( new IOpenListener()
        {
            public void open( OpenEvent event )
            {
                try
                {
                    final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    final Object data = sel.getFirstElement();

                    if( !( data instanceof IFile ) )
                    {
                        return;
                    }

                    _openAction.run();
                }
                catch( Exception e )
                {
                }
            }
        });
    }

    @Override
    public void fillContextMenu( IMenuManager menu )
    {
        final Object selection = getSelectedElements();

        if( selection instanceof TaskProblem )
        {
            menu.add( new Separator() );
            menu.add( _markDoneAction );
            menu.add( _markUndoneAction );
            menu.add( _ignoreAction );

            final TaskProblem problem = (TaskProblem) selection;

            if( problem.isResolved() )
            {
                _markDoneAction.setEnabled( false );
                _markUndoneAction.setEnabled( true );
            }
            else
            {
                _markDoneAction.setEnabled( true );
                _markUndoneAction.setEnabled( false );
            }

            if( !CoreUtil.isNullOrEmpty( problem.autoCorrectContext ) )
            {
                menu.add( _autoCorrectAction );
            }

            menu.add( new Separator() );
        }
        else if( selection instanceof IStructuredSelection )
        {
            final Iterator iterator = ( (IStructuredSelection) selection ).iterator();

            while( iterator.hasNext() )
            {
                Object o = iterator.next();

                if( !( o instanceof TaskProblem ) )
                {
                    return;
                }
            }

            menu.add( new Separator() );
            menu.add( _markDoneAction );
            menu.add( _markUndoneAction );
            menu.add( _ignoreAction );
        }
    }

    private Object getSelectedElements()
    {
        final Object selection = getContext().getSelection();

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection sSelection = (IStructuredSelection) selection;

            if( sSelection.size() == 1 )
            {
                return sSelection.getFirstElement();
            }
            else
            {
                return sSelection;
            }
        }

        return null;
    }

    public void init( ICommonActionExtensionSite site )
    {
        super.init( site );

        final ICommonViewerSite viewerSite = site.getViewSite();

        if( viewerSite instanceof ICommonViewerWorkbenchSite )
        {
            StructuredViewer v = site.getStructuredViewer();

            if( v instanceof CommonViewer )
            {
                CommonViewer cv = (CommonViewer) v;

                addListeners( cv );
            }
        }
    }

    public MigrationActionProvider makeActions( ISelectionProvider provider )
    {
        // create the open action
        _openAction = new OpenAction( provider );
        _markDoneAction = new MarkDoneAction( provider );
        _markUndoneAction = new MarkUndoneAction( provider );
        _ignoreAction = new IgnoreAction( provider );
        _autoCorrectAction = new AutoCorrectAction( provider );

        return this;
    }

    void registerSelectionProvider( ISelectionProvider provider )
    {
        provider.addSelectionChangedListener( _openAction );
        provider.addSelectionChangedListener( _markDoneAction );
        provider.addSelectionChangedListener( _markUndoneAction );
        provider.addSelectionChangedListener( _ignoreAction );
        provider.addSelectionChangedListener( _autoCorrectAction );
    }
}
