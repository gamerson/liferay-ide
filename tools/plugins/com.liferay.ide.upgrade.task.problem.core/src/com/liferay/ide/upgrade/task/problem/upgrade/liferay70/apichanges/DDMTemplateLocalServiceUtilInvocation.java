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

package com.liferay.ide.upgrade.task.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.task.problem.api.FileMigrator;
import com.liferay.ide.upgrade.task.problem.api.JavaFile;
import com.liferay.ide.upgrade.task.problem.api.SearchResult;
import com.liferay.ide.upgrade.task.problem.upgrade.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.title=Added Required Parameter resourceClassNameId for DDM Template Search Operations",
	"problem.section=#added-required-parameter-resourceclassnameid-for-ddm-template-search-operat",
	"problem.summary=Added Required Parameter resourceClassNameId for DDM Template Search Operations",
	"problem.type=java,jsp", "problem.tickets=LPS-52990", "implName=DDMTemplateLocalServiceUtilInvocation",
	"version=7.0"
},
	service = FileMigrator.class)
public class DDMTemplateLocalServiceUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		result.addAll(javaFileChecker.findMethodInvocations(null, "DDMTemplateLocalServiceUtil", "search", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DDMTemplateLocalServiceUtil", "searchCount", null));

		return result;
	}

}