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

package com.liferay.ide.project.core.samples.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.samples.NewSampleOp;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Terry Jia
 */
public class SampleNamePossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		if (_op() != null) {
			SapphireUtil.detachListener(_op().property(NewSampleOp.PROP_CATEGORY), _listener);
		}

		super.dispose();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		String liferayVersion = get(_op().getLiferayVersion());

		String bladeRepoName = "liferay-blade-samples-" + liferayVersion;

		String buildType = get(_op().getBuildType());

		String category = get(_op().getCategory());

		if (CoreUtil.isNullOrEmpty(category)) {
			return;
		}

		try {
			Path cachePath = SampleUtil.getSamplesCachePath();

			File bladeRepo = new File(cachePath.toFile(), bladeRepoName);

			File buildTypeFile = new File(bladeRepo, buildType);

			File categoryFile = new File(buildTypeFile, category);

			if (!categoryFile.exists()) {
				return;
			}

			for (File file : categoryFile.listFiles()) {
				String fileName = file.getName();

				if (file.isDirectory()) {
					values.add(fileName);
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op().property(NewSampleOp.PROP_CATEGORY), _listener);
	}

	private NewSampleOp _op() {
		return context(NewSampleOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}