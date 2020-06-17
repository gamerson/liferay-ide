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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Ethan Sun
 */
public class ProductVersionPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	public ProductVersionPossibleValuesService() {
		_productVersions = new CopyOnWriteArrayList<>();
		_promotedProductVersions = new CopyOnWriteArrayList<>();
	}

	@Override
	public void dispose() {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
		}

		super.dispose();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		if (get(op.getShowAllVersionProduct())) {
			values.addAll(_productVersions);
			String[] productVersions = _productVersions.toArray(new String[0]);

			if (!ListUtil.isEmpty(productVersions)) {
				op.setProductVersion(productVersions[0]);
			}
		}
		else {
			values.addAll(_promotedProductVersions);

			String[] productVersions = _promotedProductVersions.toArray(new String[0]);

			if (!ListUtil.isEmpty(productVersions)) {
				op.setProductVersion(productVersions[0]);
			}
		}
	}

	@Override
	protected void initPossibleValuesService() {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		Job getProductVersions = new Job("Get product versions") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					String[] allWorkspaceProducts = BladeCLI.getWorkspaceProducts(true);

					if (!_isEmpty(allWorkspaceProducts)) {
						_productVersions.clear();

						Collections.addAll(_productVersions, allWorkspaceProducts);
					}

					String[] promotedProducts = BladeCLI.getWorkspaceProducts(false);

					if (!_isEmpty(promotedProducts)) {
						_promotedProductVersions.clear();

						Collections.addAll(_promotedProductVersions, promotedProducts);
					}

					refresh();
				}
				catch (Exception exception) {
					ProjectCore.logError("Failed to init product version list.", exception);
				}

				return Status.OK_STATUS;
			}

		};

		getProductVersions.setSystem(true);

		getProductVersions.schedule();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
	}

	private boolean _isEmpty(String[] values) {
		if ((values == null) || (values.length == 0)) {
			return true;
		}

		return false;
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private List<String> _productVersions;
	private List<String> _promotedProductVersions;

}