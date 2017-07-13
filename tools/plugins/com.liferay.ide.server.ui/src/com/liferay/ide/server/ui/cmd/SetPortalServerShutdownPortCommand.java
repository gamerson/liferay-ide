/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class SetPortalServerShutdownPortCommand extends ServerCommand
{
    protected String oldShutdownPort;
    protected String shutdownPort;

    public SetPortalServerShutdownPortCommand( IServerWorkingCopy server, String shutdownPort )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.shutdownPort = shutdownPort;
    }

    public void execute()
    {
        oldShutdownPort = ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getShutdownPort();

        ( (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, null ) )
        .getPortalBundle().setShutdownPort( shutdownPort );
    }

    public void undo()
    {
        ( (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, null ) )
        .getPortalBundle().setShutdownPort( oldShutdownPort );
    }

}
