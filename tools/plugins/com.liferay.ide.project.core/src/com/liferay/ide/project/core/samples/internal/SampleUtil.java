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

package com.liferay.ide.project.core.samples.internal;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Terry Jia
 */
public class SampleUtil {

	public static Path getSamplesCachePath() throws IOException {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path samplesCachePath = userHomePath.resolve(".blade/cache/samples");

		if (!Files.exists(samplesCachePath)) {
			Files.createDirectories(samplesCachePath);
		}

		return samplesCachePath;
	}

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

}