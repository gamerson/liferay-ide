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

package com.liferay.ide.swtbot.project.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizardSecondPageWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardTests extends BaseNewLiferayModuleProjectWizard
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    Tree projectTree = ide.getPackageExporerView().getProjectTree();

    NewLiferayModuleProjectWizard createModuleProjectWizard = new NewLiferayModuleProjectWizard( bot );

    NewLiferayModuleProjectWizardSecondPageWizard createModuleProjectSecondPageWizard =
        new NewLiferayModuleProjectWizardSecondPageWizard( bot );

    @AfterClass
    public static void cleanAll()
    {
        ide.closeShell( NEW_LIFERAY_MODULE_PROJECT );
        ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    @BeforeClass
    public static void switchToLiferayWorkspacePerspective()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayWorkspacePerspective().activate();
        ide.getProjectExplorerView().show();
    }

    @Test
    public void validationProjectName()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();
        sleep();

        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, createModuleProjectWizard.getValidationMsg() );
        assertFalse( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.getProjectName().setText( "." );

        assertEquals( " '.'" + IS_AN_INVALID_NAME_ON_PLATFORM, createModuleProjectWizard.getValidationMsg() );
        assertFalse( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.getProjectName().setText( "/" );
        assertEquals(
            " /" + IS_AN_INVALID_CHARACTER_IN_RESOURCE_NAME + "'/'.", createModuleProjectWizard.getValidationMsg() );
        assertFalse( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.getProjectName().setText( "$" );
        assertEquals( THE_PROJECT_NAME_IS_INVALID, createModuleProjectWizard.getValidationMsg() );
        assertFalse( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.getProjectName().setText( StringPool.BLANK );
        assertEquals( PROJECT_NAME_MUST_BE_SPECIFIED, createModuleProjectWizard.getValidationMsg() );
        assertFalse( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.getProjectName().setText( "a" );
        assertEquals(
            ENTER_A_NAME_AND_CHOOSE_A_TEMPlATE_FOR_A_NEW_LIFERAY_MODULE_MESSAGE,
            createModuleProjectWizard.getValidationMsg() );
        assertTrue( createModuleProjectWizard.finishBtn().isEnabled() );

        createModuleProjectWizard.cancel();
    }

    @Test
    public void validationTheSecondPage()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();
        sleep();

        createModuleProjectWizard.getProjectName().setText( "test" );
        createModuleProjectWizard.next();

        createModuleProjectSecondPageWizard.getComponentClassName().setText( "@@" );
        sleep();
        assertEquals( INVALID_CLASS_NAME, createModuleProjectSecondPageWizard.getValidationMsg() );
        createModuleProjectSecondPageWizard.getComponentClassName().setText( "testClassName" );
        sleep();

        createModuleProjectSecondPageWizard.getPackageName().setText( "!!" );
        sleep();
        assertEquals( INVALID_PACKAGE_NAME, createModuleProjectSecondPageWizard.getValidationMsg() );
        createModuleProjectSecondPageWizard.getPackageName().setText( "testPackageName" );
        sleep();

        createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setFocus();
        assertEquals( NAME_MUST_BE_SPECIFIED, createModuleProjectSecondPageWizard.getValidationMsg() );
        assertTrue( createModuleProjectSecondPageWizard.getDeleteBtn().isEnabled() );
        createModuleProjectSecondPageWizard.getDeleteBtn().click();

        createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setText( 2, "a" );
        createModuleProjectSecondPageWizard.getProperties().setFocus();
        sleep();
        assertEquals( VALUE_MUST_BE_SPECIFIED, createModuleProjectSecondPageWizard.getValidationMsg() );
        sleep( 2000 );
        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setText( 2, "b" );
        createModuleProjectSecondPageWizard.cancel();
    }

    @Test
    public void createMvcportletModuleProject()
    {
        String projectName = "testMvcportletProject";

        newLiferayModuleProject(
            GRADLE, projectName, MVC_PORTLET, eclipseWorkspace, true, eclipseWorkspace + "newFolder", StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, true );

        createModuleProjectSecondPageWizard.waitForPageToClose();

        String javaFileName = "TestMvcportletProjectPortlet.java";
        String javaContent = "extends MVCPortlet";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testMvcportletProject.portlet", javaFileName );

        String buildGradleFileName = "build.gradle";
        String buildGradleContent =
            "apply plugin: \"com.liferay.plugin\"\n\ndependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( buildGradleContent, projectName, projectName, buildGradleFileName );

        ide.getPackageExporerView().deleteResouceByName( "testMvcportletProject", true );
    }

    @Test
    public void createMvcportletModuleProjectInLiferayWorkspace() throws IOException
    {

        String liferayWorkspaceName = "liferayWorkspace";
        String projectName = "testMvcportletInLS";

        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
        sleep( 2000 );

        NewLiferayWorkspaceProjectWizard newLiferayWorkspace = new NewLiferayWorkspaceProjectWizard( bot );

        newLiferayWorkspace.getWorkspaceName().setText( liferayWorkspaceName );
        newLiferayWorkspace.finish();
        newLiferayWorkspace.waitForPageToClose();

        newLiferayModuleProject(
            GRADLE, projectName, MVC_PORTLET, eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false,
            eclipseWorkspace + "newFolder", StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        createModuleProjectSecondPageWizard.waitForPageToClose();

        String javaFileName = "TestMvcportletInLSPortlet.java";
        String javaContent = "extends MVCPortlet";

        openEditorAndCheck(
            javaContent, projectName, liferayWorkspaceName, "modules", projectName, "src/main/java",
            "testMvcportletInLS.portlet", javaFileName );

        String buildGradleFileName = "build.gradle";
        String buildGradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            buildGradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

        killGradleProcess();

        DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );

        DeleteResourcesContinueDialog continueDeleteResources =
            new DeleteResourcesContinueDialog( bot, "Delete Resources" );

        projectTree.getTreeItem( liferayWorkspaceName ).doAction( DELETE );
        sleep( 2000 );

        deleteResources.getDeleteFromDisk().select();
        deleteResources.confirm();

        try
        {
            continueDeleteResources.getContinueBtn().click();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createServiceModuleProject()
    {
        String projectName = "testServiceProject";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            "*lifecycleAction", true );

        String javaFileName = "TestServiceProject.java";
        String javaContent = "implements LifecycleAction";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testServiceProject", javaFileName );

        String buildGradleFileName = "build.gradle";;
        String buildGradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( buildGradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createServiceBuilderModuleProject()
    {
        String projectName = "testServiceBuilderProject";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE_BUILDER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue( projectTree.expandNode( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-api" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service", "service.xml" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String apiContent =
            "dependencies {\n\tcompileOnly group: \"biz.aQute.bnd\", name: \"biz.aQute.bndlib\", version: \"3.1.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.osgi.util\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"";

        openEditorAndCheck( apiContent, projectName, projectName, projectName + "-api", buildGradleFileName );

        String serviceContent = "buildService {\n\tapiDir = \"../testServiceBuilderProject-api/src/main/java\"";
        openEditorAndCheck( serviceContent, projectName, projectName, projectName + "-service", buildGradleFileName );

        projectTree.expandNode( projectName, projectName + "-service" ).doAction( "Liferay", "build-service" );
        sleep( 10000 );

        try
        {
            projectTree.expandNode( projectName ).doAction( "Gradle", "Refresh Gradle Project" );
        }
        catch( Exception e )
        {
            projectTree.expandNode( projectName ).doAction( "Gradle", "Refresh Gradle Project" );
        }

        sleep( 10000 );
        assertTrue(
            projectTree.expandNode(
                projectName, projectName + "-api", "src/main/java", "testServiceBuilderProject.service" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, projectName + "-service", "src/main/java",
                "testServiceBuilderProject.model.impl" ).isVisible() );

    }

    @Test
    public void createActivatorModuleProject()
    {
        String projectName = "testActivatorProject";

        newLiferayModuleProject(
            GRADLE, projectName, ACTIVATOR, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );

    }

    @Test
    public void createApiModuleProject()
    {
        String projectName = "testApiProject";

        newLiferayModuleProject(
            GRADLE, projectName, API, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testApiProject.api", "TestApiProject.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";

        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createContentTargetingReportModuleProject()
    {
        String projectName = "testContentTargetingReportProject";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_REPORT, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testContentTargetingReportProject.content.targeting.report",
                "TestContentTargetingReportProjectReport.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );

    }

    @Test
    public void createContentTargetingRuleModuleProject()
    {
        String projectName = "testContentTargetingRuleProject";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_RULE, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testContentTargetingRuleProject.content.targeting.rule",
                "TestContentTargetingRuleProjectRule.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProject()
    {
        String projectName = "testContentTargetingTrackingActionProject";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_TRACKING_ACTION, eclipseWorkspace, false, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java",
                "testContentTargetingTrackingActionProject.content.targeting.tracking.action",
                "TestContentTargetingTrackingActionProjectTrackingAction.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createControlMenuEntryModuleProject()
    {
        String projectName = "testControlMenuEntryProject";

        newLiferayModuleProject(
            GRADLE, projectName, CONTROL_MENU_ENTRY, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.control.menu.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testControlMenuEntryProject.control.menu",
                "TestControlMenuEntryProjectProductNavigationControlMenuEntry.java" ).isVisible() );
    }

    @Test
    public void createFormFieldModuleProject()
    {
        String projectName = "testFormFieldProject";

        newLiferayModuleProject(
            GRADLE, projectName, FORM_FIELD, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field",
                "TestFormFieldProjectDDMFormFieldRenderer.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field",
                "TestFormFieldProjectDDMFormFieldType.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.dynamic.data.mapping.api\", version: \"3.2.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.dynamic.data.mapping.form.field.type\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );

    }

    @Test
    public void createPanelAppModuleProject()
    {
        String projectName = "testPanelAppProject";

        newLiferayModuleProject(
            GRADLE, projectName, PANEL_APP, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.application.list" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.application.list",
                "TestPanelAppProjectPanelApp.java" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.application.list",
                "TestPanelAppProjectPanelCategory.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode( projectName, "src/main/java", "testPanelAppProject.constants" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.constants",
                "TestPanelAppProjectPanelCategoryKeys.java" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.constants",
                "TestPanelAppProjectPortletKeys.java" ).isVisible() );

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "testPanelAppProject.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPanelAppProject.portlet",
                "TestPanelAppProjectPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.application.list.api\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createPortletModuleProject()
    {
        String projectName = "testPortletProject";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "testPortletProject.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProject.portlet",
                "TestPortletProjectPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createPortletConfigurationIconModuleProject()
    {
        String projectName = "testPortletConfigurationIconProject";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_CONFIGURATION_ICON, eclipseWorkspace, false, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletConfigurationIconProject.portlet.configuration.icon",
                "TestPortletConfigurationIconProjectPortletConfigurationIcon.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createPortletProviderModuleProject()
    {
        String projectName = "testPortletProviderProject";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_PROVIDER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProviderProject.constants" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProviderProject.constants",
                "TestPortletProviderProjectPortletKeys.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProviderProject.constants",
                "TestPortletProviderProjectWebKeys.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode( projectName, "src/main/java", "testPortletProviderProject.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProviderProject.portlet",
                "TestPortletProviderProjectAddPortletProvider.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletProviderProject.portlet",
                "TestPortletProviderProjectPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createPortletToolbarContributorModuleProject()
    {
        String projectName = "testPortletToolbarContributorProject";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_TOOLBAR_CONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testPortletToolbarContributorProject.portlet.toolbar.contributor",
                "TestPortletToolbarContributorProjectPortletToolbarContributor.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createRestModuleProject()
    {
        String projectName = "testRestProject";

        newLiferayModuleProject(
            GRADLE, projectName, REST, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testRestProject.application",
                "TestRestProjectApplication.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\", version: \"2.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createServiceWrapperModuleProject()
    {
        String projectName = "testServiceWrapperProject";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE_WRAPPER_UPPER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, "*BookmarksEntryLocalServiceWrapper", true );

        sleep( 5000 );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", projectName, "TestServiceWrapperProject.java" ).isVisible() );

        String javaFileName = "TestServiceWrapperProject.java";
        String javaContent = "extends BookmarksEntryLocalServiceWrapper";

        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java", projectName, javaFileName );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createSimulationPanelEntryModuleProject()
    {
        String projectName = "testSimulationPanelEntryProject";

        newLiferayModuleProject(
            GRADLE, projectName, SIMULATION_PANEL_ENTRY, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testSimulationPanelEntryProject.application.list",
                "TestSimulationPanelEntryProjectSimulationPanelApp.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.application.list.api\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.simulation\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.simulation.web\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createTemplateContextContributorModuleProject()
    {
        String projectName = "testTemplateContextContributorProject";

        newLiferayModuleProject(
            GRADLE, projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testTemplateContextContributorProject.context.contributor",
                "TestTemplateContextContributorProjectTemplateContextContributor.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createThemeModuleProject()
    {
        String projectName = "testThemeProject";

        newLiferayModuleProject(
            GRADLE, projectName, THEME, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue( projectTree.expandNode( projectName, "src", "main", "webapp", "css", "_custom.scss" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "apply plugin: \"com.liferay.portal.tools.theme.builder\"\n\ndependencies {\n\tparentThemes group: \"com.liferay\", name: \"com.liferay.frontend.theme.styled\", version: \"2.0.13\"\n\tparentThemes group: \"com.liferay\", name: \"com.liferay.frontend.theme.unstyled\", version: \"2.0.13\"\n\n\tportalCommonCSS group: \"com.liferay\", name: \"com.liferay.frontend.css.common\", version: \"2.0.3\"\n\n\tthemeBuilder group: \"com.liferay\", name: \"com.liferay.portal.tools.theme.builder\", version: \"1.1.3\"";

        openEditorAndCheck( gradleContent, projectName, projectName, buildGradleFileName );
    }

    @Test
    public void createThemeContributorModuleProject()
    {
        String projectName = "testThemeContributorProject";

        newLiferayModuleProject(
            GRADLE, projectName, THEME_CONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css", projectName,
                "_body.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css", projectName,
                "_control_menu.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css", projectName,
                "_product_menu.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css", projectName,
                "_simulation_panel.scss" ).isVisible() );

        // build.gradle is empry in theme-contributor template
        assertTrue( projectTree.expandNode( projectName, "build.gradle" ).isVisible() );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

    }

}
