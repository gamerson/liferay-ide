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

import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

/**
 * @author Terry Jia
 */
public class UseDefaultLocationEnablementService extends EnablementService
{

    private Listener listener = null;

    @Override
    protected void initEnablementService()
    {
        super.initEnablementService();

        this.listener = new FilteredListener<Event>()
        {

            protected void handleTypedEvent( Event event )
            {
                refresh();
            }
        };

        final NewLiferayPluginProjectOp op = op();

        op.getProjectProvider().attach( this.listener );
    }

    @Override
    protected Boolean compute()
    {
        boolean retval = true;

        if( "ant".equals( op().getProjectProvider().content().getShortName() ) )
        {
            retval = false;
        }

        return retval;
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
