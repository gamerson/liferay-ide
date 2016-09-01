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
import org.eclipse.swt.widgets.Text;

/**
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 */
public class DescriptorsPage extends Page
{
    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };
    
    public DescriptorsPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style,dataModel );
        GridLayout layout = new GridLayout( 1, false );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Upgrade Descriptor Files" );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );
        
        Text content = new Text( this, SWT.MULTI );
        final String descriptor =
            "This step will upgrade descriptor xml dtd version from 6.2 to 7.0 and " +
            "delete wap-template-path \ntag in liferay-layout-template.xml.\n" +
            "Double click the file in the list. It will popup a comparison page" +
            " which shows the differences \nbetween your original source file and the upgrade preview file.\n";
        content.setText( descriptor );
        content.setBackground( getDisplay().getSystemColor( SWT.COLOR_TRANSPARENT) );

        new LiferayDescriptorUpgradeTableViewCustomPart(this, SWT.NONE);

        setActions( actions );
        this.setPageId( DESCRIPTORS_PAGE_ID );
    }


}
