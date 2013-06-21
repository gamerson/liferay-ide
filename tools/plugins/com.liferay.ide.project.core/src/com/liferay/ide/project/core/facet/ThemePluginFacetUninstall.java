/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.facet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;


/**
 * @author Greg Amerson
 */
public class ThemePluginFacetUninstall extends PluginFacetUninstall
{

    @Override
    public void execute( IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor )
        throws CoreException
    {

        super.execute( project, fv, config, monitor );

        removeThemeCSSBuilder( project );
    }

    protected void removeThemeCSSBuilder( IProject project ) throws CoreException
    {

        if( project == null )
        {
            return;
        }

        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();
        List<ICommand> newCommands = new ArrayList<ICommand>();

        for( ICommand command : commands )
        {
            if( !( "com.liferay.ide.eclipse.theme.core.cssBuilder".equals( command.getBuilderName() ) ) ) //$NON-NLS-1$
            {
                newCommands.add( command );
            }
        }

        desc.setBuildSpec( newCommands.toArray( new ICommand[0] ) );
    }

}
