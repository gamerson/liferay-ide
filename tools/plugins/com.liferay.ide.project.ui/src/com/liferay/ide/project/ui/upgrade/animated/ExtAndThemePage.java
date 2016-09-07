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

package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
public class ExtAndThemePage extends Page
{

    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };

    public ExtAndThemePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        GridLayout layout = new GridLayout( 1, true );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Ext and Theme Project " );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        final String descriptor =
            "Theme and Ext projects are not supported to upgrade in this tool currenttly.\n" +
            "For Theme Projects, you can upgrade them manually.\n"+
            "For Ext Projects, we didn't provide support for them at Liferay 7.0.\n" +
            "If you have ext projects, you can change them into modules.\n"+
            "For more details, please see <a>Liferay Blade Samples</a>.\n";
        String url = new String("https://github.com/liferay/liferay-blade-samples");
        Link link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );

        setActions( actions );

        this.setPageId( EXTANDTHEME_PAGE_ID );
    }
}
