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

package com.liferay.ide.upgrade.planner.core;

import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public abstract class BaseUpgradeTask implements UpgradeTask {

	@Activate
	public void activate(ComponentContext componentContext) {
		Dictionary<String, Object> properties = componentContext.getProperties();

		_categoryId = _getProperty(properties, "categoryId");
		_description = _getProperty(properties, "description");
		_id = _getProperty(properties, "id");
		_title = _getProperty(properties, "title");

		_lookupTasks(componentContext);
	}

	@Override
	public String getCategoryId() {
		return _categoryId;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public List<UpgradeTaskStep> getSteps() {
		return null;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	private String _getProperty(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	private void _lookupTasks(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		try {
			bundleContext.getServiceReferences(UpgradeTaskStep.class, "(taskId=" + _id + ")");
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private String _categoryId;
	private String _description;
	private String _id;
	private String _title;

}