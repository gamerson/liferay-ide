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
public class SetDisableCustomSettingCommand extends ServerCommand
{

    protected boolean disableCustomSetting;
    protected boolean oldDisableCustomSetting;

    /**
     * Constructs command to set portal ide properties
     *
     * @param server a Tomcat server
     * @param value of useIdeProperties
     */
    public SetDisableCustomSettingCommand( LiferayTomcatServer server, boolean useIdeProperties )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.disableCustomSetting = useIdeProperties;
    }

    /**
     * Execute setting useIdeProperties propety
     */
    public void execute()
    {
        oldDisableCustomSetting = ( (LiferayTomcatServer) server ).getDisableCustomSetting();
        ( (LiferayTomcatServer) server ).setDisableCustomSetting( disableCustomSetting );
    }

    /**
     * Restore prior useIdeProperties prooperty
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setDisableCustomSetting( oldDisableCustomSetting );
    }

}
