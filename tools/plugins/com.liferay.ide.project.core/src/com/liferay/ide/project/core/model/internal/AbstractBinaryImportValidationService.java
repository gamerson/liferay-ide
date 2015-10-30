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

package com.liferay.ide.project.core.model.internal;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public abstract class AbstractBinaryImportValidationService extends ValidationService
{

    protected FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        bindSdkLocation();
    }

    protected abstract void  bindSdkLocation();
    protected abstract void  unBindSdkLocation();
    protected abstract Path getOpSDKLocation();
    protected abstract Path getOpPluginLocation();

    protected abstract Status validatePlugin( Path pluginPath);

    protected Status validateProject( Path pluginPath, Path sdkPath)
    {
        return Status.createOkStatus();
    }

    @Override
    public void dispose()
    {
        if ( listener != null )
        {
            unBindSdkLocation();
        }

        super.dispose();
    }
}
