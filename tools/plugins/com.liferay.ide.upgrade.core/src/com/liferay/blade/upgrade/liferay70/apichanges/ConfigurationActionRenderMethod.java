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
	"file.extensions=java", "problem.summary=Removed render Method from ConfigurationAction API",
	"problem.tickets=LPS-56300", "problem.title=ConfigurationAction render method",
	"problem.section=#removed-render-method-from-configurationaction-api", "implName=ConfigurationActionRenderMethod"
},
	service = FileMigrator.class)
public class ConfigurationActionRenderMethod extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		// render method declarations

		List<SearchResult> declarations = javaFileChecker.findMethodDeclaration(
			"render", new String[] {"PortletConfig", "RenderRequest", "RenderResponse"}, null);

		searchResults.addAll(declarations);

		// render method invocations

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
			"ConfigurationAction", null, "render", null);

		searchResults.addAll(invocations);

		return searchResults;
	}

}