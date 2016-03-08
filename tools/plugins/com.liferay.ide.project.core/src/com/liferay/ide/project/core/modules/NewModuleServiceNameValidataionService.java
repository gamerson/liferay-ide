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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */

public class NewModuleServiceNameValidataionService extends ValidationService
{

    private Listener listener;

    @Override
    protected Status compute()
    {
        Status retVal = Status.createOkStatus();
        final String serviceName = op().getServiceName().content( true );

        if( CoreUtil.isNullOrEmpty( serviceName ) )
        {
            retVal = Status.createErrorStatus( "The service integration point can't be empty." );
        }

        return retVal;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null )
        {
            op().property( NewModuleOp.PROP_COMPONENT_TEMPLATE_NAME ).detach( this.listener );

            this.listener = null;
        }
        super.dispose();
    }

    @Override
    protected void initValidationService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewModuleOp.PROP_COMPONENT_TEMPLATE_NAME ).attach( this.listener );
    }

    private NewModuleOp op()
    {
        return context( NewModuleOp.class );
    }
}
