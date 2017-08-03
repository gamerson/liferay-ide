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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddOverriddenFileDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.ComponentModelClassSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.ComponentPackageSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.FragmentHostOSGIBundleDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.SelectModuleServiceNameDialog;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayServerRuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayServerWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewModuleFragmentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewModuleFragmentWizardSecondPageWizard;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.IOException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class NewLiferayComponentWizardTests extends SwtbotBase
{

    public final String[] expectedComponentTemplateItems =
        { AUTH_FAILURES, AUTH_MAX_FAILURE, AUTHENTICATOR, FRIENDLY_URL_MAPPER, GOGO_COMMAND, INDEXER_POST_PROCESSOR,
            LOGIN_PRE_ACTION, MVC_PORTLET, MODEL_LISTENER, POLLER_PROCESSOR, PORTLET, PORTLET_ACTION_COMMAND,
            PORTLET_FILTER, REST, SERVICE_WRAPPER_UPPER, STRUTS_IN_ACTION, STRUTS_PORTLET_ACTION };
    static String projectName = "testComponent";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void initWizard() throws IOException
    {

        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayWorkspacePerspective().activate();

        unzipServer();

        NewLiferayServerWizard newServer = new NewLiferayServerWizard( bot );

        NewLiferayServerRuntimeWizard setRuntime = new NewLiferayServerRuntimeWizard( bot );

        ide.getCreateLiferayProjectToolbar().getNewLiferayServer().click();

        newServer.getServerTypeTree().selectTreeItem( LIFERAY_INC, LIFERAY_7_X );
        newServer.next();

        setRuntime.getServerLocation().setText( getLiferayServerDir().toOSString() );

        setRuntime.finish();
    }

    NewLiferayModuleProjectWizard createModuleProjectWizard = new NewLiferayModuleProjectWizard( bot );

    NewLiferayComponentWizard newLiferayComponentWizard = new NewLiferayComponentWizard( bot );

    NewLiferayComponentWizard newLiferayComponentWizardWithNewId = new NewLiferayComponentWizard( bot, 3 );

    NewModuleFragmentWizard newModuleFragmentProject = new NewModuleFragmentWizard( bot );

    ComponentPackageSelectionDialog selectPackageName = new ComponentPackageSelectionDialog( bot );

    NewModuleFragmentWizardSecondPageWizard setModuleFragmentOSGiBundle =
        new NewModuleFragmentWizardSecondPageWizard( bot );

    public void clickWizardSelectProjectAndTemplate( String projectName, String componentClassTemplate )
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        newLiferayComponentWizard.getProjectNames().setSelection( ( projectName ) );

        if( !componentClassTemplate.equals( newLiferayComponentWizard.getComponentClassTemplates().getText() ) )
        {
            newLiferayComponentWizard.getComponentClassTemplates().setSelection( ( componentClassTemplate ) );
        }

        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals( componentClassTemplate, newLiferayComponentWizard.getComponentClassTemplates().getText() );
    }

    public void createModuleProject( String templateName )
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        createModuleProjectWizard.getProjectName().setText( projectName );

        if( !( templateName.equals( createModuleProjectWizard.getProjectTemplateNames().getText() ) ||
            templateName.equals( StringPool.BLANK ) ) )
        {
            createModuleProjectWizard.getProjectTemplateNames().setSelection( templateName );
        }

        sleep();

        if( !createModuleProjectWizard.finishBtn().isEnabled() )
        {
            sleep( 120000 );
        }

        createModuleProjectWizard.finish();

        sleep( 15000 );

    }

    @Test
    public void newComponentClassOnModelListenerTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( StringPool.BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, MODEL_LISTENER );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        ComponentPackageSelectionDialog selectPackageName = new ComponentPackageSelectionDialog( bot );

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        // validation test for model class
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getModelClassName().getText() );

        newLiferayComponentWizard.getModelClassName().setText( "tt" );
        sleep();

        assertEquals( " \"tt\"" + IS_NOT_AMONG_POSSIBLE_VALUES, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "1" );
        sleep();

        assertEquals( " \"1\"" + IS_NOT_AMONG_POSSIBLE_VALUES, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "-" );
        sleep();

        assertEquals( " \"-\"" + IS_NOT_AMONG_POSSIBLE_VALUES, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "." );
        sleep();

        assertEquals( " \".\"" + IS_NOT_AMONG_POSSIBLE_VALUES, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( StringPool.BLANK );
        sleep();

        assertEquals( MODEL_CLASS_MUST_BE_SPECIFIED, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getBrowseBtn().click();
        sleep( 5000 );

        ComponentModelClassSelectionDialog selectModelClass = new ComponentModelClassSelectionDialog( bot );

        selectModelClass.getModelClassSelection().setText( "tt" );

        sleep();
        assertFalse( selectModelClass.confirmBtn().isEnabled() );

        selectModelClass.getModelClassSelection().setText( "*com.liferay.blogs.kernel.model.BlogsEntry" );

        sleep();
        selectModelClass.confirm();
        sleep( 2000 );

        assertEquals(
            "com.liferay.blogs.kernel.model.BlogsEntry", newLiferayComponentWizard.getModelClassName().getText() );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentModelListener.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentModelListener extends BaseModelListener<BlogsEntry>", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnPortletTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( StringPool.BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, PORTLET );

        assertEquals( CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        // validation test for Package name
        newLiferayComponentWizard.getPackageName().setText( "1" );

        sleep( 1000 );
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "-" );

        sleep( 1000 );
        assertEquals( " \"-\"" + IS_NOT_A_VALIDA_JAVA_PACKAGE_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "." );

        sleep( 1000 );
        assertEquals( " \".\"" + IS_NOT_A_VALIDA_JAVA_PACKAGE_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "/" );

        sleep( 1000 );
        assertEquals( " \"/\"" + IS_NOT_A_VALIDA_JAVA_PACKAGE_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "a" );

        sleep( 1000 );
        assertEquals( CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        assertEquals( CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        // validation test for Component Class Name
        newLiferayComponentWizard.getComponentClassName().setText( "1" );
        sleep( 1000 );

        assertEquals( INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "-" );
        sleep( 1000 );

        assertEquals( INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "." );
        sleep( 1000 );

        assertEquals( INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "/" );
        sleep( 1000 );

        assertEquals( INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "a" );
        sleep( 1000 );

        assertEquals( CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( StringPool.BLANK );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentPortlet.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentPortlet extends GenericPortlet", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnServiceWrapperTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( StringPool.BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, SERVICE_WRAPPER_UPPER );

        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getServiceName().getText() );

        newLiferayComponentWizard.getServiceName().setText( "tt" );
        sleep();

        assertEquals( CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getServiceName().setText( StringPool.BLANK );
        sleep();

        assertEquals( VALIDATION_SERVICE_WRAPPER_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        newLiferayComponentWizard.getBrowseBtn().click();
        sleep( 5000 );

        SelectModuleServiceNameDialog selectServiceName = new SelectModuleServiceNameDialog( bot );

        selectServiceName.getServiceName().setText( "gg" );
        sleep();
        assertFalse( selectServiceName.confirmBtn().isEnabled() );

        selectServiceName.getServiceName().setText( "*bookmarksEntryLocal" );
        sleep();
        assertTrue( selectServiceName.confirmBtn().isEnabled() );

        selectServiceName.confirm();
        sleep( 2000 );
        assertEquals(
            "com.liferay.bookmarks.service.BookmarksEntryLocalServiceWrapper",
            newLiferayComponentWizard.getServiceName().getText() );

        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentServiceHook.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentServiceHook extends BookmarksEntryLocalServiceWrapper", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnThemeModuleTest()
    {
        if( hasAddedProject )
        {
            ide.getProjectTree().getTreeItem( projectName ).collapse();

            ide.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        createModuleProject( "theme" );

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        assertEquals( StringPool.BLANK, newLiferayComponentWizardWithNewId.getProjectNames().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals( PORTLET, newLiferayComponentWizard.getComponentClassTemplates().getText() );
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.cancel();

        ide.getPackageExporerView().deleteResouceByName( projectName, true );

    }

    @Test
    public void newLiferayComponentClassWithoutAvailableModuleProjectTest()
    {

        if( hasAddedProject )
        {
            ide.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default initial state
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getProjectNames().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals( PORTLET, newLiferayComponentWizard.getComponentClassTemplates().getText() );
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        String[] componentTemplateItems = newLiferayComponentWizard.getComponentClassTemplates().items();

        assertTrue( componentTemplateItems.length >= 1 );

        assertEquals( expectedComponentTemplateItems.length, componentTemplateItems.length );

        for( int i = 0; i < componentTemplateItems.length; i++ )
        {
            assertTrue( componentTemplateItems[i].equals( expectedComponentTemplateItems[i] ) );
        }

        newLiferayComponentWizard.cancel();

        // create Liferay Fragment project
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragmentProject().click();

        newModuleFragmentProject.getProjectName().setText( "fragmentTest" );
        newModuleFragmentProject.next();

        // select OSGi Bundle and Overridden files

        setModuleFragmentOSGiBundle.getBrowseOSGiBundleBtn().click();

        FragmentHostOSGIBundleDialog selectOSGiBundle = new FragmentHostOSGIBundleDialog( bot );

        AddOverriddenFileDialog addJSPFiles = new AddOverriddenFileDialog( bot );

        selectOSGiBundle.getOsgiBundle().setText( "*com.liferay.bookmarks.web" );
        selectOSGiBundle.confirm();

        setModuleFragmentOSGiBundle.getAddOverridFilesBtn().click();
        addJSPFiles.getAddFilesToOverride().selectTreeItem( "META-INF/resources/bookmarks/view.jsp" );
        addJSPFiles.confirm();

        setModuleFragmentOSGiBundle.finish();
        sleep( 15000 );

        // open component wizard again then check state to make sure it couldn't
        // support new component class
        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default state again
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getProjectNames().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals( StringPool.BLANK, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals( PORTLET, newLiferayComponentWizard.getComponentClassTemplates().getText() );
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.cancel();

        ide.getPackageExporerView().deleteResouceByName( "fragmentTest", true );
    }

    @Before
    public void shouldRunTests()
    {

        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

    }

}
