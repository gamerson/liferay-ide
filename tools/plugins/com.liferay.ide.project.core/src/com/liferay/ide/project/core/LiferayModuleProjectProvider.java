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
package com.liferay.ide.project.core;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.model.ProjectName;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;


/**
 * @author Simon Jiang
 */
public abstract class LiferayModuleProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayModuleProjectProvider( Class<?>[] types )
    {
        super( types );
    }

    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        if( ! (operation instanceof NewLiferayModuleProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayPluginProjectOp" ); //$NON-NLS-1$
        }

        final NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.class.cast( operation );

        ElementList<ProjectName> projectNames = op.getProjectNames();

        return doCreateNewProject( op, monitor, projectNames );
    }

    public abstract IStatus doCreateNewProject(
        final NewLiferayModuleProjectOp op, IProgressMonitor monitor, ElementList<ProjectName> projectNames )
        throws CoreException;

    public abstract IStatus validateProjectLocation( String projectName, IPath path );

    protected IStatus createOSGIBundleProject(
        File baseLocation, File dir, String projectType, String buildType, String projectName, String className,
        String serviceName )
    {
        IStatus retVal = Status.OK_STATUS;

        final BundleContext bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();

        Collection<ServiceReference<Command>> refs;
        try
        {
            refs = bundleContext.getServiceReferences( Command.class, "(osgi.command.function=createProject)" );

            if( refs != null && refs.size() > 0 )
            {
                final Command command = bundleContext.getService( refs.iterator().next() );

                final Map<String, Object> parameters = new HashMap<>();

                parameters.put( "base", baseLocation );
                parameters.put( "dir", dir );
                parameters.put( "typeValue", projectType );
                parameters.put( "buildValue", buildType );
                parameters.put( "name", projectName );
                parameters.put( "classname", projectName );
                parameters.put( "service", projectName );

                final Object errors = command.execute( parameters );

                if( errors != null )
                {
                    retVal = ProjectCore.createErrorStatus( "Create this Project error " );
                }
            }
            else
            {
                retVal = ProjectCore.createErrorStatus( "Unable to create this Project " );
            }
        }
        catch( InvalidSyntaxException | CommandException e )
        {
            ProjectCore.createErrorStatus( "Can not invoke blade tools to create project " );
        }

        return retVal;
    }

}
