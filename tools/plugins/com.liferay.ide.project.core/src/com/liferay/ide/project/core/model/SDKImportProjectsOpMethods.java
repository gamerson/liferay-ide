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
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;


/**
 * @author Simon Jiang
 */
public class SDKImportProjectsOpMethods
{

    public static final Status execute( final SDKProjectsImportOp30 op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        final Path projectLocation = op.getLocation().content();
        
        ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectLocation.toPortableString() );

        if( projectRecord == null )
        {
            final IStatus status = ProjectCore.createErrorStatus( "Project record to import is null." ); //$NON-NLS-1$
            
            retval = StatusBridge.create( status );
            
            return retval;
        }

        File projectDir = projectRecord.getProjectLocation().toFile();

        SDK sdk = SDKUtil.getSDKFromProjectDir( projectDir );

        if( sdk != null )
        {
            if( !( SDKManager.getInstance().containsSDK( sdk ) ) )
            {
                SDKManager.getInstance().addSDK( sdk );
            }
        }

        IRuntime runtime = (IRuntime) model.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME );

        try
        {
            ProjectUtil.importProject( projectRecord, runtime, sdk.getLocation().toOSString(), monitor );
        }
        catch( CoreException e )
        {
            return ProjectCore.createErrorStatus( e );
        }

        return Status.OK_STATUS;
        
        return retval;
    }

}
