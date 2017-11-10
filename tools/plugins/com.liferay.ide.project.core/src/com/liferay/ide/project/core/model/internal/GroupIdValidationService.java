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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class GroupIdValidationService extends ValidationService {

	@Override
	protected Status compute() {
		NewLiferayPluginProjectOp op = _op();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		if ("maven".equals(provider.getShortName())) {
			String groupId = op.getGroupId().content(true);

			IStatus javaStatus = JavaConventions.validatePackageName(
				groupId, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			return StatusBridge.create(javaStatus);
		}

		return StatusBridge.create(org.eclipse.core.runtime.Status.OK_STATUS);
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		op.getProjectProvider().attach(_listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private Listener _listener;

}