/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.tomcat.ui.command;

import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;
import org.eclipse.osgi.util.NLS;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class SetUsernameCommand extends ServerCommand
{

    protected String oldUsername;
    protected String username;

    public SetUsernameCommand( LiferayTomcatServer server, String username )
    {
        super( server, Msgs.setUsername );
        this.username = username;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldUsername =  ( (LiferayTomcatServer) server ).getUsername();
        ( (LiferayTomcatServer) server ).setUsername( username );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setUsername( oldUsername );
    }

    private static class Msgs extends NLS
    {
        public static String setUsername;

        static
        {
            initializeMessages( SetUsernameCommand.class.getName(), Msgs.class );
        }
    }

}