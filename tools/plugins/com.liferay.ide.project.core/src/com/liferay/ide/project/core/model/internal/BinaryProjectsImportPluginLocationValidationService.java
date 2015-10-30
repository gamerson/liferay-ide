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

import com.liferay.ide.project.core.model.BinarySDKProjectsImportOp;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 */
public class BinaryProjectsImportPluginLocationValidationService extends AbstractBinaryProjectImportPluginLocationValidationService
{
    private BinarySDKProjectsImportOp op()
    {
        return context( BinarySDKProjectsImportOp.class );
    }

    @Override
    protected void bindSdkLocation()
    {
        op().property( BinarySDKProjectsImportOp.PROP_PLUGINS_LOCATION ).attach( listener );
        op().property( BinarySDKProjectsImportOp.PROP_SDK_LOCATION ).attach( listener );
    }

    @Override
    protected void unBindSdkLocation()
    {
        op().property( BinarySDKProjectsImportOp.PROP_PLUGINS_LOCATION ).detach( listener );
        op().property( BinarySDKProjectsImportOp.PROP_SDK_LOCATION ).detach( listener );
    }

    @Override
    protected Path getOpSDKLocation()
    {
        Value<Path> sdkLocation = op().getSdkLocation();

        if ( sdkLocation != null )
        {
            return op().getSdkLocation().content( true );
        }

        return null;
    }

    @Override
    protected Path getOpPluginLocation()
    {
        Value<Path> pluginLocation = op().getPluginsLocation();

        if ( pluginLocation != null  && !pluginLocation.empty() )
        {
            return pluginLocation.content( true );
        }

        return null;
    }

    @Override
    protected Status validatePlugin( Path pluginPath )
    {
        Status status = Status.createOkStatus();

        if ( pluginPath == null || pluginPath.isEmpty() )
        {
            return Status.createErrorStatus( "Select binary plugins (wars) to import as new Liferay Plugin Projects" );
        }

        if ( !pluginPath.toFile().exists() )
        {
            return Status.createErrorStatus( "Selected binary Plugins (wars) path isn't valid" );
        }

        return status;
    }
}