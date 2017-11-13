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
	"file.extensions=java,jsp,jspf", "problem.title=Changes to Indexer methods",
	"problem.summary=Method Indexer.addRelatedEntryFields(Document, Object) has been moved into RelatedEntryIndexer. " +
		"Indexer.reindexDDMStructures(List<Long>) has been moved into DDMStructureIndexer. Indexer.getQueryString(" +
			"SearchContext, Query) has been removed, in favor of calling SearchEngineUtil.getQueryString(" +
				"SearchContext, Query)",
	"problem.tickets=LPS-55928",
	"problem.section=#moved-indexer-addrelatedentryfields-and-indexer-reindexddmstructures-and-re",
	"implName=IndexerThreeMethodsChange"
},
	service = FileMigrator.class)
public class IndexerThreeMethodsChange extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();
		List<SearchResult> declarations = new ArrayList<>();
		List<SearchResult> interfaceSearch = javaFileChecker.findImplementsInterface("RelatedEntryIndexer");

		if (interfaceSearch.isEmpty()) {
			declarations = javaFileChecker.findMethodDeclaration(
				"addRelatedEntryFields", new String[] {"Document", "Object"}, null);

			searchResults.addAll(declarations);
		}

		interfaceSearch = javaFileChecker.findImplementsInterface("DDMStructureIndexer");

		if (interfaceSearch.isEmpty()) {
			declarations = javaFileChecker.findMethodDeclaration(
				"reindexDDMStructures", new String[] {"List<Long>"}, null);

			searchResults.addAll(declarations);
		}

		declarations = javaFileChecker.findMethodDeclaration(
			"getQueryString", new String[] {"SearchContext", "Query"}, null);

		searchResults.addAll(declarations);

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
			"Indexer", null, "addRelatedEntryFields", new String[] {"Document", "Object"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			"Indexer", null, "reindexDDMStructures", new String[] {"java.util.List<java.lang.Long>"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			"Indexer", null, "getQueryString", new String[] {"SearchContext", "Query"});

		searchResults.addAll(invocations);

		return searchResults;
	}

}