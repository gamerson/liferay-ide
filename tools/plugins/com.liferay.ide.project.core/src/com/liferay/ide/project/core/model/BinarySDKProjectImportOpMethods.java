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

import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;


/**
 * @author Simon Jiang
 */
public class BinarySDKProjectImportOpMethods
{
    public static final Status execute( final BinarySDKProjectImportOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay plugin project...", 100 );

        Status retval = Status.createOkStatus();

        try
        {
            Path pluginLocation = op.getPluginLocation().content();

            if ( pluginLocation == null || pluginLocation.isEmpty() )
            {
                return Status.createErrorStatus( "Please select a valid plugin war file." );
            }

            BinaryProjectRecord binaryProjectRecord = new BinaryProjectRecord( PathBridge.create( op.getPluginLocation().content() ).toFile() );

            IPath sdkPath = PathBridge.create( op.getSdkLocation().content() );

            if ( sdkPath == null || sdkPath.isEmpty() || !sdkPath.toFile().exists() )
            {
                return Status.createErrorStatus( "The sdk path is invalid." );
            }

            SDK sdk = SDKUtil.createSDKFromLocation( sdkPath );

            if ( sdk == null || !sdk.validate().isOK() )
            {
                return Status.createErrorStatus( "Selected SDK is invalid." );
            }

            ProjectRecord createSDKPluginProject = ProjectImportUtil.createSDKPluginProject(null, binaryProjectRecord, sdk);

            if ( createSDKPluginProject == null )
            {
                return Status.createErrorStatus( "Create sdk project failed." );
            }

            ProjectImportUtil.importProject(createSDKPluginProject.getProjectLocation(),new NullProgressMonitor(), null );
        }
        catch( Exception e )
        {
            return Status.createErrorStatus( e.getMessage() );
        }
        return retval;
    }
}
