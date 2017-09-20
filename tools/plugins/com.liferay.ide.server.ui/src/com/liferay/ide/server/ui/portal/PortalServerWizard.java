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
/**
 *
 */

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.LiferayServerPort;
import com.liferay.ide.server.core.portal.PortalServerDelegate;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Gregory Amerson
 */
public class PortalServerWizard extends WizardFragment
{

    public PortalServerWizard()
    {
        super();
    }

    public void exit()
    {
        IServerWorkingCopy serverWorkingCopy = getServerWorkingCopy();
        IRuntime runtime = serverWorkingCopy.getRuntime();
        IServer[] servers = ServerUtil.getServersForRuntime( runtime );

        if( servers.length > 1 )
        {
            LiferayServerUI.logWarning(
                "The runtime selected already has server(s), you shouldn't make multiple servers point to the same runtime if you want to launch multiple servers in IDE at one time" );
        }
    }

    private void saveDefaultPorsts()
    {
        IPath defaultPortsJson = LiferayServerCore.getDefault().getStateLocation().append(
            getServerWorkingCopy().getId().replace( " ", "_" ) + "_default_ports.json" );

        if( !defaultPortsJson.toFile().exists() )
        {
            try
            {
                PortalServerDelegate portalSeverDelgate = (PortalServerDelegate) getServerWorkingCopy().loadAdapter(
                    PortalServerDelegate.class, new NullProgressMonitor() );
                File defaultPortsFile = new File( defaultPortsJson.toOSString() );
                List<LiferayServerPort> liferayServerPorts = portalSeverDelgate.getLiferayServerPorts();
                final ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue( defaultPortsFile, liferayServerPorts );
            }
            catch( IOException e )
            {
                LiferayServerUI.logError( "Failed to save server default ports inforamion", e );
            }
        }
    }

    @Override
    public void performFinish( IProgressMonitor monitor ) throws CoreException
    {
        super.performFinish( monitor );
        saveDefaultPorsts();
    }

    protected IServerWorkingCopy getServerWorkingCopy()
    {
        return (IServerWorkingCopy) getTaskModel().getObject( TaskModel.TASK_SERVER );
    }
}
