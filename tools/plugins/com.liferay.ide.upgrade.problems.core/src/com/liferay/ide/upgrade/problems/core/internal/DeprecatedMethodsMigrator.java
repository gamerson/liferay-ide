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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Component(property = "file.extensions=java,jsp,jspf", service = FileMigrator.class)
public class DeprecatedMethodsMigrator extends JavaFileMigrator {

	public DeprecatedMethodsMigrator() {
		_deprecatedMethods = _getDeprecatedMethods();
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();
		String fileExtension = new Path(
			file.getAbsolutePath()
		).getFileExtension();

		for (JSONArray deprecatedMethodsArray : _deprecatedMethods) {
			for (int j = 0; j < deprecatedMethodsArray.length(); j++) {
				try {
					_tempMethod = deprecatedMethodsArray.getJSONObject(j);

					List<FileSearchResult> searchResults = searchFile(
						file, createFileService(type, file, fileExtension));

					if (searchResults != null) {
						for (FileSearchResult searchResult : searchResults) {
							int makerType = UpgradeProblem.MARKER_ERROR;

							if (Objects.equals("7.0", _tempMethod.getString("deprecatedVersion"))) {
								makerType = UpgradeProblem.MARKER_WARNING;
							}

							problems.add(
								new UpgradeProblem(
									_tempMethod.getString("javadoc"), _tempMethod.getString("javadoc"), fileExtension,
									"", "7.0", file, searchResult.startLine, searchResult.startOffset,
									searchResult.endOffset, _tempMethod.getString("javadoc"),
									searchResult.autoCorrectContext, UpgradeProblem.STATUS_NOT_RESOLVED,
									UpgradeProblem.DEFAULT_MARKER_ID, makerType));
						}
					}
				}
				catch (JSONException jsone) {
				}
			}
		}

		return problems;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFile) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		try {
			JSONArray parameterJSONArray = _tempMethod.getJSONArray("parameters");

			if (parameterJSONArray != null) {
				parameters = new String[parameterJSONArray.length()];

				for (int i = 0; i < parameterJSONArray.length(); i++) {
					parameters[i] = parameterJSONArray.getString(i);
				}
			}

			searchResults.addAll(
				javaFile.findMethodInvocations(
					_tempMethod.getString("className"), null, _tempMethod.getString("methodName"), parameters));

			searchResults.addAll(
				javaFile.findMethodInvocations(
					null, _tempMethod.getString("className"), _tempMethod.getString("methodName"), parameters));
		}
		catch (JSONException jsone) {
		}

		return searchResults;
	}

	private JSONArray[] _getDeprecatedMethods() {
		if (_deprecatedMethods == null) {
			List<JSONArray> deprecatedMethodsList = new ArrayList<>();

			Map<String, String[]> liferayVersionMappingGroup = new LinkedHashMap<>();

			liferayVersionMappingGroup.put(
				"liferay70",
				new String[] {
					"deprecatedMethods61.json", "deprecatedMethods62.json", "deprecatedMethodsNoneVersionFile.json"
				});

			liferayVersionMappingGroup.put("liferay71", new String[] {"deprecatedMethod70.json"});

			liferayVersionMappingGroup.put("liferay72", new String[] {"deprecatedMethod71.json"});

			liferayVersionMappingGroup.put(
				"liferay73", new String[] {"deprecatedMethod72.json", "deprecatedMethodNoneVersionFile.json"});

			String fqn = "/com/liferay/ide/upgrade/problems/core/internal/";

			List<String> jsonFilePathsList = new ArrayList<>();

			liferayVersionMappingGroup.forEach(
				(version, jsonFileArr) -> {
					for (String jsonFileName : jsonFileArr) {
						String path = fqn + version + "/" + jsonFileName;

						jsonFilePathsList.add(path);
					}
				});

			Class<? extends DeprecatedMethodsMigrator> classes = DeprecatedMethodsMigrator.class;

			for (String path : jsonFilePathsList) {
				try {
					String jsonContent = readFully(classes.getResourceAsStream(path));

					deprecatedMethodsList.add(new JSONArray(jsonContent));
				}
				catch (IOException ioe) {
				}
				catch (JSONException jsone) {
				}
			}

			_deprecatedMethods = deprecatedMethodsList.toArray(new JSONArray[0]);
		}

		return _deprecatedMethods;
	}

	private JSONArray[] _deprecatedMethods;
	private JSONObject _tempMethod = null;

}