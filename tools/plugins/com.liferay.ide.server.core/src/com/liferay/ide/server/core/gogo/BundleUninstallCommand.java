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

package com.liferay.ide.server.core.gogo;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.BundleDTOWithStatus.ResponseState;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class BundleUninstallCommand extends BundleCommand {

	public BundleUninstallCommand(IBundleProject project) {

		super(project);
	}

	@Override
	protected void after()
		throws CoreException {

		super.after();
		try {
			if (bundle != null) {
				setResponseState(ResponseState.error);
			}
			else {
				setResponseState(ResponseState.ok);
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}
	}

	@Override
	protected void execute()
		throws CoreException {

		try {
			if (bundle != null) {
				String response = helper.run("uninstall " + bid);
				fillResult(response);
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}

	}

}
