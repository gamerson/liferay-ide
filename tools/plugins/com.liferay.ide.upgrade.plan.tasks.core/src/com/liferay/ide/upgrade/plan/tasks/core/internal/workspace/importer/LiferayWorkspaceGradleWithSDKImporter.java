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

package com.liferay.ide.upgrade.plan.tasks.core.internal.workspace.importer;

import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceGradleWithSDKImporter extends LiferayWorkspaceGradleImporter {

	public LiferayWorkspaceGradleWithSDKImporter(IPath location) {
		super(location);
	}

	@Override
	public void doBefore(IProgressMonitor monitor) {
		StringBuilder sb = new StringBuilder();

		sb.append("init ");
		sb.append("--base \"");
		sb.append(location.toOSString());
		sb.append("\" ");
		sb.append("-u");

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (BladeCLIException bclie) {
			bclie.printStackTrace();
		}
	}

}