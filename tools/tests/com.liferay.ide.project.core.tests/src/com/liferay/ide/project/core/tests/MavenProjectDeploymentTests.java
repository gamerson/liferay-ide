
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;

public class MavenProjectDeploymentTests extends ProjectCoreBase
{

    final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
    private IProject project;

    @Before
    public void start() throws Exception
    {
        super.startServer();
    }

    @AfterClass
    public static void cleanUp() throws Exception
    {
        shutDownServer();
        deleteAllWorkspaceProjects();
    }

    @Test
    public void testDeployMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "MVCPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "mvc" );

        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "1 portlet for MVCPortletMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "MVCPortletMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }

    @Test
    public void testDeployJSFPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "JSFPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "jsf-2.x" );

        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "1 portlet for JSFPortletMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "JSFPortletMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }

    @Test
    public void testDeploySpringMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "SpringMVCPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "spring-mvc" );

        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "1 portlet for SpringMVCPortletMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "SpringMVCPortletMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }

    @Test
    public void testDeployVaadinPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "VaadinPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );

        project = createMavenProject( op );

        File liferayDisplayXML = LiferayCore.create( project ).getDescriptorFile( "liferay-display.xml" ).getRawLocation().toFile();
        FileUtil.searchAndReplace( liferayDisplayXML, "7.4.0", "6.2.0" );
        
        File liferayPortletXML = LiferayCore.create( project ).getDescriptorFile( "liferay-portlet.xml" ).getRawLocation().toFile();
        FileUtil.searchAndReplace( liferayPortletXML, "7.4.0", "6.2.0" );
        
        project.refreshLocal( IResource.DEPTH_INFINITE, null );
        
        deployMavenProject( project );

        String expectedMessage = "1 portlet for VaadinPortletMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "VaadinPortletMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }

    @Test
    public void testDeployHookPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "HookMavenProjectTest" );
        op.setPluginType( PluginType.hook );
        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "Hook for HookMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "HookMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }

    @Test
    public void testDeployServiceBuilderPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ServiceBuilderMavenProjectTest" );
        op.setPluginType( PluginType.servicebuilder );

        createMavenProject( op );

        project = ProjectUtil.getProject( "ServiceBuilderMavenProjectTest" );
        deployMavenProject( project );

        String expectedMessage = "1 portlet for ServiceBuilderMavenProjectTest-portlet is available for use";
        assertTrue( checkProjectDeployed( "ServiceBuilderMavenProjectTest-portlet", expectedMessage ) );
    }

    @Test
    public void testDeployThemeProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ThemeMavenProjectTest" );
        op.setPluginType( PluginType.theme );
        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "1 theme for ThemeMavenProjectTest-1.0.0-SNAPSHOT is available for use";
        assertTrue( checkProjectDeployed( "ThemeMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );

    }

    public void testLayoutProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "LayoutMavenProjectTest" );
        op.setPluginType( PluginType.layouttpl );

        project = createMavenProject( op );

        deployMavenProject( project );

        String expectedMessage = "1 layout template for LayoutMavenProjectTest-1.0.0-SNAPSHOT was unregistered";
        assertTrue( checkProjectDeployed( "LayoutMavenProjectTest-1.0.0-SNAPSHOT", expectedMessage ) );
    }
    
    @Test
    public void testExtProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ExtMavenPorjectTest" );
        op.setPluginType( PluginType.ext );

        project = createMavenProject( op );

       /* deployMavenProject( project );

        String expectedMessage = "stop";
        assertTrue( checkProjectDeployed( "ExtMavenPorjectTest-1.0.0-SNAPSHOT", expectedMessage ) );*/
    }
}
