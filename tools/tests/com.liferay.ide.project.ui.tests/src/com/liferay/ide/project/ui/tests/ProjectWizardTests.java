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
import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.ui.tests.CheckBoxUtil;
import com.liferay.ide.ui.tests.LabelUtil;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.TextUtil;
import com.liferay.ide.ui.tests.CheckBoxUtil;
import com.liferay.ide.ui.tests.UITestsUtils;

import org.eclipse.swtbot.swt.finder.SWTBotAssert;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
{

    public static boolean added = false;

    @AfterClass
    public static void cleanAll()
    {
        SWTBotTreeItem[] items = treeUtil.getItems();

        for( SWTBotTreeItem item : items )
        {
            item.contextMenu( "Delete" ).click();
            bot.checkBox().click();
            buttonUtil.click( BUTTON_OK );
        }
    }

    public static void deleteProjectInSdk( String projectName, String... nodes )
    {
        treeUtil.expandNode( nodes ).getNode( projectName ).contextMenu( "Delete" ).click();
        buttonUtil.click( BUTTON_OK );
    }

    private boolean addedProjecs()
    {
        viewUtil.show( "Package Explorer" );

        return treeUtil.hasItems();
    }

    @After
    public void checkNewProjectSuccessInConsole()
    {
        assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );
    }

    @Test
    public void createExtProject()
    {
        comboBoxUtil.select( 1, MENU_EXT );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createHookProject()
    {
        comboBoxUtil.select( 1, MENU_HOOK );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createLayoutProject()
    {
        comboBoxUtil.select( 1, MENU_LAYOUT_TEMPLATE );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );

    }

    @Test
    public void createPortletProject()
    {
        comboBoxUtil.select( 1, MENU_PORTLET );
        assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );

        buttonUtil.click( BUTTON_NEXT );
        assertEquals( textUtil.getText( INDEX_VALIDATION_MESSAGE3 ), TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS );
        assertTrue( radioUtil.radio( TEXT_LIFERAY_MVC_FRAMEWORK ).isSelected() );
        assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE1 ).isVisible() );
        assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE2 ).isVisible() );

        if( !added )
        {
            setSDKLocation();
        }
        else
        {
            assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
            assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        }

        buttonUtil.click( BUTTON_FINISH );
        assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );
        treeUtil.expandNode( "test-portlet", "docroot", "WEB-INF" ).getNode( "liferay-display.xml" ).doubleClick();
        assertTrue( editorUtil.isActive( "liferay-display.xml" ) );
        assertContains( "sample", textUtil.getStyledText() );

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
        comboBoxUtil.select( 1, MENU_PORTLET );
        // enter project name which contains *
        textUtil.setText( TEXT_PROJECT_NAME, "test*" );
        assertContains(
            "*" + TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME + "'test*'.", textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        // enter project which contains .
        textUtil.setText( TEXT_PROJECT_NAME, "test." );
        assertContains( "'test.'" + TEXT_INVALID_NAME_ON_PLATFORM, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        // enter project name which is open in eclipse
        textUtil.setText( TEXT_PROJECT_NAME, "test" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        // enter projet with -portlet and check
        textUtil.setText( TEXT_PROJECT_NAME, "test-portlet" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        buttonUtil.click( BUTTON_CANCEL );
        // enter project name which is existing in workspace
        treeUtil.getNode( "test-portlet" ).contextMenu( "Delete" ).click();
        buttonUtil.click( BUTTON_OK );
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
        comboBoxUtil.select( 1, MENU_PORTLET );
        textUtil.setText( TEXT_PROJECT_NAME, "test" );
        assertContains(
            "test-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        textUtil.setText( TEXT_PROJECT_NAME, "test-portlet" );
        assertContains(
            "test-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        buttonUtil.click( BUTTON_CANCEL );
        deleteProjectInSdk( "test-portlet", "liferay-plugins-sdk-6.2-ee-sp10", "portlets" );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        textUtil.setText( TEXT_PROJECT_NAME, "noSampleTest" );
        checkBoxUtil.deSelect( TEXT_INCLUDE_SAMPLE_CODE );
        checkBoxUtil.select( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );

        assertFalse( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertTrue( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );

        buttonUtil.click( BUTTON_NEXT );
        assertEquals( textUtil.getText( INDEX_VALIDATION_MESSAGE1 ), TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS );
        if( !added )
        {
            setSDKLocation();
        }
        else
        {
            assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
            assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        }
        buttonUtil.click( BUTTON_FINISH );

        assertTrue( shellUtil.shell( TOOLTIP_NEW_LIFERAY_PORTLET ).isActive() );
        assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_CANCEL ) );
        assertTrue( radioUtil.radio( TEXT_CREATE_NEW_PORTLET ).isSelected() );
        assertFalse( radioUtil.radio( TEXT_USE_DEFAULT_MVC_PORTLET ).isSelected() );
        buttonUtil.click( BUTTON_CANCEL );

    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        comboBoxUtil.select( 1, MENU_SERVICE_BUILDER_PORTLET );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createThemeProject()
    {
        comboBoxUtil.select( 1, MENU_THEME );

        buttonUtil.click( BUTTON_NEXT );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createWebProject()
    {
        comboBoxUtil.select( 1, MENU_WEB );

        if( !added )
        {
            setSDKLocation();
        }

        assertEquals( false, buttonUtil.isEnabled( BUTTON_FINISH ) );

        shellUtil.close();
    }

    @Before
    public void openWizard()
    {
        added = addedProjecs();

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        assertEquals( TEXT_ENTER_PROJECT_NAME, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        assertEquals( "", textUtil.getText( TEXT_PROJECT_NAME ) );
        assertEquals( "", textUtil.getText( TEXT_DISPLAY_NAME ) );
        assertEquals( MENU_BUILD_TYPE_ANT, comboBoxUtil.getText( TEXT_BUILD_TYPE ) );
        assertEquals( MENU_PORTLET, comboBoxUtil.getText( TEXT_PLUGIN_TYPE ) );
        assertTrue( buttonUtil.isTooltipEnabled( TOOLTIP_LEARN_MORE ) );
        assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT ) );
        assertFalse( checkBoxUtil.isChecked( TEXT_ADD_PROJECT_TO_WORKING_SET ) );
        assertFalse( comboBoxUtil.isEnabled( TEXT_WORKING_SET ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_CANCEL ) );

        textUtil.setText( TEXT_PROJECT_NAME, "test" );
        assertEquals( textUtil.getText( INDEX_VALIDATION_MESSAGE3 ), TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN );
        assertTrue( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
    }

    private void setSDKLocation()
    {
        buttonUtil.click( BUTTON_NEXT );

        assertEquals( TEXT_SDK_LOCATION_EMPTY, textUtil.getText( INDEX_VALIDATION_MESSAGE2 ) );

        textUtil.setText( TEXT_SDK_LOCATION, "D:\\work\\liferay-plugins-sdk\\liferay-plugins-sdk-6.2-ee-sp10" );

        assertEquals( TEXT_CHOOSE_PLUGINS_SDK_AND_OPEN, textUtil.getText( INDEX_VALIDATION_MESSAGE2 ) );
        assertTrue( buttonUtil.isTooltipEnabled( TOOLTIP_BROWSE ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_BACK ) );
        assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        assertTrue( buttonUtil.isEnabled( BUTTON_FINISH ) );
    }

}
