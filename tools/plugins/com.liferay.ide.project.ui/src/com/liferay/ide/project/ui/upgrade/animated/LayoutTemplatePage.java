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
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 */
public class LayoutTemplatePage extends Page
{

    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };

    public LayoutTemplatePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        GridLayout layout = new GridLayout( 1, true );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Upgrade Layout Template" );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        final String descriptor = "This step will upgrade layout template file from 6.2 to 7.0.\n" +
            "The layout template's rows and columns are affected by the new grid system syntax of Bootsrap.\n" +
            "For more details, please see <a>Upgrading Layout Templates</a>.\n";
        String url = new String( "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/upgrading-layout-templates" );
        Link link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );

        new LiferayLayouttplUpgradeTableViewCustomPart( this, SWT.NONE );

        setActions( actions );
        this.setPageId( LAYOUTTEMPLATE_PAGE_ID );
    }
}
