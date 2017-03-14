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

package com.liferay.ide.server.websphere.core.publish;

import com.liferay.ide.server.core.portal.AbstractBundlePublishFullAdd;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.websphere.core.IWebsphereServer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Simon Jiang
 */

public class WebsphereBundlePublishFullAdd extends AbstractBundlePublishFullAdd
{

    protected IWebsphereServer websphereServer;

    public WebsphereBundlePublishFullAdd( IServer server, IModule[] modules, BundleDTO[] existingBundles )
    {
        super( server, modules, existingBundles );
        websphereServer = (IWebsphereServer) server.loadAdapter( IWebsphereServer.class, new NullProgressMonitor() );
    }

    @Override
    protected BundleSupervisor createBundleSupervisor() throws Exception
    {
        return ServerUtil.createBundleSupervisor( Integer.valueOf( websphereServer.getWebsphereJMXPort() ), server );
    }

    @Override
    protected IPath getAutoDeployPath()
    {
        return websphereServer.getAutoDeployPath();
    }

    @Override
    protected IPath getModulePath()
    {
        return websphereServer.getModulesPath();
    }
}
