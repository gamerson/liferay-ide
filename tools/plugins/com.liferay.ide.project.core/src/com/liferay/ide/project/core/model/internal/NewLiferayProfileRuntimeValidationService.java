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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Objects;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Tao Tao
 */
public class NewLiferayProfileRuntimeValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = context(NewLiferayPluginProjectOp.class);

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = get(op.getProjectProvider());

		if (Objects.equals("maven", provider.getShortName())) {
			NewLiferayProfile newLiferayProfile = context(NewLiferayProfile.class);

			String runtimeName = get(newLiferayProfile.getRuntimeName());

			IRuntime runtime = ServerUtil.getRuntime(runtimeName);

			if (runtime == null) {
				retval = Status.createErrorStatus("Liferay runtime must be configured.");
			}
		}

		return retval;
	}

}