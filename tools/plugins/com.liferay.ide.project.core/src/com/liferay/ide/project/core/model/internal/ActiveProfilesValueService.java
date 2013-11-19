/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Tao Tao
 */
public class ActiveProfilesValueService extends ValidationService
{

    @Override
    protected Status compute()
    {
        String activeProfileId = op().getActiveProfilesValue().content();
        Status retval = Status.createOkStatus();

        if( activeProfileId !=null && activeProfileId.contains( StringPool.SPACE ) )
        {
            retval = Status.createErrorStatus( "No spaces are allowed." );
        }

        return retval;
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
