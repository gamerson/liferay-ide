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

import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardInLiferayWorkspaceTests extends BaseNewLiferayModuleProjectWizard
{

    static String liferayWorkspaceName = "liferayWorkspace";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @AfterClass
    public static void cleanAll()
    {
        ide.getPackageExporerView().deleteResouceByName( liferayWorkspaceName, true );
    }

    @BeforeClass
    public static void createGradleLiferayWorkspace()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        newLiferayWorkspace( liferayWorkspaceName, GRADLE );
    }

    @Test
    public void createActivatorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testActivatorProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, ACTIVATOR, eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createApiModuleProjectInLiferayWorkspace()
    {
        String projectName = "testApiProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, API, eclipseWorkspace + "/" + liferayWorkspaceName + "/modules",
            false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testApiProjectInLS.api",
                "TestApiProjectInLS.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";

        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createContentTargetingReportModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingReportProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_REPORT,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testContentTargetingReportProjectInLS.content.targeting.report",
                "TestContentTargetingReportProjectInLSReport.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createContentTargetingRuleModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingRuleProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_RULE,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testContentTargetingRuleProjectInLS.content.targeting.rule",
                "TestContentTargetingRuleProjectInLSRule.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingTrackingActionProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, CONTENT_TARGETING_TRACKING_ACTION,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testContentTargetingTrackingActionProjectInLS.content.targeting.tracking.action",
                "TestContentTargetingTrackingActionProjectInLSTrackingAction.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.analytics.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.anonymous.users.api\", version: \"2.0.2\"\n\tcompileOnly group: \"com.liferay.content-targeting\", name: \"com.liferay.content.targeting.api\", version: \"4.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createControlMenuEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "testControlMenuEntryProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, CONTROL_MENU_ENTRY,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.control.menu.api\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testControlMenuEntryProjectInLS.control.menu",
                "TestControlMenuEntryProjectInLSProductNavigationControlMenuEntry.java" ).isVisible() );
    }

    @Test
    public void createFormFieldModuleProjectInLiferayWorkspace()
    {
        String projectName = "testFormFieldProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, FORM_FIELD,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testFormFieldProjectInLS.form.field",
                "TestFormFieldProjectInLSDDMFormFieldRenderer.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testFormFieldProjectInLS.form.field",
                "TestFormFieldProjectInLSDDMFormFieldType.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.dynamic.data.mapping.api\", version: \"3.2.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.dynamic.data.mapping.form.field.type\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createMvcportletModuleProject()
    {
        String projectName = "testMvcportletProject";

        newLiferayModuleProject(
            GRADLE, projectName, MVC_PORTLET,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", true, eclipseWorkspace + "newFolder",
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, true );

        sleep( 4000 );

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
    public void createMvcportletModuleProjectInLiferayWorkspace()
    {
        String projectName = "testMvcportletInLS";

        newLiferayModuleProject(
            GRADLE, projectName, MVC_PORTLET,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        String javaFileName = "TestMvcportletInLSPortlet.java";
        String javaContent = "extends MVCPortlet";

        openEditorAndCheck(
            javaContent, projectName, liferayWorkspaceName, "modules", projectName, "src/main/java",
            "testMvcportletInLS.portlet", javaFileName );

        String buildGradleFileName = "build.gradle";;
        String buildGradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            buildGradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createPanelAppModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPanelAppProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, PANEL_APP,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPanelAppProjectInLS.application.list" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPanelAppProjectInLS.application.list", "TestPanelAppProjectInLSPanelApp.java" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPanelAppProjectInLS.application.list", "TestPanelAppProjectInLSPanelCategory.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPanelAppProjectInLS.constants" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPanelAppProjectInLS.constants",
                "TestPanelAppProjectInLSPanelCategoryKeys.java" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPanelAppProjectInLS.constants",
                "TestPanelAppProjectInLSPortletKeys.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPanelAppProjectInLS.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPanelAppProjectInLS.portlet",
                "TestPanelAppProjectInLSPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.application.list.api\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );

    }

    @Test
    public void createPortletConfigurationIconModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletConfigurationIconProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_CONFIGURATION_ICON,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletConfigurationIconProjectInLS.portlet.configuration.icon",
                "TestPortletConfigurationIconProjectInLSPortletConfigurationIcon.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createPortletModuleProjectInLiferayWorkspace() throws IOException
    {
        String projectName = "testPortletProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletProjectInLS.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPortletProjectInLS.portlet",
                "TestPortletProjectInLSPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createPortletProviderModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletProviderProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_PROVIDER,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletProviderProjectInLS.constants" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletProviderProjectInLS.constants",
                "TestPortletProviderProjectInLSPortletKeys.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletProviderProjectInLS.constants",
                "TestPortletProviderProjectInLSWebKeys.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletProviderProjectInLS.portlet" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPortletProviderProjectInLS.portlet",
                "TestPortletProviderProjectInLSAddPortletProvider.java" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testPortletProviderProjectInLS.portlet",
                "TestPortletProviderProjectInLSPortlet.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createPortletToolbarContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletToolbarContributorProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, PORTLET_TOOLBAR_CONTRIBUTOR,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testPortletToolbarContributorProjectInLS.portlet.toolbar.contributor",
                "TestPortletToolbarContributorProjectInLSPortletToolbarContributor.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createRestModuleProjectInLiferayWorkspace()
    {
        String projectName = "testRestProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, REST,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", "testRestProjectInLS.application",
                "TestRestProjectInLSApplication.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\", version: \"2.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createServiceBuilderModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceBuilderProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE_BUILDER,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue( projectTree.expandNode( liferayWorkspaceName, "modules", projectName ).isVisible() );
        assertTrue(
            projectTree.expandNode( liferayWorkspaceName, "modules", projectName, projectName + "-api" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, projectName + "-service" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, projectName + "-service", "service.xml" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String apiContent =
            "dependencies {\n\tcompileOnly group: \"biz.aQute.bnd\", name: \"biz.aQute.bndlib\", version: \"3.1.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.osgi.util\", version: \"3.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.core\", version: \"6.0.0\"";

        openEditorAndCheck(
            apiContent, projectName, liferayWorkspaceName, "modules", projectName, projectName + "-api",
            buildGradleFileName );

        String serviceContent = "buildService {\n\tapiDir = \"../testServiceBuilderProjectInLS-api/src/main/java\"";
        openEditorAndCheck(
            serviceContent, projectName, liferayWorkspaceName, "modules", projectName, projectName + "-service",
            buildGradleFileName );

        projectTree.expandNode( liferayWorkspaceName, "modules", projectName, projectName + "-service" ).doAction(
            "Liferay", "build-service" );
        sleep( 10000 );

        try
        {
            projectTree.expandNode( liferayWorkspaceName, "modules", projectName ).doAction(
                "Gradle", "Refresh Gradle Project" );
        }
        catch( Exception e )
        {
            projectTree.expandNode( liferayWorkspaceName, "modules", projectName ).doAction(
                "Gradle", "Refresh Gradle Project" );
        }

        sleep( 10000 );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, projectName + "-api", "src/main/java",
                "testServiceBuilderProjectInLS.service" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, projectName + "-service", "src/main/java",
                "testServiceBuilderProjectInLS.model.impl" ).isVisible() );

    }

    @Test
    public void createServiceModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            "*lifecycleAction", true );

        String javaFileName = "TestServiceProjectInLS.java";
        String javaContent = "implements LifecycleAction";

        openEditorAndCheck(
            javaContent, projectName, liferayWorkspaceName, "modules", projectName, "src/main/java",
            "testServiceProjectInLS", javaFileName );

        String buildGradleFileName = "build.gradle";;
        String buildGradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            buildGradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createServiceWrapperModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceWrapperProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, SERVICE_WRAPPER_UPPER,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            "*BookmarksEntryLocalServiceWrapper", true );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java", projectName,
                "TestServiceWrapperProjectInLS.java" ).isVisible() );

        String javaFileName = "TestServiceWrapperProjectInLS.java";
        String javaContent = "extends BookmarksEntryLocalServiceWrapper";

        openEditorAndCheck(
            javaContent, projectName, liferayWorkspaceName, "modules", projectName, "src/main/java", projectName,
            javaFileName );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"org.osgi\", name: \"osgi.cmpn\", version: \"6.0.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createSimulationPanelEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "testSimulationPanelEntryProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, SIMULATION_PANEL_ENTRY,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testSimulationPanelEntryProjectInLS.application.list",
                "TestSimulationPanelEntryProjectInLSSimulationPanelApp.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.application.list.api\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.simulation\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay\", name: \"com.liferay.product.navigation.simulation.web\", version: \"2.0.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.3.0\"\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createTemplateContextContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testTemplateContextContributorProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR,
            eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
            StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/java",
                "testTemplateContextContributorProjectInLS.context.contributor",
                "TestTemplateContextContributorProjectInLSTemplateContextContributor.java" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "dependencies {\n\tcompileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"\n\tcompileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "modules", projectName, buildGradleFileName );
    }

    @Test
    public void createThemeContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testThemeContributorProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, THEME_CONTRIBUTOR, eclipseWorkspace + "/" + liferayWorkspaceName + "/modules", false,
            StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName, "_body.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName, "_control_menu.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName, "_product_menu.scss" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "modules", projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName, "_simulation_panel.scss" ).isVisible() );

        // build.gradle is empry in theme-contributor template
        assertTrue(
            projectTree.expandNode( liferayWorkspaceName, "modules", projectName, "build.gradle" ).isVisible() );
    }

    @Test
    public void createThemeModuleProjectInLiferayWorkspace()
    {
        String projectName = "testThemeProjectInLS";

        newLiferayModuleProject(
            GRADLE, projectName, THEME, eclipseWorkspace + "/" + liferayWorkspaceName + "/wars",
            false, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false );

        assertTrue(
            projectTree.expandNode(
                liferayWorkspaceName, "wars", projectName, "src", "main", "webapp", "css",
                "_custom.scss" ).isVisible() );

        String buildGradleFileName = "build.gradle";
        String gradleContent =
            "apply plugin: \"com.liferay.portal.tools.theme.builder\"\n\ndependencies {\n\tparentThemes group: \"com.liferay\", name: \"com.liferay.frontend.theme.styled\", version: \"2.0.13\"\n\tparentThemes group: \"com.liferay\", name: \"com.liferay.frontend.theme.unstyled\", version: \"2.0.13\"\n\n\tportalCommonCSS group: \"com.liferay\", name: \"com.liferay.frontend.css.common\", version: \"2.0.3\"\n\n\tthemeBuilder group: \"com.liferay\", name: \"com.liferay.portal.tools.theme.builder\", version: \"1.1.3\"";

        openEditorAndCheck(
            gradleContent, projectName, liferayWorkspaceName, "wars", projectName, buildGradleFileName );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

}
