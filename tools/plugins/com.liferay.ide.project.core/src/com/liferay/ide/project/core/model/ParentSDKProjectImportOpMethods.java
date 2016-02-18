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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.Map;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class ParentSDKProjectImportOpMethods
{

    public static final Status execute( final ParentSDKProjectImportOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay plugin projects...", 100 );

        Status retval = Status.createOkStatus();

        final Path sdkLocation = op.getSdkLocation().content();

        if( sdkLocation == null || sdkLocation.isEmpty() )
        {
            return Status.createErrorStatus( "SDK folder cannot be empty" );
        }

        final Job job = new WorkspaceJob( "Importing Liferay SDK...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                IStatus retval = StatusBridge.create( Status.createOkStatus() );

                final SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );

                SDKUtil.openAsProject( sdk );

                return retval;
            }
        };

        job.schedule();

        return retval;
    }
    
    public static IStatus validateBuildProperties( final SDK sdk )
    {
        MultiStatus status = new MultiStatus( ProjectCore.PLUGIN_ID, IStatus.OK, "", null );

        Map<String,Object> sdkProperties = null;

        try
        {
            sdkProperties = sdk.getBuildProperties( false );

            if ( sdkProperties == null )
            {
                status.add( ProjectCore.createWarningStatus( "Could not find sdk settings." ) );

                return status;
            }
        }
        catch( Exception e)
        {
            status.add( ProjectCore.createWarningStatus( e.getMessage() ) );

            return status;
        }

        for( String propertyKey : SDK.APP_SERVER_PROPERTIES_KEYS )
        {
            final String propertyValue = (String) sdkProperties.get( propertyKey );

            if ( propertyValue == null )
            {
                status.add( ProjectCore.createWarningStatus( propertyKey + " is null." ) );
            }
            else
            {
                switch (propertyKey)
                {
                    case "app.server.type":
                    {
                        if( !SDK.SUPPORTED_SERVER_TYPES.contains( propertyValue ) )
                        {
                            status.add( ProjectCore.createWarningStatus( "The " + propertyKey + "(" +
                                            propertyValue + ") server is not supported by Liferay IDE." ) );
                        }

                        break;
                    }

                    case "app.server.dir":
                    case "app.server.deploy.dir":
                    case "app.server.lib.global.dir":
                    case "app.server.parent.dir":
                    case "app.server.portal.dir":
                    {
                        IPath propertyPath = PathBridge.create( new Path( propertyValue ) );

                        if( !propertyPath.toFile().exists() )
                        {
                            status.add( ProjectCore.createWarningStatus( "The " + propertyKey + "(" +
                                            propertyValue + ") is not valid." ) );
                        }

                        break;
                    }
                    default:
                }
            }
        }

        return status;
    }
}
