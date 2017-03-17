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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.TaskListUtility;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;
import org.eclipse.wst.validation.internal.operations.MessageInfo;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class NewGradleJSFModuleProjectProvider extends NewMavenJSFModuleProjectProvider
{
    public final static String VALIDATOR_ID = "org.eclipse.wst.html.core.HTMLValidator";

    @Override
    public IStatus createNewProject( NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;;

        try
        {
            IPath projectLocation = createArchetypeProject(op, monitor );

            IPath buildGradlePath = projectLocation.append( "build.gradle" );

            if( buildGradlePath.toFile().exists() )
            {
                try
                {
                    boolean hasLiferayWorkspace = LiferayWorkspaceUtil.isWorkspace( projectLocation.toFile() );

                    if( hasLiferayWorkspace )
                    {
                        List<String> buildGradleContents =
                            Files.readAllLines( Paths.get( buildGradlePath.toFile().toURI() ), StandardCharsets.UTF_8 );
                        List<String> modifyContents = new ArrayList<String>();

                        for( String line : buildGradleContents )
                        {
                            if( line.startsWith( "apply" ) )
                            {
                                continue;
                            }

                            modifyContents.add( line );
                        }

                        Files.write( buildGradlePath.toFile().toPath(), modifyContents, StandardCharsets.UTF_8 );
                    }
                }
                catch( Exception e )
                {
                    ProjectCore.logError( "Failed to check LiferayWorkspace project. " );
                }
            }

            IPath buildPom = projectLocation.append( "pom.xml" );

            if( buildPom.toFile().exists() )
            {
                buildPom.toFile().delete();
            }

            ILiferayProjectImporter importer = LiferayCore.getImporter( "gradle" );
            IStatus canImport = importer.canImport( projectLocation.toOSString() );

            if( canImport.getCode() != Status.ERROR )
            {
                importer.importProjects( projectLocation.toOSString(), monitor );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( LiferayCore.createErrorStatus( e ) );
        }

        return retval;
    }
}