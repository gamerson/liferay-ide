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

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 */
public class ExtAndThemePage extends Page
{
    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };
    
    public ExtAndThemePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style,dataModel );
        GridLayout layout = new GridLayout( 1, true );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Ext and Theme Project " );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        Link link = new Link( this, SWT.MULTI );
        final String layouttpl =
            "Ext and Theme Projects will be supported in the future.\n" +
            "For more details, please see <a>Introduction to Themes</a>.\n";
        link.setText( layouttpl );
        link.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                try
                {
                    URL extAndThemeUrl = new URL("https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/introduction-to-themes");
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(extAndThemeUrl);
                }
                catch( Exception e )
                {
                }

            }
        } );
        setActions( actions );
        this.setPageId( EXTANDTHEME_PAGE_ID );
    }
}
