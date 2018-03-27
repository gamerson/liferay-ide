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
import com.liferay.ide.server.core.portal.BundleDTOWithStatus;
import com.liferay.ide.server.core.portal.BundleDTOWithStatus.ResponseState;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public abstract class BundleCommand extends GogoCommand {

	protected BundleDTOWithStatus bundleStatus;
	protected IBundleProject project;

	public BundleCommand(IBundleProject project) {

		this.project = project;
	}

	@Override
	protected void after()
		throws CoreException {

		super.after();
		try {
			if (project != null) {
				bsn = project.getSymbolicName();

				if (bsn != null && helper != null) {
					bundle = helper.getBundle(bsn);

					if (bundle != null) {
						bid = bundle.id;
					}
					else {
						bid = -1;
					}

				}
			}
			generateResponse();
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}
	}

	@Override
	protected void before()
		throws CoreException {

		try {
			if (project != null) {
				bsn = project.getSymbolicName();

				if (bsn != null && helper != null) {
					bundle = helper.getBundle(bsn);

					if (bundle != null) {
						bid = bundle.id;
					}
					else {
						bid = -1;
					}

				}
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayServerCore.createErrorStatus(e));
		}
	}

	@Override
	protected void fillResult(String result) {
		super.fillResult(result);

		if (bundleStatus != null) {
			bundleStatus.setStatus(result);
		}
	}

	protected BundleDTOWithStatus generateResponse() {

		if (bundle != null) {
			bundleStatus = new BundleDTOWithStatus(bundle, getResult());
			bundleStatus.setResponseState(ResponseState.ok);
		}
		else {
			bundleStatus = new BundleDTOWithStatus(bid, getResult(), bsn);
		}

		return bundleStatus;
	}

	public BundleDTOWithStatus generateResponse(ResponseState responseState) {

		if (bundle != null) {
			bundleStatus = new BundleDTOWithStatus(bundle, getResult());
			bundleStatus.setResponseState(responseState);
		}
		return bundleStatus;
	}

	public BundleDTOWithStatus getResponseStatus() {

		return bundleStatus;
	}

	public void setResponseState(ResponseState responseState) {

		if (bundleStatus != null) {
			bundleStatus.setResponseState(responseState);
		}
	}
}
