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

package com.liferay.ide.project.core.samples;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.samples.internal.CategoryPossibleValuesService;
import com.liferay.ide.project.core.samples.internal.SampleNamePossibleValuesService;
import com.liferay.ide.project.core.samples.internal.SampleProjectNameListener;
import com.liferay.ide.project.core.samples.internal.SampleProjectNameValidationService;
import com.liferay.ide.project.core.samples.internal.SampleProjectUseDefaultLocationListener;
import com.liferay.ide.project.core.samples.internal.TargetLiferayVersionValidationService;
import com.liferay.ide.project.core.service.TargetLiferayVersionDefaultValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Terry Jia
 */
public interface NewSampleOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewSampleOp.class);

	@DelegateImplementation(NewSampleOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getBuildType();

	public Value<String> getCategory();

	public Value<String> getLiferayVersion();

	public Value<String> getSampleName();

	@DefaultValue(text = "liferay-workspace")
	@PossibleValues(values = "liferay-workspace")
	public ValueProperty PROP_BUILD_TYPE = new ValueProperty(TYPE, "BuildType");

	@Listeners(SampleProjectNameListener.class)
	@Required
	@Service(impl = CategoryPossibleValuesService.class)
	public ValueProperty PROP_CATEGORY = new ValueProperty(TYPE, "Category");

	@Label(standard = "liferay version")
	@Service(impl = TargetLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	@Service(impl = TargetLiferayVersionValidationService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@Listeners(SampleProjectNameListener.class)
	@Service(impl = SampleProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Required
	@Service(impl = SampleNamePossibleValuesService.class)
	public ValueProperty PROP_SAMPLE_NAME = new ValueProperty(TYPE, "SampleName");

	@Listeners(SampleProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

}