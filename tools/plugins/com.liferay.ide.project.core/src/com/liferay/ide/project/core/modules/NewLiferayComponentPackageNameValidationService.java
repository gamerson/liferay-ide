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

package com.liferay.ide.project.core.modules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewLiferayComponentPackageNameValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_COMPONENT_NAME ).attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        final JavaPackageName packageName = op().getPackageName().content( true );
        Status retval = Status.createOkStatus();

        int packageNameStatus = IStatus.OK;

        if( packageName != null )
        {
            packageNameStatus = JavaConventions.validatePackageName(
                packageName.toString(), CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7 ).getSeverity();

            if( packageNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid package name" );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null )
        {
            op().property( NewLiferayComponentOp.PROP_COMPONENT_NAME ).detach( this.listener );

            this.listener = null;
        }
        super.dispose();
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }
}
