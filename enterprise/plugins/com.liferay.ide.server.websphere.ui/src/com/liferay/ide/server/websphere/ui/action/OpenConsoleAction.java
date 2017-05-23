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

package com.liferay.ide.server.websphere.ui.action;

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.ui.action.AbstractServerRunningAction;
import com.liferay.ide.server.websphere.admin.IWebsphereAdminService;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereServer;
import com.liferay.ide.server.websphere.util.WebsphereUtil;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class OpenConsoleAction extends AbstractServerRunningAction
{

	public OpenConsoleAction() {
		super();
	}

	protected ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) selectedServer.loadAdapter( ILiferayServer.class, null );
    }

	protected URL getPortalURL()
	{
		if ( getLiferayServer() instanceof WebsphereServer )
		{
			WebsphereServer wasServer = (WebsphereServer) getLiferayServer();

			IWebsphereAdminService proxy = WebsphereCore.getWebsphereAdminService(wasServer);

			String consoleUrl = WebsphereUtil.getConsoleUrlString(selectedServer, wasServer, proxy);

			try {
				return new URL( consoleUrl + "/ibm/console" );
			}
			catch (MalformedURLException e) {
			}
		}

		return null;
	}


	protected String getPortalURLTitle()
	{
		return "WebSphere Console";
	}

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    protected void openBrowser( final URL url, final String browserTitle )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    IWorkbenchBrowserSupport browserSupport =
                        ServerUIPlugin.getInstance().getWorkbench().getBrowserSupport();

                    IWebBrowser browser =
                        browserSupport.createBrowser( IWorkbenchBrowserSupport.LOCATION_BAR |
                            IWorkbenchBrowserSupport.NAVIGATION_BAR, null, browserTitle, null );

                    browser.openURL( url );
                }
                catch( Exception e )
                {
                }
            }
        } );
    }

    protected void openPortalURL( ILiferayServer portalServer )
    {
        URL portalUrl = getPortalURL();

        if( portalUrl == null )
        {
            return;
        }

        openBrowser( portalUrl, getPortalURLTitle() );
    }

    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final ILiferayServer portalServer = getLiferayServer();

            new Job("")
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    openPortalURL( portalServer );
                    return Status.OK_STATUS;
                }

            }.schedule();
        }
    }

}
