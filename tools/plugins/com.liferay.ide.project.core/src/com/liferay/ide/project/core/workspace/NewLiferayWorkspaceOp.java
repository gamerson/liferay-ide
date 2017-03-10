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
package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.modules.PropertyKey;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public interface NewLiferayWorkspaceOp extends BaseLiferayWorkspaceOp
{
    ElementType TYPE = new ElementType( NewLiferayWorkspaceOp.class );

    // *** WorkspaceName ***

    @Required
    @Label( standard = "workspace name" )
    @Service( impl = WorkspaceNameValidationService.class )
    ValueProperty PROP_WORKSPACE_NAME = new ValueProperty( TYPE, "WorkspaceName" );

    Value<String> getWorkspaceName();
    void setWorkspaceName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( WorkspaceUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" );

    Value<Boolean> getUseDefaultLocation();
    void setUseDefaultLocation( String value );
    void setUseDefaultLocation( Boolean value );

    // *** ProjectLocation ***

    @Required
    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = WorkspaceLocationValidationService.class )
    @Service( impl = WorkspaceLocationInitialValueService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );

    // *** serverName ***

    @Service( impl = NewLiferayWorkspaceServerNameService.class )
    ValueProperty PROP_SERVER_NAME = new ValueProperty( TYPE, BaseLiferayWorkspaceOp.PROP_SERVER_NAME );

    // *** Environment ***

    @Label( standard = "environment" )
    @DefaultValue( text = "local" )
    ValueProperty PROP_ENVIRONMENT = new ValueProperty( TYPE, "Environment" );

    Value<String> getEnvironment();

    void setEnvironment( String environment );

    // *** ModulesDefaultRepositoryEnabled ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    @Label( standard = "modules default repository enabled" )
    ValueProperty PROP_MODULES_DEFAULT_REPOSITORY_ENABLED =
        new ValueProperty( TYPE, "ModulesDefaultRepositoryEnabled" );

    Value<Boolean> getModulesDefaultRepositoryEnabled();

    void setModulesDefaultRepositoryEnabled( Boolean modulesDefaultRepositoryEnabled );

    // *** ModulesDir ***

    @Label( standard = "modules dir" )
    @DefaultValue( text = "modules" )
    ValueProperty PROP_MODULES_DIR = new ValueProperty( TYPE, "ModulesDir" );

    Value<String> getModulesDir();

    void setModulesDir( String modulesDir );

    // *** ThemesDir ***

    @Label( standard = "themes dir" )
    @DefaultValue( text = "themes" )
    ValueProperty PROP_THEMES_DIR = new ValueProperty( TYPE, "ThemesDir" );

    Value<String> getThemesDir();

    void setThemesDir( String themesDir );

    // *** WarsDir ***

    @Label( standard = "wars dir" )
    @DefaultValue( text = "wars" )
    ValueProperty PROP_WARS_DIR = new ValueProperty( TYPE, "WarsDir" );

    Value<String> getWarsDir();

    void setWarsDir( String warsDir );

    // *** AdditionalProperties ***

    @Type( base = AdditionalProperty.class )
    @Label( standard = "additional properties" )
    ListProperty PROP_ADDITIONAL_PROPERTIES = new ListProperty( TYPE, "AdditionalProperties" );

    ElementList<AdditionalProperty> getAdditionalProperties();

    // *** PropertyKeys ***

    @Type( base = PropertyKey.class )
    @Label( standard = "Properties" )
    ListProperty PROP_PROPERTYKEYS = new ListProperty( TYPE, "PropertyKeys" );
    ElementList<PropertyKey> getPropertyKeys();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayWorkspaceOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
