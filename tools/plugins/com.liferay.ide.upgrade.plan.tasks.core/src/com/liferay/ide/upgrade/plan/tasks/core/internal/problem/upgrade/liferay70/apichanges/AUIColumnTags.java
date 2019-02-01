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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.JSPTagMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.AutoMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=The aui:column taglib has been removed and replaced with aui:col taglib",
	"problem.section=#removed-the-auicolumn-tag-and-replaced-with-auicol",
	"problem.summary=The aui:column taglib has been removed and replaced with aui:col taglib",
	"problem.tickets=LPS-62208", "auto.correct=jsptag", "implName=AUIColumnTags", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class AUIColumnTags extends JSPTagMigrator {

	public AUIColumnTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, _NEW_TAG_NAMES);
	}

	private static final String[] _NEW_TAG_NAMES = {"aui:col"};

	private static final String[] _TAG_NAMES = {"aui:column"};

}