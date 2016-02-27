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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class NewModuleOpMethods
{

    public static final Status execute( final NewModuleOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = Status.createOkStatus();

        try
        {
            List<String> properties = new ArrayList<String>();

            String projectName = op.getProjectName().content( true );
            String packageName = op.getPackageName().content( true );
            String className = op.getComponentName().content( true );
            String templateName = op.getProjectTemplateName().content( true );
            String serviceName = op.getServiceName().content( true );

            ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

            for( PropertyKey propertyKey : propertyKeys )
            {
                properties.add( propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
            }

            ILiferayModuleOperation moduleOperation =
                LiferayModuleOperationFactory.getInstance().getModuleOperation( templateName );

            moduleOperation.init( projectName, packageName, className, templateName, serviceName, properties );

            moduleOperation.doExecute();

        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }
}
