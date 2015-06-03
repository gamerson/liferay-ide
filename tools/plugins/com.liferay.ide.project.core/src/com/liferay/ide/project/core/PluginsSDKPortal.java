/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginsSDKPortal implements ILiferayPortal
{
    private final ILiferayRuntime runtime;

    public PluginsSDKPortal( ILiferayRuntime runtime )
    {
        this.runtime = runtime;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        return this.runtime.getAppServerPortalDir();
    }

    @Override
    public String[] getHookSupportedProperties()
    {
        return this.runtime.getHookSupportedProperties();
    }

    @Override
    public Properties getPortletCategories()
    {
        return this.runtime.getPortletCategories();
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return this.runtime.getPortletEntryCategories();
    }

    @Override
    public String getVersion()
    {
        return this.runtime.getPortalVersion();
    }

    @Override
    public Map<String, String> getRequiredProperties()
    {
        Map<String, String> properties = new HashMap<String, String>();

        String type = runtime.getAppServerType();

        String dir = runtime.getAppServerDir().toOSString();

        String deployDir = runtime.getAppServerDeployDir().toOSString();

        String libGlobalDir = runtime.getAppServerLibGlobalDir().toOSString();

        String parentDir = new File( dir ).getParent();

        String portalDir = runtime.getAppServerPortalDir().toOSString();

        properties.put( ISDKConstants.PROPERTY_APP_SERVER_TYPE, type );

        final String appServerDirKey =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DIR, runtime );
        final String appServerDeployDirKey =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, runtime );
        final String appServerLibGlobalDirKey =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, runtime );
        final String appServerPortalDirKey =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, runtime );

        properties.put( appServerDirKey, dir );
        properties.put( appServerDeployDirKey, deployDir );
        properties.put( appServerLibGlobalDirKey, libGlobalDir );
        //IDE-1268 need to always specify app.server.parent.dir, even though it is only useful in 6.1.2/6.2.0 or greater
        properties.put( ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR, parentDir );
        properties.put( appServerPortalDirKey, portalDir );

        return properties;
    }
}
