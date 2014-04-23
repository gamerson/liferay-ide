/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.project.core.model.internal.ProjectActionPossibleValuesService;
import com.liferay.ide.project.core.model.internal.ProjectUpgradeRuntimeValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author Simon Jiang
 */

public interface ProjectUpgradeOp extends ExecutableElement, HasLiferayRuntime
{

    ElementType TYPE = new ElementType( ProjectUpgradeOp.class );

    @Type( base = ProjectItem.class )
    ListProperty PROP_SELECTED_PROJECTS = new ListProperty( TYPE, "SelectedProjects" );

    ElementList<ProjectItem> getSelectedProjects();


    @Service( impl = ProjectUpgradeRuntimeValidationService.class )
    ValueProperty PROP_RUNTIME_NAME = new ValueProperty( TYPE, HasLiferayRuntime.PROP_RUNTIME_NAME ); //$NON-NLS-1$


    @Type( base = ProjectAction.class )
    @Unique
    @CountConstraint( min = 1 )
    @Service( impl = ProjectActionPossibleValuesService.class )
    ListProperty PROP_SELECTED_ACTIONS = new ListProperty( TYPE, "SelectedActions" );

    ElementList<ProjectAction> getSelectedActions();


    @DelegateImplementation( ProjectUpgradeOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
