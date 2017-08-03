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

package com.liferay.ide.swtbot.liferay.ui.page.dialog;

import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class PortalPropertiesFileDialog extends Dialog
{

    private Tree paths;

    public PortalPropertiesFileDialog( SWTBot bot )
    {
        super( bot );

        paths = new Tree( bot );
    }

    public void select( String... items )
    {
        paths.selectTreeItem( items );
    }

}
