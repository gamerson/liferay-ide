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

package com.liferay.ide.project.core.tests.modules.jsf;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.modules.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.modules.jsf.NewLiferayJSFModuleProjectOpMethods;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectOpTests
{

    @Test
    public void testNewLiferayJSFModuleProjectOpProjectName() throws Exception
    {
        NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        op.setProjectName( "Test1" );
        Status projectNameOkValidationStatus1 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus1.message(), "ok" );

        op.setProjectName( "#Test1" );
        Status projectNameErrorValidationStatus = op.getProjectName().validation();
        assertEquals( projectNameErrorValidationStatus.message(), "The project name is invalid." );

        op.setProjectName( "Test1_Abc" );
        Status projectNameOkValidationStatus2 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus2.message(), "ok" );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpProjectExisted() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectName( "Test2" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );

        NewLiferayJSFModuleProjectOp newProjectNameop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        newProjectNameop.setProjectName( "Test2" );
        Status projectNameExistedValidationStatus = newProjectNameop.getProjectName().validation();
        assertEquals(
            projectNameExistedValidationStatus.message(), "A project with that name(ignore case) already exists." );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpDefaultBuildType() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectName( "Test3" );
        existedProjectop.setProjectProvider( "maven-jsf-module" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        NewLiferayJSFModuleProjectOp newBuildTypeop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        DefaultValueService buildTypeDefaultService =
            newBuildTypeop.getProjectProvider().service( DefaultValueService.class );
        assertEquals( buildTypeDefaultService.value(), "maven-jsf-module" );
    }

    @Test
    public void testNewLiferayJSFModuleStandardAloneProject() throws Exception
    {
        NewLiferayJSFModuleProjectOp mavenProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        mavenProjectop.setProjectName( "Test4" );
        mavenProjectop.setProjectProvider( "maven-jsf-module" );
        Status mavenProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            mavenProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( mavenProjectStatus.ok() );

        IProject mavenProject = CoreUtil.getProject( "Test4" );

        assertNotNull( mavenProject );

        NewLiferayJSFModuleProjectOp graldeProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        graldeProjectop.setProjectName( "Test5" );
        graldeProjectop.setProjectProvider( "gradle-jsf-module" );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            graldeProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IProject gradleProject = CoreUtil.getProject( "Test5" );

        assertNotNull( gradleProject );
    }

    @Test
    public void testNewLiferayJSFModuleWorkspaceProject() throws Exception
    {
        IPath importWorkspaceProjectLocation = importWorkspaceProject( "testWorkspace" );

        ILiferayProjectImporter gradleImporter = LiferayCore.getImporter( "gradle" );
        gradleImporter.importProjects( importWorkspaceProjectLocation.toOSString(), new NullProgressMonitor() );

        IProject workspaceProject = CoreUtil.getProject( "testWorkspace" );
        assertNotNull( workspaceProject );

        NewLiferayJSFModuleProjectOp gradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        gradleProjectop.setProjectName( "Test6" );
        gradleProjectop.setProjectProvider( "gradle-jsf-module" );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            gradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IPath workspaceWarLocation = importWorkspaceProjectLocation.append( "wars" );
        IPath projectLocation = workspaceWarLocation.append( gradleProjectop.getProjectName().content() );
        IProject gradleProject = CoreUtil.getProject( projectLocation.toFile() );
        assertNotNull( gradleProject );

    }

    private IPath importWorkspaceProject( String name ) throws Exception
    {
        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        IPath wordkspaceProjectLocation = CoreUtil.getWorkspaceRoot().getLocation();
        ZipUtil.unzip( projectZipFile, wordkspaceProjectLocation.toFile() );

        final IPath projectFolder = wordkspaceProjectLocation.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        return projectFolder;
    }
}
