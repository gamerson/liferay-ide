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

import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizardSecondPageWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Sunny Shi
 */
public class NewLiferayMavenModuleProjectWizardTests extends BaseNewLiferayModuleProjectWizard
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

    NewLiferayModuleProjectWizard createMavenModuleProjectWizard = new NewLiferayModuleProjectWizard( bot );

    NewLiferayModuleProjectWizardSecondPageWizard createMavenModuleProjectSecondPageWizard =
        new NewLiferayModuleProjectWizardSecondPageWizard( bot );

    @After
    public void clean()
    {
        ide.closeShell( NEW_LIFERAY_MODULE_PROJECT );

        // if( addedProjects() )
        // {
        // ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
        // }
    }

    @BeforeClass
    public static void switchToLiferayWorkspacePerspective()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayWorkspacePerspective().activate();
        ide.getProjectExplorerView().show();
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

    }

    @Test
    public void createMvcportletModuleProject()
    {
        String projectName = "testMvcportletProject";

        newLiferayModuleProject(
            MAVEN, projectName, MVC_PORTLET, eclipseWorkspace, true, eclipseWorkspace + "newFolder", StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, true );

        createMavenModuleProjectSecondPageWizard.waitForPageToClose();

        String pomXmlFileName = "pom.xml";

        String pomContent = "<artifactId>testMvcportletProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testMvcportletProject.portlet",
                "TestMvcportletProjectPortlet.java" ).isVisible() );
    }

    @Test
    public void createServiceModuleProject()
    {
        String projectName = "testServiceProject";

        newLiferayModuleProject(
            MAVEN, projectName, SERVICE, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            "*lifecycleAction", true );

        String javaFileName = "TestServiceProject.java";
        String javaContent = "service = LifecycleAction.class";
        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testServiceProject</artifactId>";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testServiceProject", javaFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createServiceBuilderModuleProject()
    {
        // ide.getProjectExplorerView().show();

        String projectName = "testServiceBuilderProject";

        newLiferayModuleProject(
            MAVEN, projectName, SERVICE_BUILDER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue( projectTree.expandNode( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-api" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service", "service.xml" ).isVisible() );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testServiceBuilderProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        projectTree.expandNode( projectName, projectName + "-service" ).doAction( "Liferay", "build-service" );
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
            MAVEN, projectName, ACTIVATOR, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestActivatorProjectActivator.java";
        String javaContent = "implements BundleActivator";

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testActivatorProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java", projectName, javaFileName );
    }

    @Test
    public void createApiModuleProject()
    {
        String projectName = "testApiProject";

        newLiferayModuleProject(
            MAVEN, projectName, API, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testApiProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createContentTargetingReportModuleProject()
    {
        String projectName = "testContentTargetingReportProject";

        newLiferayModuleProject(
            MAVEN, projectName, CONTENT_TARGETING_REPORT, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestContentTargetingReportProjectReport.java";
        String javaContent = "extends BaseJSPReport";
        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testContentTargetingReportProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testContentTargetingReportProject.content.targeting.report", javaFileName );

    }

    @Test
    public void createContentTargetingRuleModuleProject()
    {
        String projectName = "testContentTargetingRuleProject";

        newLiferayModuleProject(
            MAVEN, projectName, CONTENT_TARGETING_RULE, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestContentTargetingRuleProjectRule.java";
        String javaContent = "osgi.web.symbolicname=testContentTargetingRuleProject";

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>com.liferay.content-targeting</groupId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testContentTargetingRuleProject.content.targeting.rule", javaFileName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProject()
    {
        String projectName = "testContentTargetingTrackingActionProject";

        newLiferayModuleProject(
            MAVEN, projectName, CONTENT_TARGETING_RULE, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testContentTargetingTrackingActionProject</groupId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createControlMenuEntryModuleProject()
    {
        String projectName = "testControlMenuEntryProject";

        newLiferayModuleProject(
            MAVEN, projectName, CONTROL_MENU_ENTRY, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestControlMenuEntryProjectProductNavigationControlMenuEntry.java";
        String javaContent = "extends BaseProductNavigationControlMenuEntry";

        String pomXmlFileName = "pom.xml";
        String pomContent = "com.liferay.product.navigation.control.menu.api";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testControlMenuEntryProject.control.menu",
            javaFileName );
    }

    @Test
    public void createFormFieldModuleProject()
    {
        String projectName = "testFormFieldProject";

        newLiferayModuleProject(
            MAVEN, projectName, FORM_FIELD, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName1 = "TestFormFieldProjectDDMFormFieldRenderer.java";
        String javaContent1 = "extends BaseDDMFormFieldRenderer";
        String javaFileName2 = "TestFormFieldProjectDDMFormFieldType.java";
        String javaContent2 = "service = DDMFormFieldType.class";

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName1 ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName2 ).isVisible() );

        openEditorAndCheck(
            javaContent1, projectName, projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName1 );
        openEditorAndCheck(
            javaContent2, projectName, projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName2 );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testFormFieldProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createPanelAppModuleProject()
    {
        String projectName = "testPanelAppProject";

        newLiferayModuleProject(
            MAVEN, projectName, PANEL_APP, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );
        assertTrue(
            projectTree.expandNode( projectName, "src/main/java", projectName + ".application.list" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "src/main/java", projectName + ".constants" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "src/main/java", projectName + ".portlet" ).isVisible() );

        String javaFileName = "TestPanelAppProjectPortlet.java";
        String javaContent = "TestPanelAppProjectPortletKeys.TestPanelAppProject";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testPanelAppProject.portlet", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPanelAppProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletModuleProject()
    {
        String projectName = "testPortletProject";

        newLiferayModuleProject(
            MAVEN, projectName, PORTLET, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String javaFileName = "TestPortletProjectPortlet.java";
        String javaContent = "service = Portlet.class";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testPortletProject.portlet", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createPortletConfigurationIconModuleProject()
    {
        String projectName = "testPortletConfigurationIconProject";

        newLiferayModuleProject(
            MAVEN, projectName, PORTLET_CONFIGURATION_ICON, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestPortletConfigurationIconProjectPortletConfigurationIcon.java";
        String javaContent = "extends BasePortletConfigurationIcon";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testPortletConfigurationIconProject.portlet.configuration.icon", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletConfigurationIconProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletProviderModuleProject()
    {
        String projectName = "testPortletProviderProject";

        newLiferayModuleProject(
            MAVEN, projectName, PORTLET_PROVIDER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName1 = "TestPortletProviderProjectAddPortletProvider.java";
        String javaContent1 = "service = AddPortletProvider.class";
        String javaFileName2 = "TestPortletProviderProjectPortlet.java";
        String javaContent2 = "TestPortletProviderProjectPortletKeys.TestPortletProviderProject";

        openEditorAndCheck(
            javaContent1, projectName, projectName, "src/main/java", "testPortletProviderProject.portlet",
            javaFileName1 );
        openEditorAndCheck(
            javaContent2, projectName, projectName, "src/main/java", "testPortletProviderProject.portlet",
            javaFileName2 );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletProviderProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletToolBarContributorModuleProject()
    {
        String projectName = "testPortletToolBarContributorProject";

        newLiferayModuleProject(
            MAVEN, projectName, PORTLET_TOOLBAR_CONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestPortletToolBarContributorProjectPortletToolbarContributor.java";
        String javaContent = "service = PortletToolbarContributor.class";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testPortletToolBarContributorProject.portlet.toolbar.contributor", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletToolBarContributorProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createRestModuleProject()

    {
        String projectName = "testRestProject";

        newLiferayModuleProject(
            MAVEN, projectName, REST, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String javaFileName = "TestRestProjectApplication.java";
        String javaContent = "TestRestProjectApplication extends Application";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testRestProject.application", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testRestProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createServiceWrapperModuleProject()

    {
        String projectName = "testServiceWrapperProject";

        newLiferayModuleProject(
            MAVEN, projectName, SERVICE_WRAPPER_UPPER, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, "*bookmarksEntryServiceWrapper", true );

        String javaFileName = "TestServiceWrapperProject.java";
        String javaContent = "extends BookmarksEntryServiceWrapper";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testServiceWrapperProject", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testServiceWrapperProject</groupId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createSimulationPanelEntryModuleProject()

    {
        String projectName = "testSimulationPanelEntryProject";

        newLiferayModuleProject(
            MAVEN, projectName, SIMULATION_PANEL_ENTRY, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestSimulationPanelEntryProjectSimulationPanelApp.java";
        String javaContent = "SimulationPanelCategory.SIMULATION";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testSimulationPanelEntryProject.application.list",
            javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testSimulationPanelEntryProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createTemplateContextContributorModuleProject()

    {
        String projectName = "testTemplateContextContributorProject";

        newLiferayModuleProject(
            MAVEN, projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        String javaFileName = "TestTemplateContextContributorProjectTemplateContextContributor.java";
        String javaContent = "implements TemplateContextContributor";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testTemplateContextContributorProject.context.contributor", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testTemplateContextContributorProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createThemeModuleProject()
    {
        String projectName = "testThemeProject";

        newLiferayModuleProject(
            MAVEN, projectName, THEME, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String scssFileName = "_custom.scss";
        assertTrue( projectTree.expandNode( projectName, "src", "main", "webapp", "css", scssFileName ).isVisible() );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testThemeProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );
        DeleteResourcesContinueDialog continueDeleteResources =
            new DeleteResourcesContinueDialog( bot, "Delete Resources" );

        projectTree.getTreeItem( projectName ).doAction( DELETE );
        sleep( 2000 );

        deleteResources.getDeleteFromDisk().select();
        deleteResources.confirm();
        continueDeleteResources.getContinueBtn().click();

    }

    @Test
    public void createThemeContributorModuleProject()

    {
        String projectName = "testThemeContributorProject";

        newLiferayModuleProject(
            MAVEN, projectName, THEME_CONTRIBUTOR, eclipseWorkspace, false, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName + ".scss" ).isVisible() );

        projectTree.setFocus();
        String scssFileName = "_body.scss";
        String scssFileContent = "background-color";

        openEditorAndCheck(
            scssFileContent, projectName, projectName, "src/main/resources", "META-INF", "resources", "css",
            projectName, scssFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testThemeContributorProject</groupId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }
}
