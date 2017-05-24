/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All riproghts reserved.
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

package com.liferay.ide.server.websphere.core.publish;

import com.liferay.ide.server.core.portal.AbstractBundlePublishFullRemove;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.websphere.core.IWebsphereServer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Simon Jiang
 */
public class WebsphereBundlePublishFullRemove extends AbstractBundlePublishFullRemove
{

    protected IWebsphereServer websphereServer;

    public WebsphereBundlePublishFullRemove( IServer server, IModule[] modules, BundleDTO[] existingBundles )
    {
        super( server, modules, existingBundles );
        websphereServer = (IWebsphereServer) server.loadAdapter( IWebsphereServer.class, new NullProgressMonitor() );
    }

    @Override
    protected BundleSupervisor createBundleSupervisor() throws Exception
    {
        return ServerUtil.createBundleSupervisor( Integer.valueOf( websphereServer.getWebsphereJMXPort() ), server );
    }

}
