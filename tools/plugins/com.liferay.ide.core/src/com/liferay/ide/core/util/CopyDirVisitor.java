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

package com.liferay.ide.core.util;

import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Gregory Amerson
 */
public class CopyDirVisitor extends SimpleFileVisitor<Path> {

	public CopyDirVisitor(Path fromPath, Path toPath, CopyOption copyOption) {
		_fromPath = fromPath;
		_toPath = toPath;
		_copyOption = copyOption;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Path targetPath = _toPath.resolve(_fromPath.relativize(dir));

		if (!Files.exists(targetPath)) {
			Files.createDirectory(targetPath);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.copy(file, _toPath.resolve(_fromPath.relativize(file)), _copyOption);

		return FileVisitResult.CONTINUE;
	}

	private final CopyOption _copyOption;
	private final Path _fromPath;
	private final Path _toPath;

}