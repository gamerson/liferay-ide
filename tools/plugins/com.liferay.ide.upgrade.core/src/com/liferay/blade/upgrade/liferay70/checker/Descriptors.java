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

package com.liferay.blade.upgrade.liferay70.checker;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;
import com.liferay.blade.upgrade.XMLFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(property = {
	"file.extensions=xml", "problem.title=DTD versions",
	"problem.summary=This step upgrades descriptor XML DTD versions from 6.2 to 7.0 or 7.1 and deletes the " +
		"wap-template-path \\ntag from liferay-layout-template.xml files.",
	"problem.section=#descriptor-XML-DTD-version", "implName=Descriptors", "version=7.0"
},
	service = FileMigrator.class)
public class Descriptors extends XMLFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!"liferay-portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		List<SearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.getDocumentTypeDeclaration("7.0"));

		return results;
	}

}