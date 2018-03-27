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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.BundleDeployCommand;
import com.liferay.ide.server.core.gogo.CommandFactory;
import com.liferay.ide.server.core.portal.BundleDTOWithStatus.ResponseState;

import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class BundlePublishFullAdd extends BundlePublishOperation
{

    public BundlePublishFullAdd( IServer s, IModule[] modules )
    {
        super( s, modules );
    }

    private IStatus autoDeploy( IPath output ) throws CoreException
    {
        IStatus retval = null;

        final IPath autoDeployPath = portalRuntime.getPortalBundle().getAutoDeployPath();
        final IPath statePath = portalRuntime.getPortalBundle().getModulesPath().append( "state" );

        if( autoDeployPath.toFile().exists() )
        {
            try
            {
                FileUtil.writeFileFromStream(
                    autoDeployPath.append( output.lastSegment() ).toFile(), Files.newInputStream( output.toFile().toPath() ) );

                retval = Status.OK_STATUS;
            }
            catch( IOException e )
            {
                retval = LiferayServerCore.error( "Unable to copy file to auto deploy folder", e );
            }
        }

        if( statePath.toFile().exists() )
        {
            FileUtil.deleteDir( statePath.toFile(), true );
        }

        return retval;
    }

    protected boolean cleanBuildNeeded()
    {
        return false;
    }

    @Override
    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
        for( IModule module : modules )
        {
            IStatus retval = Status.OK_STATUS;

            IProject project = module.getProject();

            if( project == null )
            {
                continue;
            }

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

            if( bundleProject != null )
            {
                // TODO catch error in getOutputJar and show a popup notification instead

                monitor.subTask( "Building " + module.getName() + " output bundle..." );

                try
                {
                    IPath outputJar = bundleProject.getOutputBundle( cleanBuildNeeded(), monitor );

                    if( FileUtil.exists(outputJar) )
                    {
                        if( server.getServerState() == IServer.STATE_STARTED )
                        {
                            monitor.subTask(
                                "Remotely deploying " + module.getName() + " to Liferay module framework..." );

                            BundleDeployCommand deployCommand = new CommandFactory(bundleProject).createCommand(outputJar);
                            deployCommand.setHelper(createBundleDeployer());
                            deployCommand.run();
                            BundleDTOWithStatus generateResponse = deployCommand.getResponseStatus();

                            if ( generateResponse.getResponseState()!= ResponseState.ok) {
                            	retval = LiferayServerCore.error(generateResponse.getStatus());
                            }
                            else {
                                if ( !CoreUtil.isNullOrEmpty(generateResponse.getStatus())) {
                                    LiferayServerCore.logInfo(LiferayServerCore.info(generateResponse.getStatus()));
                                }
                            }
                        }
                        else
                        {
                            retval = autoDeploy( outputJar );
                        }

                        portalServerBehavior.setModuleState2( new IModule[] { module }, IServer.STATE_STARTED );
                    }
                    else
                    {
                        retval = LiferayServerCore.error( "Could not create output jar for " + project.getName());
                    }
                }
                catch( Exception e )
                {
                    retval = LiferayServerCore.error( "Deploy module project error for " + project.getName(), e );
                }
            }
            else
            {
                retval = LiferayServerCore.error( "Unable to get bundle project for " + module.getProject().getName() );
            }

            if( retval.isOK() )
            {
                this.portalServerBehavior.setModulePublishState2(
                    new IModule[] { module }, IServer.PUBLISH_STATE_NONE );

                project.deleteMarkers( LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE, false, 0 );
            }
            else
            {
                this.portalServerBehavior.setModulePublishState2(
                    new IModule[] { module }, IServer.PUBLISH_STATE_NONE );

                project.createMarker( LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE );

                LiferayServerCore.logError( retval );
            }
        }
    }
}
