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

package com.liferay.blade.upgrade.liferay70.descriptors;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.apichanges.BaseLiferayDescriptorVersion;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(property = {
	"file.extensions=xml", "problem.title=Descriptor XML DTD Versions Changes",
	"problem.summary=The descriptor XML DTD versions should be matched with version 7.0.",
	"problem.section=#descriptor-XML-DTD-version", "auto.correct=descriptor", "implName=LiferayDescriptorVersion",
	"version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class LiferayDescriptorVersion70 extends BaseLiferayDescriptorVersion {

	public LiferayDescriptorVersion70() {
		super(_PUBLICID_REGREX70);
	}

	private static final String _PUBLICID_REGREX70 =
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(7\\.[0-9]\\.[0-9])\\//(?:[A-z]+)";

}