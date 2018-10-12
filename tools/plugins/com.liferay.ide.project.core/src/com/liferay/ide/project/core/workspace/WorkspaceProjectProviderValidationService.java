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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 */
public class WorkspaceProjectProviderValidationService extends ValidationService {

	@Override
	protected Status compute() {
		NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> projectProvider = SapphireUtil.getContent(
			_op().getProjectProvider());

		if ("maven-liferay-workspace".equals(projectProvider.getShortName())) {
			return Status.createWarningStatus("Maven Liferay Workspace would not support Target Platform.");
		}

		return Status.createOkStatus();
	}

	private BaseLiferayWorkspaceOp _op() {
		return context(BaseLiferayWorkspaceOp.class);
	}

}