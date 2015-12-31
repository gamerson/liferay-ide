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
package com.liferay.ide.project.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

import com.liferay.ide.core.LiferayWorkspaceNature;
import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Andy Wu
 */
public class WorkspaceNameValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    public static String hasLiferayWorkspaceMsg = "Can't create, there is already a Liferay Workspace";

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                if( ! event.property().definition().equals( NewLiferayWorkspaceOp.PROP_WORKSPACE_NAME ) )
                {
                    refresh();
                }
            }
        };

        op().attach( listener, "*" );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if( hasLiferayWorkspace() )
        {
            retval = Status.createErrorStatus( hasLiferayWorkspaceMsg );

            return retval;
        }

        final NewLiferayWorkspaceOp op = op();
        final String currentWorkspaceName = op.getWorkspaceName().content();

        if( currentWorkspaceName != null )
        {
            final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentWorkspaceName, IResource.PROJECT );

            if( ! nameStatus.isOK() )
            {
                retval = StatusBridge.create( nameStatus );
            }
            else if( isInvalidProjectName( op ) )
            {
                retval = Status.createErrorStatus( "A project with that name already exists." );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private boolean isInvalidProjectName( NewLiferayWorkspaceOp op )
    {
        final String workspaceName = op.getWorkspaceName().content();

        if( CoreUtil.getProject( workspaceName ).exists() )
        {
            return true;
        }

        return false;
    }

    private NewLiferayWorkspaceOp op()
    {
        return context( NewLiferayWorkspaceOp.class );
    }

    public static boolean hasLiferayWorkspace()
    {
        IProject[] projects = CoreUtil.getAllProjects();

        LiferayWorkspaceNature liferayWorkspaceNature = new LiferayWorkspaceNature();

        for(IProject project : projects)
        {
            if(liferayWorkspaceNature.hasNature( project ))
                return true;
        }

        return false;
    }

}
