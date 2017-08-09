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

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class SetPortalServerAgentPortCommand extends AbstractSetPortCommond
{

    public SetPortalServerAgentPortCommand( IServerWorkingCopy server, int port )
    {
        super( server, port );
    }

    public void execute()
    {
        oldPort = ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getAgentPort();

        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setAgentPort( port );
    }

    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setAgentPort( oldPort );
    }

}
