/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Simon Jiang
 */

public class LiferayServiceBuild
{

    protected IFile serviceXmlFile;

    public LiferayServiceBuild( IFile serviceXmlFile )
    {
        this.serviceXmlFile = serviceXmlFile;
    }

    protected IProject getProject()
    {
        return this.serviceXmlFile != null ? this.serviceXmlFile.getProject() : null;
    }

    private IProjectBuilder getProjectBuilder() throws CoreException
    {
        final ILiferayProject liferayProject = LiferayCore.create( getProject() );

        if( liferayProject == null )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( NLS.bind(
                Msgs.couldNotCreateLiferayProject, getProject() ) ) );
        }

        final IProjectBuilder builder = liferayProject.adapt( IProjectBuilder.class );

        if( builder == null )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( NLS.bind(
                Msgs.couldNotCreateProjectBuilder, getProject() ) ) );
        }

        return builder;
    }

    public IStatus run( IProgressMonitor monitor )
    {
        IStatus retval = null;

        if( getProject() == null )
        {
            return LiferayProjectCore.createErrorStatus( Msgs.useLiferayProjectImportWizard );
        }

        if( !ProjectUtil.isLiferayFacetedProject( getProject() ) )
        {
            return LiferayProjectCore.createErrorStatus( MessageFormat.format(
                Msgs.useConvertLiferayProject, getProject().getName() ) );
        }

        final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 100 );
        submon.subTask( "Run Service Build Process" ); //$NON-NLS-1$ //$NON-NLS-2$

        final IWorkspaceRunnable workspaceRunner = new IWorkspaceRunnable()
        {

            public void run( IProgressMonitor monitor ) throws CoreException
            {
                runBuild( submon );
            }
        };

        try
        {
            ResourcesPlugin.getWorkspace().run( workspaceRunner, submon );
        }
        catch( CoreException e1 )
        {
            retval = LiferayProjectCore.createErrorStatus( e1 );
        }

        return retval == null || retval.isOK() ? Status.OK_STATUS : retval;
    }

    private void runBuild( final IProgressMonitor monitor ) throws CoreException
    {
        final IProjectBuilder builder = getProjectBuilder();

        monitor.worked( 50 );

        IStatus retval = builder.buildService( serviceXmlFile, monitor );

        if( retval == null )
        {
            retval = LiferayProjectCore.createErrorStatus( NLS.bind( Msgs.errorRunningBuildService, getProject() ) );
        }

        if( retval == null || !retval.isOK() )
        {
            throw new CoreException( retval );
        }

        monitor.worked( 90 );
    }

    protected static class Msgs extends NLS
    {

        public static String buildingLiferayServices;
        public static String buildServices;
        public static String couldNotCreateLiferayProject;
        public static String couldNotCreateProjectBuilder;
        public static String errorRunningBuildService;
        public static String useConvertLiferayProject;
        public static String useLiferayProjectImportWizard;

        static
        {
            initializeMessages( LiferayServiceBuild.class.getName(), Msgs.class );
        }
    }
}
