/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertTrue;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IProject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class SDKProjectDeploymentTests extends ProjectCoreBase
{
    final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

    private IProject project;
    
    @AfterClass
    public static void cleanUp() throws Exception
    {
        shutDownServer();
        deleteAllWorkspaceProjects();
    }

    @Before
    public void startUp() throws Exception
    {
        super.startServer();
    }

    @Test
    public void testDeployHookPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "HookPortletPorjectTest" );
        op.setPluginType( PluginType.hook );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 hook for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeployJSFPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "JSFPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "jsf-2.x" );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeployMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "MVCPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "mvc" );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeployServiceBuilderPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ServiceBuilderPorjectTest" );
        op.setPluginType( PluginType.servicebuilder );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeploySpringMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "SpringMVCPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "spring-mvc" );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeployThemeProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ThemePorjectTest" );
        op.setPluginType( PluginType.theme );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 theme for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testDeployVaadinPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "VaadinPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testExtProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ExtPorjectTest" );
        op.setPluginType( PluginType.ext );
        project = createAntProject( op );
        
        publishToServer( project );

        String expectedMessage = "Destroying ProtocolHandler";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }

    @Test
    public void testLayoutProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "LayoutPorjectTest" );
        op.setPluginType( PluginType.layouttpl );
        project = createAntProject( op );

        deploySDKProject( project );

        String expectedMessage = "1 layout for " + project.getName() + " is available for use";
        assertTrue( checkProjectDeployed( project.getName(), expectedMessage ) );
    }
}
