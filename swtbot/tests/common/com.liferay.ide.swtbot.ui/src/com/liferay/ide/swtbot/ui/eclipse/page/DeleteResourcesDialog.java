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

package com.liferay.ide.swtbot.ui.eclipse.page;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Dialog;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class DeleteResourcesDialog extends Dialog
{

    private CheckBox deleteFromDisk;

    public DeleteResourcesDialog( SWTBot bot )
    {
        super( bot, DELETE_RESOURCES );

        deleteFromDisk = new CheckBox( bot, DELETE_FROM_DISK );
    }

    public CheckBox getDeleteFromDisk()
    {
        return deleteFromDisk;
    }

}
