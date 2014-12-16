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
 *
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class SetPropertyFileSettingsCommand extends ServerCommand
{

    protected boolean propertyFileSettings;
    protected boolean oldPropertyFileSettings;

    /**
     * Constructs command to set portal ide properties
     *
     * @param server a Tomcat server
     * @param value of propertyFileSettings
     */
    public SetPropertyFileSettingsCommand( LiferayTomcatServer server, boolean propertyFileSettings )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.propertyFileSettings = propertyFileSettings;
    }

    /**
     * Execute setting propertyFileSettings propety
     */
    public void execute()
    {
        oldPropertyFileSettings = ( (LiferayTomcatServer) server ).getPropertyFileSettings();
        ( (LiferayTomcatServer) server ).setPropertyFileSetting( propertyFileSettings );
    }

    /**
     * Restore prior propertyFileSettings prooperty
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setPropertyFileSetting( oldPropertyFileSettings );
    }

}
