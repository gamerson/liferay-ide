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
	"problem.summary=Removed get and format Methods that Used PortletConfig Parameters", "problem.tickets=LPS-44342",
	"problem.title=PortletConfig get/format methods",
	"problem.section=#removed-get-and-format-methods-that-used-portletconfig-parameters",
	"implName=PortletConfigGetFormatMethods"
},
	service = FileMigrator.class)
public class PortletConfigGetFormatMethods extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		// LanguageUtil get methods

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "get", new String[] {"PortletConfig", "Locale", "String"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "get", new String[] {"PortletConfig", "Locale", "String", "String"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "get", new String[] {"PageContext", "String"});

		searchResults.addAll(invocations);
		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "get", new String[] {"PageContext", "String", "String"});

		searchResults.addAll(invocations);

		// UnicodeLanguageUtil get methods

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "get", new String[] {"PortletConfig", "Locale", "String"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "get", new String[] {"PortletConfig", "Locale", "String", "String"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "get", new String[] {"PageContext", "String"});

		searchResults.addAll(invocations);
		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "get", new String[] {"PageContext", "String", "String"});

		searchResults.addAll(invocations);

		// LanguageUtil format methods

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object[]", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper[]", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "Object"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "Object[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "format", new String[] {"PageContext", "String", "Object[]", "boolean"});

		searchResults.addAll(invocations);

		// LanguageUtil getTimeDescription methods

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "getTimeDescription", new String[] {"PageContext", "long"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "getTimeDescription", new String[] {"PageContext", "long", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "LanguageUtil", "getTimeDescription", new String[] {"PageContext", "Long"});

		searchResults.addAll(invocations);

		// UnicodeLanguageUtil format methods

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format",
			new String[] {"PortletConfig", "Locale", "String", "Object", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PortletConfig", "Locale", "String", "Object[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format",
			new String[] {"PortletConfig", "Locale", "String", "Object[]", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format",
			new String[] {"PageContext", "String", "LanguageWrapper", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PageContext", "String", "LanguageWrapper[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format",
			new String[] {"PageContext", "String", "LanguageWrapper[]", "boolean"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PageContext", "String", "Object"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PageContext", "String", "Object[]"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "format", new String[] {"PageContext", "String", "Object[]", "boolean"});

		searchResults.addAll(invocations);

		// UnicodeLanguageUtil getTimeDescription methods

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "getTimeDescription", new String[] {"PageContext", "long"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "UnicodeLanguageUtil", "getTimeDescription", new String[] {"PageContext", "Long"});

		searchResults.addAll(invocations);

		return searchResults;
	}

}