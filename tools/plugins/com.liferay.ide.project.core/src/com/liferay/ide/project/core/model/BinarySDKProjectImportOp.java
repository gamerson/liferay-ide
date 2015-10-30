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
package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.BinaryProjectImportPluginLocationValidationService;
import com.liferay.ide.project.core.model.internal.BinaryProjectImportSDKLocationInitialValueService;
import com.liferay.ide.project.core.model.internal.BinaryProjectImportSDKLocationValidationService;
import com.liferay.ide.project.core.model.internal.BinarySDKProjectImportVersionDerivedValueService;
import com.liferay.ide.project.core.model.internal.HasWorkspaceSdkDefaultValueService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface BinarySDKProjectImportOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( BinarySDKProjectImportOp.class );

    // *** Plugin Location ***
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @FileExtensions ( expr="war" )
    @Service( impl = BinaryProjectImportPluginLocationValidationService.class )
    @Label( standard = "Plugin Location" )
    ValueProperty PROP_PLUGIN_LOCATION = new ValueProperty( TYPE, "PluginLocation" ); //$NON-NLS-1$

    Value<Path> getPluginLocation();
    void setPluginLocation( String value );
    void setPluginLocation( Path value );

    // *** SDK Location ***
    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${HasWorkspaceSDK == 'false'}" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "SDK Location" )
    @Services
    (
        value=
        {
            @Service( impl = BinaryProjectImportSDKLocationValidationService.class ),
            @Service( impl = BinaryProjectImportSDKLocationInitialValueService.class )
        }
    )
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" ); //$NON-NLS-1$

    Value<Path> getSdkLocation();
    void setSdkLocation( String value );
    void setSdkLocation( Path value );

    // *** SDK Version ***
    @Label( standard = "SDK Version" )
    @Derived
    @Enablement( expr = "${HasWorkspaceSDK == 'false'}" )
    @Service( impl = BinarySDKProjectImportVersionDerivedValueService.class )
    ValueProperty PROP_SDK_VERSION = new ValueProperty( TYPE, "SdkVersion" ); //$NON-NLS-1$

    Value<String> getSdkVersion();
    void setSdkVersion( String value );

    @Type( base = Boolean.class )
    @Service( impl = HasWorkspaceSdkDefaultValueService.class )
    ValueProperty PROP_HAS_WORKSPACE_SDK = new ValueProperty( TYPE, "HasWorkspaceSDK" ); //$NON-NLS-1$

    @Override
    @DelegateImplementation( BinarySDKProjectImportOpMethods.class )
    Status execute( ProgressMonitor monitor );
}