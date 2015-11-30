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

package com.liferay.ide.project.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.ui.tests.SWTBotBase;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
{

    public static boolean added = false;
    public static String currentType = "";

    private boolean addedProjecs()
    {
        viewUtil.show( VIEW_PACKAGE_EXPLORER );

        return treeUtil.hasItems();
    }

    @AfterClass
    public static void cleanAll()
    {
        SWTBotTreeItem[] items = treeUtil.getItems();

        try
        {
            for( SWTBotTreeItem item : items )
            {
                if( !item.getText().equals( getLiferayPluginsSdkName() ) )
                {
                    item.contextMenu( BUTTON_DELETE ).click();

                    checkBoxUtil.click();

                    buttonUtil.click( BUTTON_OK );

                    if (buttonUtil.isEnabled( "Continue" )) {
                        buttonUtil.click( "Continue" );
                    }

                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    private void checkAndAddSDK()
    {
        if( added )
        {
            assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
            assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        }
        else
        {
            assertTrue( buttonUtil.isEnabled( BUTTON_NEXT ) );

            buttonUtil.click( BUTTON_NEXT );

            setSDKLocation();
        }
    }

    private void checkNewProjectSuccess( String projectName )
    {
        if( !currentType.equals( "web" ) )
        {
            sleep();

            viewUtil.show( VIEW_PACKAGE_EXPLORER );

            assertTrue( treeUtil.getTreeItem( projectName ).isVisible() );

            // assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );
        }
    }

    @Test
    public void createExtProject()
    {
        currentType = "ext";

        String projectName = "testExt";

        setProjectName( projectName );

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_EXT );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        checkNewProjectSuccess( projectName + "-ext" );
    }

    @Test
    public void createHookProject()
    {
        currentType = "hook";

        String projectName = "testHook";

        setProjectName( projectName );

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_HOOK );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        checkNewProjectSuccess( projectName + "-hook" );
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        currentType = "portlet";

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_SERVICE_BUILDER_PORTLET );

        String projectName = "testServiceBuilderPortlet";

        setProjectName( projectName );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        checkNewProjectSuccess( projectName + "-portlet" );

        // assertTrue( ( treeUtil.expandNode( projectName + "-portlet" ).expandNode( "docroot" ).getNode( "view.jsp" )
        // ).isVisible() );
        // assertTrue( ( treeUtil.expandNode( projectName + "-portlet" ).expandNode( "docroot" ).expandNode( "css"
        // ).getNode( "main.css" ) ).isVisible() );
        // assertTrue( ( treeUtil.expandNode( projectName + "-portlet" ).expandNode( "docroot" ).expandNode( "js"
        // ).getNode( "main.js" ) ).isVisible() );
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        currentType = "portlet";

        String projectName = "testServiceBuilderWithoutSampleCode";

        setProjectName( projectName );

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_SERVICE_BUILDER_PORTLET );

        if( checkBoxUtil.isChecked( CHECKBOX_INCLUDE_SAMPLE_CODE ) )
        {
            checkBoxUtil.click( CHECKBOX_INCLUDE_SAMPLE_CODE );
        }

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        sleep( 10000 );

        checkNewProjectSuccess( projectName + "-portlet" );
    }

    @Test
    public void createLayoutProject()
    {
        String projectName = "testLayout";

        setProjectName( projectName );

        currentType = "layout";

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_LAYOUT_TEMPLATE );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        checkNewProjectSuccess( projectName + "-layouttpl" );
    }

    @Test
    public void createPortletProject()
    {
        currentType = "portlet";

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_PORTLET );

        assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );

        String projectName = "testPortlet";

        setProjectName( projectName );

        buttonUtil.click( BUTTON_NEXT );

        assertEquals( TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );

        assertTrue( radioUtil.radio( TEXT_LIFERAY_MVC_FRAMEWORK ).isSelected() );
        assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE1 ).isVisible() );
        assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE2 ).isVisible() );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );
        // assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );

        checkNewProjectSuccess( projectName + "-portlet" );

        treeUtil.expandNode( projectName + "-portlet", "docroot", "WEB-INF" ).getNode( "liferay-display.xml" ).doubleClick();
        assertTrue( editorUtil.isActive( "liferay-display.xml" ) );
        assertContains( "sample", textUtil.getStyledText() );

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, projectName );

        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        // enter projet with -portlet and check
        textUtil.setText( TEXT_PROJECT_NAME, projectName + "-portlet" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );

        buttonUtil.click( BUTTON_CANCEL );

        // enter project name which is existing in workspace
        deleteProject( projectName + "-portlet" );

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
        comboBoxUtil.select( 1, MENU_PORTLET );
        textUtil.setText( TEXT_PROJECT_NAME, projectName );
        assertContains(
            projectName + "-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        textUtil.setText( TEXT_PROJECT_NAME, projectName + "-portlet" );
        assertContains(
            projectName + "-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        buttonUtil.click( BUTTON_CANCEL );
        deleteProjectInSdk( projectName + "-portlet", getLiferayPluginsSdkName(), "portlets" );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        currentType = "portlet";

        String projectName = "noSampleTest";

        setProjectName( projectName );
        checkBoxUtil.deSelect( TEXT_INCLUDE_SAMPLE_CODE );
        checkBoxUtil.select( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );

        assertFalse( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertTrue( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );

        buttonUtil.click( BUTTON_NEXT );
        assertEquals( textUtil.getText( INDEX_VALIDATION_MESSAGE1 ), TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );

        sleep( 10000 );

        assertTrue( shellUtil.shell( TOOLTIP_NEW_LIFERAY_PORTLET ).isActive() );
        assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_CANCEL ) );
        assertTrue( radioUtil.radio( TEXT_CREATE_NEW_PORTLET ).isSelected() );
        assertFalse( radioUtil.radio( TEXT_USE_DEFAULT_MVC_PORTLET ).isSelected() );

        buttonUtil.click( BUTTON_CANCEL );

        checkNewProjectSuccess( projectName + "-portlet" );
    }

    @Test
    public void createThemeProject()
    {
        currentType = "theme";

        String projectName = "testTheme";

        setProjectName( projectName );

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_THEME );

        buttonUtil.click( BUTTON_NEXT );

        String defaultMessage = "Select options for creating new theme project.";
        String warningMessage = " For advanced theme developers only.";

        assertEquals( defaultMessage, textUtil.getText( INDEX_THEME_VALIDATION_MESSAGE ) );

        comboBoxUtil.select( THEME_PARENT_TYPE, MANU_THEME_PARENT_UNSTYLED );
        comboBoxUtil.select( THEME_FARMEWORK_TYPE, MANU_THEME_FRAMEWORK_JSP );

        bot.sleep( 800 );
        assertEquals( warningMessage, textUtil.getText( INDEX_THEME_VALIDATION_MESSAGE ) );
        comboBoxUtil.select( THEME_PARENT_TYPE, MANU_THEME_PARENT_CLASSIC );
        comboBoxUtil.select( THEME_FARMEWORK_TYPE, MANU_THEME_FRAMEWORK_VELOCITY );

        bot.sleep( 800 );
        assertEquals( defaultMessage, textUtil.getText( INDEX_THEME_VALIDATION_MESSAGE ) );
        comboBoxUtil.select( THEME_PARENT_TYPE, MANU_THEME_PARENT_STYLED );
        comboBoxUtil.select( THEME_FARMEWORK_TYPE, MANU_THEME_FRAMEWORK_FREEMARKER );

        checkAndAddSDK();

        buttonUtil.click( BUTTON_FINISH );
        sleep( 15000 );
        checkNewProjectSuccess( projectName + "-theme" );
    }

    @Test
    public void createWebProject()
    {
        String projectName = "testWeb";

        setProjectName( projectName );

        currentType = "web";

        comboBoxUtil.select( TEXT_PLUGIN_TYPE, MENU_WEB );

        checkAndAddSDK();

        assertEquals( false, buttonUtil.isEnabled( BUTTON_FINISH ) );

        shellUtil.close();
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";
        String invalidNameDoubleSlash = "//";
        String invalidNameDot = ".";
        String invalidNameStar = "*";

        textUtil.setText( TEXT_PROJECT_NAME, TEXT_BLANK );

        assertEquals( TEXT_BLANK, textUtil.getText( TEXT_PROJECT_NAME ) );
        assertEquals( TEXT_BLANK, textUtil.getText( TEXT_DISPLAY_NAME ) );

        assertEquals( MENU_BUILD_TYPE_ANT, comboBoxUtil.getText( TEXT_BUILD_TYPE ) );
        assertEquals( MENU_PORTLET, comboBoxUtil.getText( TEXT_PLUGIN_TYPE ) );

        assertTrue( buttonUtil.isTooltipEnabled( TOOLTIP_LEARN_MORE ) );
        // assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_ADD_PROJECT_TO_WORKING_SET ) );
        assertFalse( comboBoxUtil.isEnabled( TEXT_WORKING_SET ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_CANCEL ) );

        textUtil.setText( TEXT_PROJECT_NAME, invalidNameDoubleDash );
        assertEquals( " The project name is invalid.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );

        textUtil.setText( TEXT_PROJECT_NAME, invalidNameDoubleSlash );
        assertEquals( " / is an invalid character in resource name '//'.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );

        textUtil.setText( TEXT_PROJECT_NAME, invalidNameDot );
        assertEquals( " '.' is an invalid name on this platform.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );

        textUtil.setText( TEXT_PROJECT_NAME, invalidNameStar );
        assertEquals( " * is an invalid character in resource name '*'.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );

        buttonUtil.click( BUTTON_CANCEL );
    }

    private void deleteProject( String projectName )
    {
        viewUtil.show( VIEW_PACKAGE_EXPLORER );
        treeUtil.getNode( projectName ).contextMenu( BUTTON_DELETE ).click();
        buttonUtil.click( BUTTON_OK );
        sleep();
    }

    public static void deleteProjectInSdk( String projectName, String... nodes )
    {
        treeUtil.expandNode( nodes ).getNode( projectName ).contextMenu( BUTTON_DELETE ).click();

        buttonUtil.click( BUTTON_OK );
    }

    @Before
    public void openWizard()
    {
        added = addedProjecs();

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

    }

    private void setProjectName( String projectName )
    {
        textUtil.setText( TEXT_PROJECT_NAME, projectName );

        assertEquals( TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
    }

    private void setSDKLocation()
    {
        assertEquals( TEXT_SDK_LOCATION_EMPTY, textUtil.getText( INDEX_VALIDATION_MESSAGE2 ) );

        textUtil.setText( TEXT_SDK_LOCATION, getLiferayPluginsSdkDir().toString() );

        assertTrue( buttonUtil.isTooltipEnabled( TOOLTIP_BROWSE ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );

        if( currentType.equals( "web" ) )
        {
            assertEquals( TEXT_WEB_SDK_62_ERRORR_MESSAGE, textUtil.getText( INDEX_VALIDATION_MESSAGE2 ) );
            assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        }
        else
        {
            assertEquals( TEXT_CHOOSE_PLUGINS_SDK_AND_OPEN, textUtil.getText( INDEX_VALIDATION_MESSAGE2 ) );
            assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
        }
    }
}
