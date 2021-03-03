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

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=jsp,jspf", "problem.title=Renamed URI Attribute Used to Generate AUI Tag Library",
		"problem.section=#renamed-uri-attribute-used-to-generate-aui-tag-library",
		"problem.summary=We should use the new AUI URI declaration:http://liferay.com/tld/aui",
		"problem.tickets=LPS-57809", "auto.correct=jsptag", "problem.version=7.0", "version=7.0"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class AUILiferayTags extends JSPTagMigrator {

	public AUILiferayTags() {
		super(_ATTR_NAMES, new String[0], _ATTR_VALUES, _NEW_ATTR_VALUES, _TAG_NAMES, new String[0]);
	}

	private static final String[] _ATTR_NAMES = {"uri"};

	private static final String[] _ATTR_VALUES = {"http://alloy.liferay.com/tld/aui"};

	private static final String[] _NEW_ATTR_VALUES = {"http://liferay.com/tld/aui"};

	private static final String[] _TAG_NAMES = {"jsp:directive.taglib"};

}