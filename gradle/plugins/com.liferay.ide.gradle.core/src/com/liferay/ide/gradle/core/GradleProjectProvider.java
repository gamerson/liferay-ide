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

package com.liferay.ide.gradle.core;

import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectJob;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.gradle.jarjar.org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GradleProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>
{

    public GradleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( LiferayNature.hasNature( project ) && GradleProjectNature.INSTANCE.isPresentOn( project ) )
                {
                    return new LiferayGradleProject( project );
                }
            }
            catch( Exception e )
            {
                // ignore errors
            }
        }

        return retval;
    }

    @Override
    public IStatus createNewProject( NewLiferayModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        String className = op.getComponentName().content();

        final String serviceName = op.getServiceName().content();

        final String packageName = op.getPackageName().content();

        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        final List<String> properties = new ArrayList<String>();

        for( PropertyKey propertyKey : propertyKeys )
        {
            properties.add( propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
        }

        final String projectTemplateName = op.getProjectTemplateName().content();

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append( "-d \"" + location.toFile().getAbsolutePath() + "\" " );
        sb.append( "-t " + projectTemplateName + " " );

        if( className != null )
        {
            sb.append( "-c " + className + " " );
        }

        if( serviceName != null )
        {
            sb.append( "-s " + serviceName + " " );
        }

        if( packageName != null )
        {
            sb.append( "-p " + packageName + " " );
        }

        sb.append( "\"" + projectName + "\" " );

        try
        {
            final String[] ret = BladeCLI.execute( sb.toString() );

            final String errors = checkForErrors(ret);

            if( errors.length() > 0 )
            {
                retval = GradleCore.createErrorStatus( "Project create error: " + errors );
                return retval;
            }

            if( ret.length == 0 )
            {
                ElementList<ProjectName> projectNames = op.getProjectNames();
                
                projectNames.insert().setName( projectName );
                
                IPath projecLocation = location;

                final String lastSegment = location.lastSegment();

                if( location != null && location.segmentCount() > 0 )
                {
                    if( !lastSegment.equals( projectName ) )
                    {
                        projecLocation = location.append( projectName );
                    }
                }

                ProjectImportConfiguration configuration = new ProjectImportConfiguration();
                GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );
                configuration.setGradleDistribution( from );
                configuration.setProjectDir( projecLocation.toFile() );
                configuration.setApplyWorkingSets( false );
                configuration.setWorkingSets( new ArrayList<String>() );
                SynchronizeGradleProjectJob synchronizeGradleProjectJob =
                    new SynchronizeGradleProjectJob(
                        configuration.toFixedAttributes(), configuration.getWorkingSets().getValue(),
                        AsyncHandler.NO_OP );

                if( CoreUtil.isNullOrEmpty( className ) )
                {
                    className = WordUtils.capitalize( projectName );
                }

                if( projectTemplateName.equals( "servicebuilder" ) || projectTemplateName.equals( "portlet" ) ||
                    projectTemplateName.equals( "mvcportlet" ) )
                {
                    if( !className.contains( "Portlet" ) )
                    {
                        className += "Portlet";
                    }
                }

                final String finalClassName = className + ".java";

                synchronizeGradleProjectJob.addJobChangeListener( new JobChangeAdapter()
                {

                    @Override
                    public void done( IJobChangeEvent event )
                    {
                        if( event.getResult().isOK() )
                        {
                            IProject moduleProject = CoreUtil.getProject( projectName );

                            if( moduleProject.exists() )
                            {
                                List<IFile> files =
                                    new SearchFilesVisitor().searchFiles( moduleProject, finalClassName );

                                for( IFile file : files )
                                {
                                    if( file.exists() )
                                    {
                                        try
                                        {
                                            NewLiferayModuleProjectOpMethods.addProperties( file.getLocation().toFile(), properties );
                                            file.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                                        }
                                        catch( Exception e )
                                        {
                                            ProjectCore.createErrorStatus(
                                                "Can't update properties setting for class file", e );
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

                synchronizeGradleProjectJob.schedule();
            }
        }
        catch( Exception e )
        {
            retval = ProjectCore.createErrorStatus( "can't create module project.", e );
        }

        return retval;
    }

    private String checkForErrors( String[] lines )
    {
        boolean hasErrors = false;
        final StringBuilder errors = new StringBuilder();

        for( String line : lines )
        {
            if( line.startsWith( "Error" ) ) {
                hasErrors = true;
            }
            else if( hasErrors )
            {
                errors.append( line );
            }
        }

        return errors.toString();
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        return retval;
    }

}
