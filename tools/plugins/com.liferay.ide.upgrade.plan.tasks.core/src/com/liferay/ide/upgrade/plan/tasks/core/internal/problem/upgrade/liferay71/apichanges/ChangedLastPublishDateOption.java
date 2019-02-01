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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.liferay71.apichanges;

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.JavaFileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.JavaFile;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.SearchResult;

import java.io.File;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Haoyi Sun
 */
@Component(property = {
	"file.extensions=java", "problem.title=Changed the From Last Publish Date Option in Staging",
	"problem.summary=Changed Last Publish Date Option", "problem.tickets=LPS-81695",
	"problem.section=#changed-last-publish-date-option", "implName=ChangedLastPublishDateOption", "version=7.1"
},
	service = FileMigrator.class)
public class ChangedLastPublishDateOption extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile fileChecker) {
		return fileChecker.findMethodDeclaration(
			"doPrepareManifestSummary", new String[] {"PortletDataContext", "PortletPreferences "}, null);
	}

}