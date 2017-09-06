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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.ModuleCoreUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Simon Jiang
 * @author Andy Wu
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewLiferayModuleProjectOpTests
{

    @Test
    @Ignore
    public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    @Ignore
    public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    @Ignore
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTest", op.getComponentName().content( true ) );
    }

    @Test
    @Ignore
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
    @Ignore
    public void testNewLiferayModuleProjectDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    @Ignore
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

    @Test
    public void testNewLiferayModuleProjectNameValidataionService() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        assertTrue( op.validation().ok() );

        op.setProjectName( "1" );

        assertTrue( op.getProjectName().validation().ok() );

        op.setProjectName( "a" );

        assertTrue( op.getProjectName().validation().ok() );

        op.setProjectName( "A" );

        assertTrue( op.getProjectName().validation().ok() );

        op.setProjectName( "my-test-project-" );

        assertFalse( op.getProjectName().validation().ok() );

        op.setProjectName( "my-test-project." );

        assertFalse( op.getProjectName().validation().ok() );

        op.setProjectName( "my-test-project_" );

        assertFalse( op.getProjectName().validation().ok() );
    }

    @Test
    public void testNewLiferayModuleProjectNewProperties() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-properties-in-portlet" );

        op.setProjectTemplateName( "portlet" );
        op.setComponentName( "Test" );

        PropertyKey pk = op.getPropertyKeys().insert();

        pk.setName( "property-test-key" );
        pk.setValue( "property-test-value" );

        Status exStatus =
            NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( "OK", exStatus.message() );

        IProject modPorject = CoreUtil.getProject( op.getProjectName().content() );
        modPorject.open( new NullProgressMonitor() );

        SearchFilesVisitor sv = new SearchFilesVisitor();
        List<IFile> searchFiles = sv.searchFiles( modPorject, "TestPortlet.java" );
        IFile componentClassFile = searchFiles.get( 0 );

        assertEquals( componentClassFile.exists(), true );

        String actual = CoreUtil.readStreamToString( componentClassFile.getContents() );

        assertTrue( actual, actual.contains( "\"property-test-key=property-test-value\"" ) );
    }

    @Test
    public void testJspValidationSupportDefaultValue() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-jsp-validation-support" );

        final List<String> templates = Arrays.asList( BladeCLI.getProjectTemplates() );

        for( String template : templates )
        {
            if( template.equals( "fragment" ) )
            {
                continue;
            }

            op.setProjectTemplateName( template );

            assertEquals(
                op.getAddJspValidationSupport().content(),
                Arrays.asList( ModuleCoreUtil.TEMPLATES_WITH_JSP ).contains( template ) );
        }
    }

    @Test
    public void testJspValidationSupportAdded() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-jsp-validation-support-added" );

        final String[] templatesWithJsp = ModuleCoreUtil.TEMPLATES_WITH_JSP;

        final Random random = new Random();

        final int randomIndex = random.nextInt( templatesWithJsp.length );

        op.setProjectTemplateName( templatesWithJsp[randomIndex] );

        final Status status =
            NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        waitForBuildAndValidation();

        assertTrue( status.ok() );

        final String projectName = op.getProjectName().content();

        final IProject project = CoreUtil.getProject( projectName );

        assertTrue( project.hasNature( "org.eclipse.wst.common.modulecore.ModuleCoreNature" ) );
        assertTrue( project.hasNature( "org.eclipse.wst.common.project.facet.core.nature" ) );

        final File configFile =
            project.getLocation().append( ".settings" ).append( "org.eclipse.wst.common.component" ).toFile();

        assertTrue( configFile.exists() );

        assertEquals(
            FileUtil.readContents( configFile, true ),
            ModuleCoreUtil.wstConfigContent.replace( "PROJECT_NAME", projectName ) );
    }

    @Test
    public void testNewLiferayPortletProviderNewProperties() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-properties-in-portlet-provider" );
        op.setComponentName( "Test" );
        op.setProjectTemplateName( "portlet-provider" );

        PropertyKey pk = op.getPropertyKeys().insert();

        pk.setName( "property-test-key" );
        pk.setValue( "property-test-value" );

        Status exStatus =
            NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertTrue( exStatus.message(), exStatus.ok() );

        IProject modPorject = CoreUtil.getProject( op.getProjectName().content() );
        modPorject.open( new NullProgressMonitor() );

        IFile testAddPortletProvider = modPorject.getFile(
            "src/main/java/test/properties/in/portlet/provider/portlet/TestAddPortletProvider.java" );

        assertTrue( testAddPortletProvider.exists() );

        SearchFilesVisitor sv = new SearchFilesVisitor();
        List<IFile> searchFiles = sv.searchFiles( modPorject, "TestAddPortletProvider.java" );
        IFile componentClassFile = searchFiles.get( 0 );

        assertEquals( componentClassFile.exists(), true );

        String actual = CoreUtil.readStreamToString( componentClassFile.getContents() );

        assertTrue( actual.contains( "property-test-key=property-test-value" ) );
    }

    protected void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );

            Job.getJobManager().join( "CheckingGradleConfiguration", new NullProgressMonitor() );

            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
            failTest( e );
        }
        catch( IllegalArgumentException e )
        {
            failTest( e );
        }
        catch( OperationCanceledException e )
        {
            failTest( e );
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    protected static void failTest( Exception e )
    {
        StringWriter s = new StringWriter();
        e.printStackTrace( new PrintWriter( s ) );
        fail( s.toString() );
    }
}
