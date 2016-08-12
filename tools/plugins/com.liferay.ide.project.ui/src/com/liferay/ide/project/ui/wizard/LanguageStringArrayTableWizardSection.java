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

package com.liferay.ide.project.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Terry Jia
 */
public class LanguageStringArrayTableWizardSection extends StringArrayTableWizardSection
{

    public LanguageStringArrayTableWizardSection(
        Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
        String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
        IDataModel model, String propertyName )
    {
        super( parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles, fieldLabels, labelProviderImage, model, propertyName );
    }

    public class AddLanguageStringArrayDialog extends AddStringArrayDialog
    {

        public AddLanguageStringArrayDialog( Shell shell, String windowTitle, String[] labelsForTextField )
        {
            super( shell, windowTitle, labelsForTextField );
        }

        protected Text createField( Composite composite, int index )
        {
            Label label = new Label( composite, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );
            Text text = new Text( composite, SWT.SINGLE | SWT.BORDER );
            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            data.widthHint = 100;
            text.setLayoutData( data );
            Label lastlabel = new Label( composite, SWT.RIGHT );
            lastlabel.setText( ".properties" );
            new Label( composite, SWT.NONE );
            return text;
        }

        protected void updateOKButton()
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( true );
        }
    }

    protected void handleAddButtonSelected()
    {
        AddStringArrayDialog dialog = new AddLanguageStringArrayDialog( getShell(), dialogTitle, fieldLabels );
        dialog.open();
        String[] stringArray = dialog.getStringArray();
        addStringArray( stringArray );
    }

    public void addStringArray( String[] stringArray )
    {
        if( stringArray == null )
        {
            return;
        }

        List valueList = (List) viewer.getInput();

        if( valueList == null )
        {
            valueList = new ArrayList();
        }

        for( String s : stringArray )
        {
            if( s.equals( "" ) )
            {
                valueList.add( new String[] { "Language.properties" } );
            }
            else
            {
                valueList.add( new String[] { "Language_" + s + ".properties" } );
            }
        }

        setInput( valueList );
    }

}
