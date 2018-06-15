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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.gogo.GogoTelnetClient;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Terry Jia
 */
public class UninstallTaskAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			Object elem = elems[0];

			if (!(elem instanceof IProject)) {
				return;
			}

			IProject project = (IProject)elem;

			IFile bndFile = project.getFile("bnd.bnd");

			if (FileUtil.notExists(bndFile)) {
				return;
			}

			Properties properties = new Properties();

			IPath path = bndFile.getLocation();

			try (InputStream in = Files.newInputStream(Paths.get(path.toOSString()))) {
				properties.load(in);

				String bsn = properties.getProperty("Bundle-SymbolicName");

				GogoTelnetClient client = new GogoTelnetClient("localhost", 11311);

				String cmd = "uninstall " + bsn;

				client.send(cmd);

				client.close();
			}
			catch (IOException ioe) {
			}
		}
	}

}