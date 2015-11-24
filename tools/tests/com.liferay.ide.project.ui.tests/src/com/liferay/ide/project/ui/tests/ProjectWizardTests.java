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
import org.junit.Before;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
{

    public static boolean added = false;

    private boolean addedProjecs()
    {
        viewUtil.show( "Package Explorer" );

        return treeUtil.hasItems();
    }

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

    @Test
    public void createPortletProject()
    {
        comboBoxUtil.select( 1, MENU_PORTLET );

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
        comboBoxUtil.select( 1, MENU_LAYOUT_TEMPLATE );

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

        textUtil.setText( TEXT_PROJECT_NAME, "test" );
    }

    private void setSDKLocation()
    {
        buttonUtil.click( BUTTON_NEXT );
        textUtil.setText( TEXT_SDK_LOCATION, "D:\\work\\liferay-plugins-sdk\\liferay-plugins-sdk-6.2-ee-sp10" );
    }

}
