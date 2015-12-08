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
package com.liferay.ide.project.ui.modules;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Simon Jiang
 */
public class ServiceModuleCustomPart extends ModuleCustomPart
{

    private NewLiferayModuleProjectOp op()
    {
        return getLocalModelElement().nearest( NewLiferayModuleProjectOp.class );
    }

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    @Override
    protected AddPropertyOverrideDialog getAddPropertyOverrideDialog(final Shell shell)
    {
        AddPropertyOverrideDialog dialog =
            new AddServicePropertyOverrideDialog( shell, "Add Service Property", fieldLabels, new String[] { "Select",
                "Select" }, new Boolean[] { false, true }, new String[] { "Key", null } );
        return dialog;

    }


    public class AddServicePropertyOverrideDialog extends AddPropertyOverrideDialog
    {
        public AddServicePropertyOverrideDialog(
            Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels, Boolean[] enables, String[] defaultValues  )
        {

            super( shell, windowTitle, labelsForTextField, buttonLabels, enables, defaultValues);

        }

        @Override
        protected void handleSelectPropertyButton( int index, Text text )
        {
            String[] hookProperties =
                new String[] { "admin.default.group.names", "admin.default.role.names",
                    "admin.default.user.group.names", "asset.publisher.asset.entry.query.processors",
                    "asset.publisher.display.styles", "asset.publisher.query.form.configuration",
                    "auth.forward.by.last.path" };

            PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( getParentShell() );
            dialog.setTitle( "Property Selection" );
            dialog.setMessage( "Please select a property" );
            dialog.setInput( hookProperties );

            if( dialog.open() == Window.OK )
            {
                Object[] selected = dialog.getResult();

                text.setText( selected[0].toString() );
            }
        }

    }


    public class EditServicePropertyOverrideDialog extends EditPropertyOverrideDialog
    {
        public EditServicePropertyOverrideDialog(
            Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels, String[] valuesForTextField, Boolean[] enables, String[] defaultValues  )
        {
            super( shell, windowTitle, labelsForTextField, buttonLabels, valuesForTextField, enables, defaultValues);
        }

        @Override
        protected void handleSelectPropertyButton( int index,Text text )
        {
            String[] hookProperties =
                new String[] { "admin.default.group.names", "admin.default.role.names",
                    "admin.default.user.group.names", "asset.publisher.asset.entry.query.processors",
                    "asset.publisher.display.styles", "asset.publisher.query.form.configuration",
                    "auth.forward.by.last.path" };

            PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( getParentShell() );
            dialog.setTitle( "Property Selection" );
            dialog.setMessage( "Please select a property" );
            dialog.setInput( hookProperties );

            if( dialog.open() == Window.OK )
            {
                Object[] selected = dialog.getResult();

                text.setText( selected[0].toString() );
            }
        }
    }

    @Override
    protected EditPropertyOverrideDialog getEditPropertyOverrideDialog( final Shell shell, String[] valuesForText  )
    {
        EditServicePropertyOverrideDialog dialog =
                        new EditServicePropertyOverrideDialog( shell, "Edit Service Property", fieldLabels, new String[] { "Select",
                            "Select" }, valuesForText, new Boolean[] { false, true }, new String[] { "Key", null } );
        return dialog;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    protected List<String> doAdd( String[] stringArray )
    {
        List valueList = (List) viewer.getInput();

        if ( stringArray == null )
        {
            return valueList;
        }

        if ( valueList == null )
        {
            valueList = new ArrayList<String>();
        }

        final String keyValue = stringArray[1];

        ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

        for( PropertyKey propertyKey : propertyKeys )
        {
            String keyValueContent = propertyKey.getKeyValue().content();
            if ( keyValueContent.equals( keyValue ) )
            {
                return valueList;
            }
        }

        PropertyKey propertyKey = propertyKeys.insert();
        propertyKey.setKeyName( stringArray[0] );
        propertyKey.setKeyValue( stringArray[1] );

        valueList.add( stringArray );

        return valueList;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    protected List<String> doEdit( String[] oldStringArray, String[] newStringArray )
    {
        List valueList = (List) viewer.getInput();

        if ( newStringArray == null )
        {
            return valueList;
        }

        ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

        final String oldKeyValue = oldStringArray[1];

        for( int i = 0; i < propertyKeys.size(); i++ )
        {
            PropertyKey propertyKey = propertyKeys.get( i );
            String keyValueContent = propertyKey.getKeyValue().content();

            if ( keyValueContent.equals( oldKeyValue ) )
            {
                propertyKeys.remove( i );
                valueList.set(i, newStringArray);
            }
        }

        PropertyKey newPropertyKey = propertyKeys.insert();
        newPropertyKey.setKeyName( newStringArray[0] );
        newPropertyKey.setKeyValue( newStringArray[1] );

        return valueList;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    protected List<String> doRemove( Collection<String> selectedStringArrays )
    {
        List valueList = (List) viewer.getInput();

        Object[] selectArrays = selectedStringArrays.toArray();

        for( Object selected : selectArrays )
        {
            final String[] removeList = (String[]) (selected);

            final String keyValue = removeList[1];

            ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

            for( int i = 0; i < propertyKeys.size(); i++ )
            {
                PropertyKey propertyKey = propertyKeys.get( i );
                String keyValueContent = propertyKey.getKeyValue().content();

                if ( keyValueContent.equals( keyValue ) )
                {
                    propertyKeys.remove( i );
                    valueList.removeAll(selectedStringArrays);
                }
            }
        }
        return valueList;
    }

    @Override
    protected void checkAndUpdateElement()
    {
        UIUtil.async
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    updateValidation();
                }
            }
        );
    }


    protected void updateValidation()
    {
        retval = Status.createOkStatus();



        refreshValidation();
    }
}
