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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;

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
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ModuleProjectGroupIdValidationService extends ValidationService {

	@Override
	protected Status compute() {
		NewLiferayModuleProjectOp op = _op();

		NewLiferayProjectProvider<BaseModuleOp> provider = SapphireUtil.getContent(op.getProjectProvider());

		if ("maven-module".equals(provider.getShortName())) {
			String groupId = SapphireUtil.getContent(op.getGroupId());

			IStatus javaStatus = JavaConventions.validatePackageName(
				groupId, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			if (!javaStatus.isOK()) {
				return StatusBridge.create(javaStatus);
			}
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = _op();

		SapphireUtil.attachListener(op.getProjectProvider(), _listener);
		SapphireUtil.attachListener(op.getPackageName(), _listener);
		SapphireUtil.attachListener(op.getLocation(), _listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private Listener _listener;

}