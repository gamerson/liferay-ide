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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class BundlePublishFullAdd extends AbstractBundlePublishFullAdd
{

    public BundlePublishFullAdd( IServer s, IModule[] modules, BundleDTO[] existingBundles )
    {
        super( s, modules, existingBundles );
    }

    @Override
    protected BundleSupervisor createBundleSupervisor() throws Exception
    {
        return ServerUtil.createBundleSupervisor( portalRuntime.getPortalBundle().getJmxRemotePort(), server);
    }

    @Override
    protected IPath getAutoDeployPath()
    {
        return portalRuntime.getPortalBundle().getAutoDeployPath();
    }

    @Override
    protected IPath getModulePath()
    {
        return portalRuntime.getPortalBundle().getModulesPath();
    }

}
