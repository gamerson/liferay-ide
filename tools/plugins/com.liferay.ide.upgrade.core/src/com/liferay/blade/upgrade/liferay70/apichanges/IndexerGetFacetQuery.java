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
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.summary=Replaced Method getFacetQuery with getFacetBooleanFilter in Indexer", "problem.tickets=LPS-56064",
	"problem.title=Indexer API Changes",
	"problem.section=#replaced-method-getpermissionquery-with-getpermissionfilter-in-searchpermis",
	"implName=IndexerGetFacetQuery"
},
	service = FileMigrator.class)
public class IndexerGetFacetQuery extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> declaration = javaFileChecker.findMethodDeclaration(
			"getFacetQuery", new String[] {"String", "SearchContextPortletURL"}, null);

		searchResults.addAll(declaration);

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations("Indexer", null, "getFacetQuery", null);

		searchResults.addAll(invocations);

		return searchResults;
	}

}