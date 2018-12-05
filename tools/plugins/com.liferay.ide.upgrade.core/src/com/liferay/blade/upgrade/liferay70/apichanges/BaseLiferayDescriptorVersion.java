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

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;
import com.liferay.blade.upgrade.XMLFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Seiphon Wang
 */
@SuppressWarnings("restriction")
public abstract class BaseLiferayDescriptorVersion extends XMLFileMigrator implements AutoMigrator {

	public BaseLiferayDescriptorVersion(String publicIDPattern, String version) {
		_publicIDPattern = publicIDPattern;
		_version = version;
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int problemsCorrected = 0;

		try {
			IFile xmlFile = getXmlFile(file);

			IModelManager modelManager = StructuredModelManager.getModelManager();

			IDOMModel domModel = (IDOMModel)modelManager.getModelForRead(xmlFile);

			IDOMDocument domDocument = domModel.getDocument();

			IDOMDocumentType domDocumentType = (IDOMDocumentType)domDocument.getDoctype();

			for (int i = 0; i < problems.size(); i++) {
				if (domDocumentType != null) {
					String publicId = domDocumentType.getPublicId();

					String newPublicId = _getNewDoctTypeSetting(publicId, _version, _PUBLICID_REGREX);

					domDocumentType.setPublicId(newPublicId);

					String systemId = domDocumentType.getSystemId();

					String newSystemId = _getNewDoctTypeSetting(
						systemId, _version.replaceAll("\\.", "_"), _SYSTEMID_REGREX);

					domDocumentType.setSystemId(newSystemId);

					problemsCorrected++;
				}

				domModel.save();
			}
		}
		catch (Exception e) {
		}

		return problemsCorrected;
	}

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		List<SearchResult> results = new ArrayList<>();

		for (String liferayDtdName : _liferayDtdNames) {
			results.add(xmlFileChecker.findDocumentTypeDeclaration(liferayDtdName, _publicIDPattern));
		}

		return results;
	}

	private String _getNewDoctTypeSetting(String doctypeSetting, String newValue, String regrex) {
		String newDoctTypeSetting = null;

		Pattern p = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher m = p.matcher(doctypeSetting);

		if (m.find()) {
			String oldVersionString = m.group(m.groupCount());

			newDoctTypeSetting = doctypeSetting.replace(oldVersionString, newValue);
		}

		return newDoctTypeSetting;
	}

	private static final String _PUBLICID_REGREX =
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[A-z]+)";

	private static final String _SYSTEMID_REGREX =
		"^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

	private String[] _liferayDtdNames =
		{"liferay-portlet-app", "display", "service-builder", "hook", "layout-templates", "look-and-feel"};
	private String _publicIDPattern;
	private String _version;

}