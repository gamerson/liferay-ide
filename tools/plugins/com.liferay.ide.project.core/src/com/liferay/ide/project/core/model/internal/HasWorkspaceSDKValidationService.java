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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class HasWorkspaceSDKValidationService extends ValidationService
{

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getProjectProvider().attach( listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if ( op().getProjectProvider().content().getShortName().equals( "ant" ))
        {
            int sdkCount = SDKUtil.countPossibleWorkspaceSDKProjects();

            if ( sdkCount > 1 )
            {
                retval = Status.createErrorStatus( "This workspace has more than one SDK, please make sure only one sdk in workspace" );
            }
        }

        return retval;
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

}