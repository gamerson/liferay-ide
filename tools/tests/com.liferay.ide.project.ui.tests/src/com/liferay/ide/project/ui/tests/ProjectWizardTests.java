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

import static org.junit.Assert.assertEquals;

import com.liferay.ide.ui.tests.SWTBotBase;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
{

    @Test
    public void createPortletProject()
    {
        boolean added = addedProjecs();

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        buttonUtil.click( BUTTON_NEXT );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        boolean added = addedProjecs();

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "test" );
        comboBoxUtil.select( 1, MENU_SERVICE_BUILDER_PORTLET );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createHookProject()
    {
        boolean added = addedProjecs();
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        comboBoxUtil.select( 1, MENU_HOOK );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createExtProject()
    {
        boolean added = addedProjecs();
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        comboBoxUtil.select( 1, MENU_EXT );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createThemeProject()
    {
        boolean added = addedProjecs();
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        comboBoxUtil.select( 1, MENU_THEME );
        buttonUtil.click( BUTTON_NEXT );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );
    }

    @Test
    public void createLayoutProject()
    {
        boolean added = addedProjecs();
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        comboBoxUtil.select( 1, MENU_LAYOUT_TAMPLATE );

        if( !added )
        {
            setSDKLocation();
        }

        buttonUtil.click( BUTTON_FINISH );

    }

    @Test
    public void createWebProject()
    {
        boolean added = addedProjecs();
        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        textUtil.setText( TEXT_PROJECT_NAME, "asd" );
        comboBoxUtil.select( 1, MENU_WEB );

        if( !added )
        {
            setSDKLocation();
        }

        assertEquals( false, buttonUtil.isEnabled( BUTTON_FINISH ) );

        shellUtil.close();
    }

    private boolean addedProjecs()
    {
        viewUtil.show( "Package Explorer" );
        return treeUtil.hasItems();
    }

    private void setSDKLocation()
    {
        buttonUtil.click( BUTTON_NEXT );
        textUtil.setText( TEXT_SDK_LOCATION, "D:\\work\\liferay-plugins-sdk\\liferay-plugins-sdk-6.2-ee-sp10" );
    }

    @AfterClass
    public static void afterClass()
    {
        SWTBotTreeItem[] items = treeUtil.getItems();

        for( SWTBotTreeItem item : items )
        {
            item.contextMenu( "Delete" ).click();
            bot.checkBox().click();
            buttonUtil.click( BUTTON_OK );
        }
    }
}
