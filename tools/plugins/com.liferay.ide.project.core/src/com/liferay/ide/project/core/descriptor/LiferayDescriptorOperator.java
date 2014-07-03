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

package com.liferay.ide.project.core.descriptor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * @author Kuo Zhang
 */
public class LiferayDescriptorOperator
{
    private static LiferayDescriptorOperator instance;

    private LiferayDescriptorHelperManager helperManager;

    public static LiferayDescriptorOperator getInstance()
    {
        if( instance == null )
        {
            instance = new LiferayDescriptorOperator();
        }

        return instance;
    }

    private LiferayDescriptorOperator()
    {
        helperManager = LiferayDescriptorHelperManager.getInstance();
    }

    public IStatus operate( IProject project, Class<? extends IDescriptorOperation> type, Object... params )
    {
        IStatus status = Status.OK_STATUS;

        LiferayDescriptorHelper[] helpers = helperManager.getDescriptorHelpers( project );

        for( LiferayDescriptorHelper helper : helpers )
        {
            if( helper.getDescriptorOperation( type ) != null )
            {
                status = helper.getDescriptorOperation( type ).execute( params );

                if( ! status.isOK() )
                {
                    return status;
                }
            }
        }

        return status;
    }
}
