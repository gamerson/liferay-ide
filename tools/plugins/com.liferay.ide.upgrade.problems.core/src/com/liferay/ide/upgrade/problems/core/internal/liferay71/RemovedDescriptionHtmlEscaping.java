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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Haoyi Sun
 */
@Component(property = {
	"file.extensions=java,jsp,jspf", "problem.title=Removed description html escaping in PortletDisplay",
	"problem.summary=Removed Description Html Escaping", "problem.tickets=LPS-83185",
	"problem.section=#removed-description-html-escaping-in-portletdisplay", "implName=RemovedDescriptionHtmlEscaping",
	"version=7.1"
},
	service = FileMigrator.class)
public class RemovedDescriptionHtmlEscaping extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		List<FileSearchResult> methodInvocations = fileChecker.findMethodInvocations(
			"PortletDisplay", null, "setDescription", new String[] {"String"});

		searchResults.addAll(methodInvocations);

		methodInvocations = fileChecker.findMethodInvocations("PortletDisplay", null, "getDescription", null);

		searchResults.addAll(methodInvocations);

		return searchResults;
	}

}