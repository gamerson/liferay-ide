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

import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.BinarySDKProjectImportOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import java.io.File;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class BinaryProjectImportPluginLocationValidationService extends AbstractBinaryProjectImportPluginLocationValidationService
{
    private BinarySDKProjectImportOp op()
    {
        return context( BinarySDKProjectImportOp.class );
    }

    @Override
    protected void bindSdkLocation()
    {
        op().property( BinarySDKProjectImportOp.PROP_PLUGIN_LOCATION ).attach( listener );
        op().property( BinarySDKProjectImportOp.PROP_SDK_LOCATION ).attach( listener );
    }

    @Override
    protected void unBindSdkLocation()
    {
        op().property( BinarySDKProjectImportOp.PROP_PLUGIN_LOCATION ).detach( listener );
        op().property( BinarySDKProjectImportOp.PROP_SDK_LOCATION ).detach( listener );
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
        Value<Path> pluginLocation = op().getPluginLocation();

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
            return Status.createErrorStatus( "Select a binary to import." );
        }
        else
        {
            File binaryFile = pluginPath.toFile();

            if( !ProjectImportUtil.isValidLiferayPlugin( binaryFile ) )
            {
                return Status.createErrorStatus( "Its not a valid pluing war file." );
            }
        }

        return status;
    }

    @Override
    protected Status validateProject( Path pluginPath, Path sdkPath )
    {
        Status validateProject = Status.createOkStatus();

        BinaryProjectRecord binaryProjectRecord = new BinaryProjectRecord( PathBridge.create( pluginPath ).toFile() );

        ProjectRecord[] updateProjectsList = ProjectImportUtil.updateProjectsList( sdkPath.toPortableString() );

        for( ProjectRecord projectRecord : updateProjectsList )
        {
            if ( projectRecord.getProjectName().equals( binaryProjectRecord.getLiferayPluginName() ) )
            {
                return Status.createErrorStatus( "SDK already has same name project, Please check." );
            }
        }

        return validateProject;
    }
}