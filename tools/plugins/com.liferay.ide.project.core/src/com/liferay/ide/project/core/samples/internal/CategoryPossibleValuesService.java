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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.samples.NewSampleOp;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Terry Jia
 */
public class CategoryPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		if (_op() != null) {
			SapphireUtil.detachListener(_op().property(NewSampleOp.PROP_BUILD_TYPE), _listener);
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

		try {
			Path cachePath = SampleUtil.getSamplesCachePath();

			File bladeRepo = new File(cachePath.toFile(), bladeRepoName);

			File samples = new File(bladeRepo, buildType);

			for (File file : samples.listFiles()) {
				String fileName = file.getName();

				if (file.isDirectory() && _topLevelFolders.contains(fileName)) {
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

		SapphireUtil.attachListener(_op().property(NewSampleOp.PROP_BUILD_TYPE), _listener);
	}

	private NewSampleOp _op() {
		return context(NewSampleOp.class);
	}

	private static final Collection<String> _topLevelFolders = Arrays.asList(
		"apps", "ext", "extensions", "overrides", "themes", "tests", "wars");

	private FilteredListener<PropertyContentEvent> _listener;

}