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
public class SetPasswordCommand extends ServerCommand
{

    protected String oldPassword;
    protected String password;

    public SetPasswordCommand( LiferayTomcatServer server, String password )
    {
        super( server, Msgs.setPassword );
        this.password = password;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldPassword = ( (LiferayTomcatServer) server ).getPassword();
        ( (LiferayTomcatServer) server ).setPassword( password );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setPassword( oldPassword );
    }

    private static class Msgs extends NLS
    {

        public static String setPassword;

        static
        {
            initializeMessages( SetPasswordCommand.class.getName(), Msgs.class );
        }
    }

}