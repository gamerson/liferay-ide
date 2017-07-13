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

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class SetPortalServerJmxPortCommand extends ServerCommand
{
    protected String oldJmxPort;
    protected String jmxPort;

    public SetPortalServerJmxPortCommand( IServerWorkingCopy server, String jmxPort )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.jmxPort = jmxPort;
    }

    public void execute()
    {
        oldJmxPort = ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getJmxPort();

        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setJmxPort( jmxPort );
    }

    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setJmxPort( oldJmxPort );
    }
}
