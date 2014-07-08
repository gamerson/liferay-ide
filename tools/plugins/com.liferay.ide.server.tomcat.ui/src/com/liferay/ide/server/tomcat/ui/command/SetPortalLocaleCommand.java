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

package com.liferay.ide.server.tomcat.ui.command;

import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;

import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

/**
 * Command to change the user timezone
 */
@SuppressWarnings( "restriction" )
public class SetPortalLocaleCommand extends ServerCommand
{

    protected String portalLocale;
    protected String oldPortalLocale;

    /**
     * Constructs command to set the portal locale
     *
     * @param server
     *            a Tomcat server
     * @param portalLocale
     */
    public SetPortalLocaleCommand( LiferayTomcatServer server, String portalLocale )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.portalLocale = portalLocale;
    }

    /**
     * Execute setting the portal locale
     */
    public void execute()
    {
        oldPortalLocale = ( (LiferayTomcatServer) server ).getPortalLocale();
        ( (LiferayTomcatServer) server ).setPortalLocale( portalLocale );
    }

    /**
     * Restore prior portal locale
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setPortalLocale( portalLocale );
    }
}
