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
 * Command to change the server model
 */
@SuppressWarnings( "restriction" )
public class SetServerModelCommand extends ServerCommand
{

    protected int serverModel;
    protected int oldServerModel;

    /**
     * Constructs command to set the user timezone
     *
     * @param server
     *            a Tomcat server
     * @param server
     *            model
     */
    public SetServerModelCommand( LiferayTomcatServer server, int serverModel )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.serverModel = serverModel;
    }

    /**
     * Execute setting the server model
     */
    public void execute()
    {
        oldServerModel = ( (LiferayTomcatServer) server ).getServerModel();
        ( (LiferayTomcatServer) server ).setServerModel( serverModel );
    }

    /**
     * Restore prior server model
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setServerModel( oldServerModel );
    }

}
