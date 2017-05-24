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

import com.liferay.ide.core.util.CoreUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.PublishTaskDelegate;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortalPublishTask extends PublishTaskDelegate
{

    public PortalPublishTask()
    {
        super();
    }

    @SuppressWarnings( "rawtypes" )
    public PublishOperation[] getTasks( IServer server, int kind, List modules, List kindList )
    {
        final List<AbstractBundlePublishOperation> tasks = new ArrayList<AbstractBundlePublishOperation>();

        final PortalServerBehavior serverBehavior =
            (PortalServerBehavior) server.loadAdapter( PortalServerBehavior.class, null );

        BundleDTO[] existingBundles = new BundleDTO[0];
        BundleSupervisor supervisor = null;

        if( server.getServerState() == IServer.STATE_STARTED )
        {
            try
            {
                supervisor = serverBehavior.createBundleSupervisor();
                existingBundles = supervisor.getAgent().getBundles().toArray( new BundleDTO[0] );
            }
            catch( Exception e )
            {
            }
            finally
            {
                if( supervisor != null )
                {
                    try
                    {
                        supervisor.close();
                    }
                    catch( IOException e )
                    {
                    }
                }
            }
        }

        if( !CoreUtil.isNullOrEmpty( modules ) )
        {
            final int size = modules.size();

            for( int i = 0; i < size; i++ )
            {
                IModule[] module = (IModule[]) modules.get( i );
                Integer deltaKind = (Integer) kindList.get( i );
                boolean needClean = false;
                IModuleResourceDelta[] deltas = ( (Server) server ).getPublishedResourceDelta( module );

                for( IModuleResourceDelta delta : deltas )
                {
                    final IModuleResource resource = delta.getModuleResource();
                    final IFile resourceFile = (IFile) resource.getAdapter( IFile.class );

                    if( resourceFile != null && resourceFile.getName().equals( "bnd.bnd" ) )
                    {
                        needClean = true;
                        break;
                    }
                }
                serverBehavior.addOperation ( tasks, server, module, existingBundles, kind, deltaKind, needClean );
            }
        }

        return tasks.toArray( new PublishOperation[0] );
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public PublishOperation[] getTasks( IServer server, List modules )
    {
        return super.getTasks( server, modules );
    }

}
