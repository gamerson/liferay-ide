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

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Removed the aui:button-item Tag and Replaced with aui:button",
	"problem.section=#removed-the-auibutton-item-tag-and-replaced-with-auibutton",
	"problem.summary=Removed the aui:button-item Tag and Replaced with aui:button", "problem.tickets=LPS-62922",
	"auto.correct=jsptag", "implName=AUIButtonItemTags"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class AUIButtonItemTags extends JSPTagMigrator {

	public AUIButtonItemTags() {
		super(new String[0], new String[0], new String[0], new String[0], _tagNames, _newTagNames);
	}

	private static final String[] _newTagNames = {"aui:button"};
	private static final String[] _tagNames = {"aui:button-item"};

}