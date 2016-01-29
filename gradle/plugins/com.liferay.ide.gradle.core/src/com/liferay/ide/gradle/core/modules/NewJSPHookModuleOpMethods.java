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

package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.gradle.core.JSPHookProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseOpMethods;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Terry Jia
 */
public class NewJSPHookModuleOpMethods extends BaseOpMethods
{

    public static final Status execute( final NewJSPHookModuleOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final JSPHookProjectProvider provider = new JSPHookProjectProvider();

            final IStatus status = provider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay JSP Hook module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

}
