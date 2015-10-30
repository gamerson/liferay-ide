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

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;


/**
 * @author Simon Jiang
 */
public class BinarySDKProjectsImportOpMethods
{
    public static final Status execute( final BinarySDKProjectsImportOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay plugin project...", 100 );

        Status retval = Status.createOkStatus();

        try
        {
            ElementList<ProjectNamedItem> selectedProjects = op.getSelectedProjects();

            for( ProjectNamedItem projectNamedItem : selectedProjects )
            {
                Value<String> location = projectNamedItem.getLocation();

                if ( location != null && !location.empty() )
                {
                    String pluginLocation = location.content(true);

                    BinaryProjectRecord binaryProjectRecord = new BinaryProjectRecord( new File(pluginLocation) );

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

                    ProjectImportUtil.importProject(createSDKPluginProject.getProjectLocation(), monitor, null );
                }
            }
        }
        catch( Exception e )
        {
            return Status.createErrorStatus( e.getMessage() );
        }
        return retval;
    }
}
