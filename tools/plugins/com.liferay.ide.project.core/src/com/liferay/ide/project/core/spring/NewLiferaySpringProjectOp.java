/**
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
 */

package com.liferay.ide.project.core.spring;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionDefaultValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface NewLiferaySpringProjectOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewLiferaySpringProjectOp.class);

	@DelegateImplementation(NewLiferaySpringProjectOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getComponentName();

	public Value<String> getDependencyInjector();

	public Value<String> getFramework();

	public Value<String> getFrameworkDependencies();

	public Value<String> getLiferayVersion();

	public Value<String> getPackageName();

	public Value<String> getProjectTemplateName();

	public Value<String> getViewType();

	public void setComponentName(String value);

	public void setDependencyInjector(String value);

	public void setFramework(String value);

	public void setFrameworkDependencies(String value);

	public void setLiferayVersion(String value);

	public void setPackageName(String value);

	public void setProjectTemplateName(String value);

	public void setViewType(String value);

	@Label(standard = "Component Class Name")
	@Service(impl = SpringComponentNameDefaultValueService.class)
	@Service(impl = SpringComponentNameValidationService.class)
	public ValueProperty PROP_COMPONENT_NAME = new ValueProperty(TYPE, "ComponentName");

	@DefaultValue(text = "DS")
	@Label(standard = "dependency injector")
	@Service(impl = SpringDependenciesInjectorPossibleValuesService.class)
	public ValueProperty PROP_DEPENDENCY_INJECTOR = new ValueProperty(TYPE, "DependencyInjector");

	@DefaultValue(text = "Portlet MVC For SPring")
	@Label(standard = "framework")
	@Service(impl = SpringFrameworkPossibleValuesService.class)
	public ValueProperty PROP_FRAMEWORK = new ValueProperty(TYPE, "Framework");

	@DefaultValue(text = "Provided")
	@Label(standard = "framework dependencies")
	@Service(impl = SpringFrameworkDependenciesPossibleValuesService.class)
	public ValueProperty PROP_FRAMEWORK_DEPENDENCIES = new ValueProperty(TYPE, "FrameworkDependencies");

	@Label(standard = "liferay version")
	@Service(impl = TargetLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = SpringProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	@Label(standard = "Package name")
	@Service(impl = SpringPackageNameDefaultValueService.class)
	public ValueProperty PROP_PACKAGE_NAME = new ValueProperty(TYPE, "PackageName");

	@Listeners(SpringProjectNameListener.class)
	@Service(impl = ModuleProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Label(standard = "build type")
	@Listeners(SpringProjectNameListener.class)
	@Service(impl = SpringProjectProviderDefaultValueService.class)
	@Service(impl = SpringProjectProviderPossibleValuesService.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@InitialValue(text = "spring-mvc-portlet")
	@Label(standard = "Project Template Name")
	@Listeners(SpringProjectNameListener.class)
	public ValueProperty PROP_PROJECT_TEMPLATE_NAME = new ValueProperty(TYPE, "ProjectTemplateName");

	@Listeners(SpringProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

	@DefaultValue(text = "Jsp")
	@Label(standard = "view type")
	@Service(impl = SpringViewtypePossibleValuesService.class)
	public ValueProperty PROP_VIEW_TYPE = new ValueProperty(TYPE, "ViewType");

}