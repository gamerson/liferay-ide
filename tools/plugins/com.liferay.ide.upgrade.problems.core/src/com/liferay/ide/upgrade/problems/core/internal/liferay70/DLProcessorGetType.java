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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java", "problem.title=Created a New getType Method That is Implemented in DLProcessor",
	"problem.summary=The DLProcessor interface has a new method getType().", "problem.tickets=LPS-53574",
	"problem.section=#created-a-new-gettype-method-that-is-implemented-in-dlprocessor", "implName=DLProcessorGetType",
	"version=7.0"
},
	service = FileMigrator.class)
public class DLProcessorGetType extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findImplementsInterface("DLProcessor");
	}

}