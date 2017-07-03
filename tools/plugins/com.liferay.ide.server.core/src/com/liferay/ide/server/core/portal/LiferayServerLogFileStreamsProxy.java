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

package com.liferay.ide.server.core.portal;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */

public class LiferayServerLogFileStreamsProxy extends LiferayServerFileStreamsProxy
{
    private ILaunch launch;

    public LiferayServerLogFileStreamsProxy(
        PortalRuntime runtime, ILaunch curLaunch )
    {
        this( runtime, curLaunch, new LiferayServerOutputStreamMonitor(), new LiferayServerOutputStreamMonitor() );
    }

    public LiferayServerLogFileStreamsProxy(
        PortalRuntime runtime, 
        ILaunch curLaunch, LiferayServerOutputStreamMonitor systemOut, LiferayServerOutputStreamMonitor systemErr )
    {
        this.launch = null;

        if( ( runtime == null ) )
        {
            return;
        }

        PortalBundle portalBundle = runtime.getPortalBundle();

        this.launch = curLaunch;
        try
        {

            this._sysoutFile = portalBundle.getLogPath().toOSString();

            if( systemOut != null )
            {
                this._sysOut = systemOut;
            }
            else
            {
                this._sysOut = new LiferayServerOutputStreamMonitor();
            }

            startMonitoring();
        }
        catch( Exception e )
        {
        }
    }

    public ILaunch getLaunch()
    {
        return launch;
    }

    @Override
    public IStreamMonitor getErrorStreamMonitor()
    {
        return null;
    }
}
