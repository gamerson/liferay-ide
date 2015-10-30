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

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Simon Jiang
 */
public abstract class AbstractBinarySDKProjectImportVersionDerivedValueService extends DerivedValueService
{
    protected FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDerivedValueService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        bindSdkLocation();
    }

    protected abstract void  bindSdkLocation();
    protected abstract void  unBindSdkLocation();
    protected abstract Path getOpSDKLocation();

    @Override
    protected String compute()
    {
        String retVal = null;

        final Path sdkPath = getOpSDKLocation();

        if( sdkPath != null && !sdkPath.isEmpty() )
        {
            final String currentPath = sdkPath.toOSString();

            IStatus sdkValidate = SDKUtil.validateSDKPath( currentPath );

            if ( sdkValidate.isOK() )
            {
                SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkPath ) );

                if ( sdk.validate().isOK() )
                {
                    retVal = sdk.getVersion();
                }
            }
        }

        return retVal;
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
