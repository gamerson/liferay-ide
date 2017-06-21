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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ButtonPO extends AbstractWidgetPO
{

    public ButtonPO( SWTBot bot, int index )
    {
        super( bot, index );
    }

    public ButtonPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public ButtonPO( SWTBot bot, String label, int index )
    {
        super( bot, label, index );
    }

    public void click()
    {
        getWidget().click();
    }

    @Override
    protected SWTBotButton getWidget()
    {
        if( label == null )
        {
            return bot.button( index );
        }

        if( index > 0 )
        {
            return bot.button( label, index );
        }
        else
        {
            return bot.button( label );
        }
    }

}