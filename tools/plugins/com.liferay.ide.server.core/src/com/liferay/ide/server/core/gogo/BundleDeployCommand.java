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

import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 */

public class BundleDeployCommand extends BundleCommand {

	protected String bundleUrl;
	protected IPath outputJar;

	public BundleDeployCommand(IBundleProject project, IPath outputJar) {

		super(project);
		this.outputJar = outputJar;
	}

	@Override
	protected void after()
		throws CoreException {

		try {
			super.after();
			if (bundle != null && !project.isFragmentBundle()) {
				String response = helper.run("start " + bundle.id);
				fillResult(response);
			}
			else {
				String response = helper.run("refresh " + bundle.id);
				fillResult(response);
			}
			bundle = helper.getBundle(bsn);

			if (bundle.state != Bundle.ACTIVE) {
				setResponseState(ResponseState.error);
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}
	}

	@Override
	protected void before()
		throws CoreException {

		try {
			super.before();

			if (bundle != null && !project.isFragmentBundle()) {
				helper.run("stop " + bundle.id);
			}
			setBundleUrl();
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
				helper.run("update " + bundleUrl);
			}
			else {
				helper.run("install " + bundleUrl);
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}
	}

	protected void setBundleUrl()
		throws MalformedURLException {
	}
}
