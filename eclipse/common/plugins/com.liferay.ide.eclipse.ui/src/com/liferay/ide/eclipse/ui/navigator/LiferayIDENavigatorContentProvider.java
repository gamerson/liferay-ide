/*******************************************************************************
 *    Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *   
 *    This library is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by the Free
 *    Software Foundation; either version 2.1 of the License, or (at your option)
 *    any later version.
 *  
 *    This library is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *    FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *    details.
 *   
 *******************************************************************************/

package com.liferay.ide.eclipse.ui.navigator;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider2;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

/**
 * @author kamesh
 */

@SuppressWarnings( "rawtypes" )
public abstract class LiferayIDENavigatorContentProvider implements IPipelinedTreeContentProvider2
{

    protected ICommonContentExtensionSite config;

    public void init( ICommonContentExtensionSite config )
    {
        this.config = config;
    }

    protected ICommonContentExtensionSite getConfig()
    {
        return this.config;
    }

    public void restoreState( IMemento aMemento )
    {
        // do nothing
    }

    public void saveState( IMemento aMemento )
    {
        // do nothing
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }

    public PipelinedShapeModification interceptAdd( PipelinedShapeModification anAddModification )
    {
        return null;
    }

    public PipelinedShapeModification interceptRemove( PipelinedShapeModification aRemoveModification )
    {
        return null;
    }

    public Object[] getElements( Object inputElement )
    {
        return null;
    }

    public void getPipelinedElements( Object anInput, Set theCurrentElements )
    {
        System.out.println( "LiferayIDENavigatorContentProvider.getPipelinedElements()" );
    }

    public boolean interceptRefresh( PipelinedViewerUpdate aRefreshSynchronization )
    {
        return false;
    }

    public boolean interceptUpdate( PipelinedViewerUpdate anUpdateSynchronization )
    {
        return false;
    }
}
