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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import org.junit.Assert;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BaseTests {

	protected static void failTest(Exception e) {
		StringWriter s = new StringWriter();

		e.printStackTrace(new PrintWriter(s));

		Assert.fail(s.toString());
	}

	protected static IProject project(String name) {
		return workspaceRoot().getProject(name);
	}

	protected static IWorkspace workspace() {
		return ResourcesPlugin.getWorkspace();
	}

	protected static IWorkspaceRoot workspaceRoot() {
		return workspace().getRoot();
	}

	protected IFile createFile(IProject project, String path) throws Exception {
		return createFile(project, path, new byte[0]);
	}

	protected IFile createFile(IProject project, String path, byte[] content) throws Exception {
		return createFile(project, path, new ByteArrayInputStream(content));
	}

	protected IFile createFile(IProject project, String path, InputStream content) throws Exception {
		IFile file = project.getFile(path);

		IContainer parent = file.getParent();

		if (parent instanceof IFolder) {
			createFolder((IFolder)parent);
		}

		file.create(content, true, null);

		return file;
	}

	protected void createFolder(IFolder folder) throws Exception {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();

			if (parent instanceof IFolder) {
				createFolder((IFolder)parent);
			}

			folder.create(true, true, null);
		}
	}

	protected IFolder createFolder(IProject project, String path) throws Exception {
		IFolder folder = project.getFolder(path);

		createFolder(folder);

		return folder;
	}

	protected IProject createProject(String name) throws Exception {
		Class<?> clazz = getClass();

		String n = clazz.getName();

		if (name != null) {
			n = n + "." + name;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProject p = workspace.getRoot().getProject(n);

		p.create(null);

		p.open(null);

		return p;
	}

	protected File createTempFile(String fileDir, String fileName) {
		try {
			IPath path = LiferayCore.getDefault().getStateLocation();

			File tempFile = path.append(fileName).toFile();

			Class<?> clazz = getClass();

			FileUtil.writeFileFromStream(tempFile, clazz.getResourceAsStream(fileDir + "/" + fileName));

			if (tempFile.exists()) {
				return tempFile;
			}
		}
		catch (IOException ioe) {
		}

		return null;
	}

	protected void deleteProject(String name) throws Exception {
		Class<?> clazz = getClass();

		String n = clazz.getName();

		if (name != null) {
			n = n + "." + name;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProject p = workspace.getRoot().getProject(n);

		if (p.exists()) {
			p.delete(true, null);
		}
	}

	protected String stripCarriageReturns(String value) {
		return value.replaceAll("\r", "");
	}

}