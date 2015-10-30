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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public abstract class AbstractBinaryProjectImportSDKLocationValidationService extends AbstractBinaryImportValidationService
{
    @Override
    protected Status compute()
    {
       Status retval = Status.createOkStatus();

        final Path sdkPath = getOpSDKLocation();

        if( sdkPath != null && !sdkPath.isEmpty() )
        {
            final String sdkLocation = sdkPath.toOSString();

            IStatus sdkValidate = SDKUtil.validateSDKPath( sdkLocation );

            if ( sdkValidate.isOK() )
            {
                SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkPath ) );

                boolean validSDKVersion = SDKUtil.isValidSDKVersion( sdk.getVersion(), SDKManager.getLeastValidVersion() );

                if ( !validSDKVersion )
                {
                    return Status.createWarningStatus( "SDK version and Liferay runtime version may not be compatible." );
                }

                Path pluginPath = getOpPluginLocation();

                if ( pluginPath != null && ! pluginPath.isEmpty() )
                {
                    Status validateProjectStatus = validateProject( pluginPath, sdkPath );

                    if ( !validateProjectStatus.ok() )
                    {
                        return validateProjectStatus;
                    }
                }

            }
            else
            {
                return Status.createErrorStatus( sdkValidate.getMessage() );
            }
        }

        return retval;
    }
}