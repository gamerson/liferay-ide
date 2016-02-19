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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface NewModuleComponentOp  extends ExecutableElement
{
    ElementType TYPE = new ElementType( NewModuleComponentOp.class );

    @Label( standard = "project name" )
    @Listeners( ModuleProjectNameListener.class )
    @Service( impl = ModuleProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();

    void setProjectName( String value );

    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = ModuleProjectLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();

    void setLocation( String value );

    void setLocation( Path value );

    // *** ComponentName ***
    @Label( standard = "Component Name" )
    @Services
    (
        {
            @Service( impl = ComponentNameValidationService.class ),
            @Service( impl = ComponentNameDefaultValueService.class )
        }
    )
    ValueProperty PROP_COMPONENT_NAME = new ValueProperty( TYPE, "ComponentName" );

    Value<String> getComponentName();
    void setComponentName( String value );

    // *** ServiceName ***
    @Label( standard = "Service Name" )
    @Required
    @Services
    (
        {
            @Service( impl = ServicePossibleValuesService.class ),
            @Service( impl = ServiceNameValidataionService.class )
        }
    )
    ValueProperty PROP_SERVICE_NAME = new ValueProperty( TYPE, "ServiceName" );

    Value<String> getServiceName();
    void setServiceName( String value );

    // *** PackageeName ***

    @Label( standard = "Package Name" )
    @Services
    (
        {
            @Service( impl = PackageNameValidationService.class ),
            @Service( impl = PackageNameDefaultValueService.class )
        }
    )
    ValueProperty PROP_PACKAGE_NAME = new ValueProperty( TYPE, "PackageName" );

    Value<String> getPackageName();
    void setPackageName( String value );

    // *** PropertyKeys ***
    @Type( base = PropertyKey.class )
    @Label( standard = "Properties" )
    ListProperty PROP_PROPERTYKEYS = new ListProperty( TYPE, "PropertyKeys" );
    ElementList<PropertyKey> getPropertyKeys();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewModuleComponentOpMethods.class )
    Status execute( ProgressMonitor monitor );
}