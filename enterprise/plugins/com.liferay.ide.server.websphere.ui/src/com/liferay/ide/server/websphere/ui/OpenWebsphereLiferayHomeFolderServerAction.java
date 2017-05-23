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
package com.liferay.ide.server.websphere.ui;

import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.action.OpenLiferayHomeFolderServerAction;
import com.liferay.ide.server.ui.util.ServerUIUtil;
import com.liferay.ide.server.websphere.core.IWebsphereServer;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

/**
 * @author Simon Jiang
 */

public class OpenWebsphereLiferayHomeFolderServerAction extends OpenLiferayHomeFolderServerAction
{

    @Override
    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            IWebsphereServer wasServer = (IWebsphereServer)selectedServer.loadAdapter( IWebsphereServer.class, new NullProgressMonitor() );
            final IPath path = wasServer.getLiferayHome();

            try
            {
                ServerUIUtil.openFileInSystemExplorer( path );
            }
            catch( IOException e )
            {
                LiferayServerUI.logError( "Error opening portal home folder.", e );
            }
        }
    }

}
