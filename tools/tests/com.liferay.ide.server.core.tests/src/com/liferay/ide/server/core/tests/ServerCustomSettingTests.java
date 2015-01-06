/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class ServerCustomSettingTests extends ServerCoreBase
{

    private boolean useDefaultServerSetting = false;

    @Override
    protected void setupServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        if( runtime == null )
        {
            setupRuntime();
        }

        final IServerWorkingCopy serverWC = ServerUtil.createServerForRuntime( runtime );

        serverWC.setAttribute(
            ILiferayTomcatServer.PROPERTY_USE_DEFAULT_PORTAL_SERVER_SETTINGS, useDefaultServerSetting );

        server = serverWC.save( true, npm );

        assertNotNull( server );
    }

    @Test
    public void testUseDefaultPortalSetting() throws Exception
    {
        server = null;

        useDefaultServerSetting = true;

        server = getServer();

        ILiferayTomcatServer portalServer =
            (ILiferayTomcatServer) server.loadAdapter( ILiferayTomcatServer.class, null );

        boolean useDefaultPortalServerSettings =
            ( (LiferayTomcatServer) portalServer ).getUseDefaultPortalServerSettings();
        
        assertEquals( useDefaultServerSetting, useDefaultPortalServerSettings );
    }

}

