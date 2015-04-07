/*******************************************************************************
 * Copyright (c) 2010 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * Command to change the server model
 *
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class SetUseDefaultPortalSeverSettingsCommand extends ServerCommand
{

    protected boolean useDefaultPortalServerSettings;
    protected boolean oldUseDefaultPortalServerSettings;

    /**
     * Constructs command to set portal server setting
     *
     * @param server
     * @param value
     *            of portalServerSettings
     */
    public SetUseDefaultPortalSeverSettingsCommand( IServerWorkingCopy server, boolean useDefaultPortalServerSettings )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.useDefaultPortalServerSettings = useDefaultPortalServerSettings;
    }

    /**
     * Execute setting portalServerSettings propety
     */
    public void execute()
    {
        oldUseDefaultPortalServerSettings =
            ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getUseDefaultPortalServerSettings();
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setUseDefaultPortalServerSettings( useDefaultPortalServerSettings );
    }

    /**
     * Restore prior portalServerSettings prooperty
     */
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setUseDefaultPortalServerSettings( oldUseDefaultPortalServerSettings );
    }

}
