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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class IgnoreAction extends TaskProblemAction
{
    private ISelectionProvider provider;
    public IgnoreAction()
    {
        this( new DummySelectionProvider() );
    }

    public IgnoreAction( ISelectionProvider provider )
    {
        super( provider, "Ignore" );
        this.provider = provider;
    }

    @Override
    protected IStatus runWithMarker( TaskProblem taskProblem, IMarker marker )
    {
        IStatus retval = Status.OK_STATUS;

        try
        {
            if ( marker.exists() )
            {
                marker.delete();
            }

            IStructuredSelection selection = (IStructuredSelection) provider.getSelection();
            TaskProblem selected = (TaskProblem) selection.getFirstElement();

            final TableViewer tv = (TableViewer) provider;

            final List<Object> asList = Arrays.asList( (Object[]) tv.getInput() );

            final List<TaskProblem> inputs = new ArrayList<TaskProblem>();

            for( final Object object : asList )
            {
                TaskProblem pr = (TaskProblem) object;

                if( !selected.equals( pr ) )
                {
                    inputs.add( pr );
                }
            }

            tv.setInput( inputs.toArray() );
            tv.refresh();
           
        }
        catch( CoreException e )
        {
            retval = e.getStatus();
        }

        return retval;
    }
}
