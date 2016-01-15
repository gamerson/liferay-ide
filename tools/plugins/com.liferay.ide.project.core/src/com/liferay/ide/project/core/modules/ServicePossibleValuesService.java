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

import com.liferay.ide.project.core.ProjectCore;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */

public class ServicePossibleValuesService extends PossibleValuesService
{
    @Override
    public Status problem( final Value<?> value )
    {
        return Status.createOkStatus();
    }

    @Override
    protected void compute( final Set<String> values )
    {
        try
        {
            IServer runningServer = null;
            IServer[] servers = ServerCore.getServers();

            for( IServer iServer : servers )
            {
                if( iServer.getServerState() == IServer.STATE_STARTED )
                {
                    runningServer = iServer;
                    break;
                }
            }

            ServiceCommand serviceCommand = new ServiceCommand( runningServer );

            String[] allServices = serviceCommand.execute();

            values.addAll( Arrays.asList( allServices ) );

        }
        catch( Exception e )
        {
            ProjectCore.logError( "Get services list error. ", e );
        }
    }
}
