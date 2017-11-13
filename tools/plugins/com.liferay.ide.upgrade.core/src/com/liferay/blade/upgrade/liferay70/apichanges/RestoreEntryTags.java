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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Changed the Usage of the liferay-ui:restore-entry Tag",
	"problem.section=#changed-the-usage-of-the-liferay-uirestore-entry-tag",
	"problem.summary=Changed the Usage of the liferay-ui:restore-entry Tag", "problem.tickets=LPS-54106",
	"implName=RestoreEntryTags"
},
	service = FileMigrator.class)
public class RestoreEntryTags extends JSPTagMigrator {

	public RestoreEntryTags() {
		super(new String[0], new String[0], new String[0], new String[0], _tagNames, new String[0]);
	}

	private static final String[] _tagNames = {"liferay-ui:restore-entry"};

}