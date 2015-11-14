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

import com.liferay.blade.api.ProjectType;
import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.LiferayModuleProjectProvider;
import com.liferay.ide.project.core.model.internal.ModuleProjectArtifactVersionDefaultValueService;
import com.liferay.ide.project.core.model.internal.ModuleProjectGroupIdDefaultValueService;
import com.liferay.ide.project.core.model.internal.ModuleProjectGroupIdValidationService;
import com.liferay.ide.project.core.model.internal.ModuleProjectLocationValidationService;
import com.liferay.ide.project.core.model.internal.ModuleProjectNameListener;
import com.liferay.ide.project.core.model.internal.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.model.internal.ModuleProjectProviderDefaultValueService;
import com.liferay.ide.project.core.model.internal.ModuleProjectProviderPossibleValuesService;
import com.liferay.ide.project.core.model.internal.ModuleProjectTypeDefaultValueService;
import com.liferay.ide.project.core.model.internal.ModuleProjectTypePossibleValuesService;
import com.liferay.ide.project.core.model.internal.ModuleProjectUseDefaultLocationListener;

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
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Simon Jiang
 */
public interface NewLiferayModuleProjectOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( NewLiferayModuleProjectOp.class );

    // *** Archetype ***

    //@Service( impl = ArchetypeDefaultValueService.class )
    ValueProperty PROP_ARCHETYPE = new ValueProperty( TYPE, "Archetype" ); //$NON-NLS-1$

    Value<String> getArchetype();
    void setArchetype( String value );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( ModuleProjectNameListener.class )
    @Service( impl = ModuleProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" ); //$NON-NLS-1$

    Value<String> getProjectName();
    void setProjectName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( ModuleProjectUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" ); //$NON-NLS-1$

    Value<Boolean> getUseDefaultLocation();
    void setUseDefaultLocation( String value );
    void setUseDefaultLocation( Boolean value );


    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = ModuleProjectLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" ); //$NON-NLS-1$

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );


    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Listeners( ModuleProjectNameListener.class )
    @Services
    (
        value=
        {
            @Service( impl = ModuleProjectProviderPossibleValuesService.class ),
            @Service( impl = ModuleProjectProviderDefaultValueService.class )
        }
    )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" ); //$NON-NLS-1$

    Value<LiferayModuleProjectProvider> getProjectProvider();
    void setProjectProvider( String value );
    void setProjectProvider( LiferayModuleProjectProvider value );


    // *** Project Template ***

    @Type( base = ProjectType.class )
    @Label( standard = "Project Template" )
    @Listeners( ModuleProjectNameListener.class )
    @Services
    (
        value=
        {
            @Service( impl = ModuleProjectTypePossibleValuesService.class ),
            @Service( impl = ModuleProjectTypeDefaultValueService.class )
        }
    )
    ValueProperty PROP_PROJECT_TEMPLATE = new ValueProperty( TYPE, "ProjectTemplate" ); //$NON-NLS-1$

    Value<ProjectType> getProjectTemplate();
    void setProjectTemplate( String value );
    void setProjectTemplate( ProjectType value );

    // *** Maven settings ***
    // *** ArtifactVersion ***

    @Label( standard = "artifact version" )
    @Service( impl = ModuleProjectArtifactVersionDefaultValueService.class )
    ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty( TYPE, "ArtifactVersion" ); //$NON-NLS-1$

    Value<String> getArtifactVersion();
    void setArtifactVersion( String value );


    // *** GroupId ***

    @Label( standard = "group id" )
    @Services
    (
        value =
        {
            @Service( impl = ModuleProjectGroupIdValidationService.class ),
            @Service( impl = ModuleProjectGroupIdDefaultValueService.class )
        }
    )
    @Whitespace( trim = false )
    ValueProperty PROP_GROUP_ID = new ValueProperty( TYPE, "GroupId" ); //$NON-NLS-1$

    Value<String> getGroupId();
    void setGroupId( String value );

    // *** FinalProjectName ***

    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();
    void setFinalProjectName( String value );


    // *** PortletName ***

    @Label( standard = "portlet name" )
    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_PORTLET_NAME = new ValueProperty( TYPE, "PortletName" );

    Value<String> getPortletName();
    void setPortletName( String value );

    // *** ProjectNames ***

    @Type( base = ProjectName.class )
    ListProperty PROP_PROJECT_NAMES = new ListProperty( TYPE, "ProjectNames" );

    ElementList<ProjectName> getProjectNames();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayModuleProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
