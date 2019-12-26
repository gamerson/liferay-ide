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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

/**
 * @author Seiphon Wang
 */
public class GradleFileChecker {

	public GradleFileChecker(File file) {
		_file = file;

		if (FileUtil.notExists(file)) {
			return;
		}

		try {
			_gradleDependencyUpdater = new GradleDependencyUpdater(file);
		}
		catch (IOException ioe) {
		}
	}

	public List<Artifact> findArtifactsbyArtifactId(String artifactId) {
		List<Artifact> artifacts = new ArrayList<>();

		List<Artifact> dependencies = _gradleDependencyUpdater.getDependencies("*");

		artifacts = dependencies.stream(
		).filter(
			artifact -> artifactId.equals(artifact.getArtifactId())
		).collect(
			Collectors.toList()
		);

		return artifacts;
	}

	public List<FileSearchResult> findDependencies(String artifactId) {
		List<FileSearchResult> retval = new ArrayList<>();

		List<String> gradleFileContents = new ArrayList<>();

		String gradleFileContentString = "";

		try {
			gradleFileContents = FileUtils.readLines(_file);

			gradleFileContentString = FileUtils.readFileToString(_file, "UTF-8");
		}
		catch (Exception e) {
		}

		List<Artifact> dependencies = _gradleDependencyUpdater.getDependencies("*");

		List<Artifact> artifacts = dependencies.stream(
		).filter(
			artifact -> artifactId.equals(artifact.getArtifactId())
		).collect(
			Collectors.toList()
		);

		for (Artifact artifact : artifacts) {
			int[] lineNumbers = _gradleDependencyUpdater.getDependenceLineNumbers(artifact);

			int startLineNumber = lineNumbers[0];
			int endLineNumber = lineNumbers[1];

			String startLineContent = gradleFileContents.get(startLineNumber - 1);

			String endLineContent = gradleFileContents.get(endLineNumber - 1);

			int originLength = startLineContent.length();

			startLineContent = startLineContent.trim();

			int startPos = gradleFileContentString.indexOf(startLineContent) + originLength - startLineContent.length();

			int endPos = gradleFileContentString.indexOf(endLineContent) + endLineContent.length();

			FileSearchResult result = new FileSearchResult(
				_file, startPos, endPos, startLineNumber, endLineNumber, true);

			result.autoCorrectContext = "dependency:artifactId";

			retval.add(result);
		}

		return retval;
	}

	private File _file;
	private GradleDependencyUpdater _gradleDependencyUpdater;

}