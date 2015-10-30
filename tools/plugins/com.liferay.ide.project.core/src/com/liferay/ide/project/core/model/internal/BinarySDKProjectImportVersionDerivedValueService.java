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

import com.liferay.ide.project.core.model.BinarySDKProjectImportOp;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;


/**
 * @author Simon Jiang
 */
public class BinarySDKProjectImportVersionDerivedValueService extends AbstractBinarySDKProjectImportVersionDerivedValueService
{
    @Override
    protected void  bindSdkLocation()
    {
        op().property( BinarySDKProjectImportOp.PROP_SDK_LOCATION ).attach( this.listener );
    }

    private BinarySDKProjectImportOp op()
    {
        return context( BinarySDKProjectImportOp.class );
    }

    @Override
    protected Path getOpSDKLocation()
    {
        Value<Path> sdkLocation = op().getSdkLocation();

        if ( sdkLocation != null && !sdkLocation.empty() )
        {
            return op().getSdkLocation().content( true );
        }

        return null;
    }

    @Override
    protected void unBindSdkLocation()
    {
        if ( op() != null)
        {
            op().property( BinarySDKProjectImportOp.PROP_SDK_LOCATION ).detach( this.listener );
        }
    }
}
