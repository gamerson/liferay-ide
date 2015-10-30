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
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public abstract class AbstractBinaryProjectImportPluginLocationValidationService extends AbstractBinaryImportValidationService
{
    @Override
    protected Status compute()
    {
        Status retVal = Status.createOkStatus();

        Path pluginPath = getOpPluginLocation();

        Status pluginStatus = validatePlugin( pluginPath );

        if ( !pluginStatus.ok() )
        {
            return pluginStatus;
        }

        final Path sdkPath = getOpSDKLocation();

        if ( sdkPath != null )
        {
            SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkPath ) );

            boolean validSDKVersion = SDKUtil.isValidSDKVersion( sdk.getVersion(), SDKManager.getLeastValidVersion() );

            if ( !validSDKVersion )
            {
                return Status.createWarningStatus( "SDK version and Liferay runtime version may not be compatible." );
            }

            Status validateProjectStatus = validateProject( pluginPath, sdkPath );

            if ( !validateProjectStatus.ok() )
            {
                return validateProjectStatus;
            }
        }

        return retVal;
    }
}
