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
package com.liferay.ide.project.core.tests.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NewLiferayModuleProjectOpTests
{

    @BeforeClass
    public static void setupBladeCLIPrefs() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/" );
    }

    @AfterClass
    public static void restoreBladeCLIPrefsToDefault() throws Exception
    {
        IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        final String defaultValue = defaults.get( BladeCLI.BLADE_CLI_REPO_URL, "" );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, defaultValue );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultLocation() throws Exception
    {
        final URL wsZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/emptyLiferayWorkspace.zip" );

        final File wsZipFile = new File( FileLocator.toFileURL( wsZipUrl ).getFile() );

        File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRoot().getLocation().toFile();

        ZipUtil.unzip( wsZipFile, eclipseWorkspaceLocation );

        File wsFolder = new File( eclipseWorkspaceLocation, "emptyLiferayWorkspace" );

        ProjectImportTestUtil.importExistingProject( wsFolder, new NullProgressMonitor() );

        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "mvc-portlet" );

        op.setProjectProvider( "maven-module" );

        //don't put maven type project inside liferay-workspace
        assertTrue( op.getLocation().content().toFile().equals( eclipseWorkspaceLocation ) );

        op.setProjectProvider( "gradle-module" );

        op.setProjectTemplateName( "theme" );

        //don't put gradle type theme project inside liferay-workspace
        assertTrue( op.getLocation().content().toFile().equals( eclipseWorkspaceLocation ) );

        op.setProjectTemplateName( "mvc-portlet" );

        //put gradle type project inside liferay-workspace
        assertTrue( op.getLocation().content().toPortableString().contains( "emptyLiferayWorkspace/modules" ) );

        IProject project = CoreUtil.getProject( "emptyLiferayWorkspace" );

        if( project != null && project.isAccessible() && project.exists() )
        {
            project.delete( true, true, new NullProgressMonitor() );
        }

        op.setProjectTemplateName( "service-builder" );

        //no liferay-workspace
        assertTrue( op.getLocation().content().toFile().equals( eclipseWorkspaceLocation ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTest", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectTemplateName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "activator" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "mvc-portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service-wrapper" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service-builder" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectPackageDefaultValueService() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "my.test.project", op.getPackageName().content( true ) );

        op.setProjectName( "my.test.foo" );

        assertEquals( "my.test.foo", op.getPackageName().content( true ) );

        op.setProjectName( "my_test_foo1" );

        op.setProjectTemplateName( "ServiceWrapper" );

        assertEquals( "my.test.foo1", op.getPackageName().content( true ) );
    }

}
