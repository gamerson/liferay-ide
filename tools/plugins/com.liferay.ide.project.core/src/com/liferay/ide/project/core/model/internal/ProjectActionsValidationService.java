/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.ProjectUpgradeOp;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Simon Jiang
 */

public class ProjectActionsValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                if( ! event.property().definition().equals( ProjectUpgradeOp.PROP_SELECTED_ACTIONS ) )
                {
                    refresh();
                }
            }
        };

        op().attach( listener, "*" ); //$NON-NLS-1$
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if( op().getSelectedActions().size() < 1 )
        {
            retval = Status.createErrorStatus( "Please select at least one upgrade action to execute " );
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private ProjectUpgradeOp op()
    {
        return context( ProjectUpgradeOp.class );
    }


}
