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

package com.liferay.ide.ui.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Terry Jia
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class SWTBotBase implements UIBase
{

    public static SWTWorkbenchBot bot;

    public static ButtonUtil buttonUtil;
    public static TextUtil textUtil;
    public static ToolbarUtil toolbarUtil;
    public static ComboBoxUtil comboBoxUtil;
    public static ShellUtil shellUtil;
    public static TreeUtil treeUtil;
    public static ViewUtil viewUtil;

    @BeforeClass
    public static void beforeClass() throws Exception
    {

        bot = new SWTWorkbenchBot();

        buttonUtil = new ButtonUtil( bot );
        textUtil = new TextUtil( bot );
        toolbarUtil = new ToolbarUtil( bot );
        comboBoxUtil = new ComboBoxUtil( bot );
        shellUtil = new ShellUtil( bot );
        treeUtil = new TreeUtil( bot );
        viewUtil = new ViewUtil( bot );

        viewUtil.close( VIEW_WELCOME );
        bot.perspectiveByLabel( "Liferay" ).activate();
    }

}
