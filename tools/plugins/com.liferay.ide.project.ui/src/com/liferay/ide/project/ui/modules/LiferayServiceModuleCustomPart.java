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

import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Simon Jiang
 */
public class LiferayServiceModuleCustomPart extends LiferayPropertyModuleCustomPart
{

    @Override
    protected AddPropertyOverrideDialog getAddPropertyOverrideDialog( final Shell shell )
    {
        AddPropertyOverrideDialog dialog =
            new AddPropertyOverrideDialog(
                shell, "Add Service Property", fieldLabels, new String[] { null, null }, new Boolean[] { true, true },
                new String[] { null, null }, "Property Selection", "Please select a property", 2 );
        return dialog;
    }

    @Override
    protected String[] loadProperties()
    {
        //TODO
        String[] hookProperties = null;
        return hookProperties;
    }

    @Override
    protected EditPropertyOverrideDialog getEditPropertyOverrideDialog( final Shell shell, String[] valuesForText )
    {
        EditPropertyOverrideDialog dialog =
            new EditPropertyOverrideDialog(
                shell, "Edit Service Property", fieldLabels, new String[] { null, null }, valuesForText, new Boolean[] {
                    true, true }, new String[] { null, null }, "Property Selection", "Please select a property", 2 );
        return dialog;
    }

    @Override
    protected void createControlArea( final Composite composite )
    {
        super.createControlArea( composite );

        SWTUtil.createLabel( composite, SWT.LEAD, "Service Name:", 1 );

        serviceText = SWTUtil.createText( composite, 1 );

        serviceText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                checkAndUpdateElement();
                op().setServiceName( serviceText.getText() );
            }
        } );
        
        serviceSelect = SWTUtil.createButton( composite, "Browse.." );

        serviceSelect.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected( SelectionEvent event )
            {
                handleServiceSelected( composite.getShell() );
            }

            @Override
            public void widgetDefaultSelected( SelectionEvent event )
            {
                // Do nothing
            }
        } );
    }
    
    @Override
    protected void updateValidation()
    {
        super.updateValidation();
        final String serviceName = op().getServiceName().content();
        
        if ( !CoreUtil.isNullOrEmpty( serviceName ) )
        {
            boolean matched = false;
            for( String service : services )
            {
                if ( serviceName.equals( service ) )
                {
                    matched = true;
                }
            }
            
            if ( !matched )
            {
                retval = Status.createErrorStatus( "The service integration point isn't valid." );
            }
        }
    }
    

    private void handleServiceSelected( final Shell shell )
    {
        PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( shell );
        dialog.setTitle( "Select Service " );
        dialog.setMessage( "Please select a Service" );

        /* will be replaced by calling getServices API function*/
        //TODO
        dialog.setInput( services );    
        
        if( dialog.open() == Window.OK )
        {
            Object[] selected = dialog.getResult();

            serviceText.setText( selected[0].toString() );
        }
        checkAndUpdateElement();
    }
}
