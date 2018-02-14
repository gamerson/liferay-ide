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

package com.liferay.ide.ui.liferay.base;

import com.liferay.ide.ui.liferay.util.BundleInfo;
import com.liferay.ide.ui.liferay.util.FileUtil;
import com.liferay.ide.ui.liferay.util.SdkInfo;
import com.liferay.ide.ui.liferay.util.ZipUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ImportLiferayWorkspaceProjectSupport extends ImportProjectSupport {

	public ImportLiferayWorkspaceProjectSupport(SWTWorkbenchBot bot, String name) {
		super(bot, name);
	}

	public void prepareSdk() {
		File sdkZipFile = envAction.getBundleFile(_sdk.getSdkZip());

		File sdkDir = new File(getPath(), "plugins-sdk");

		sdkDir.mkdirs();

		try {
			ZipUtil.unzip(sdkZipFile, _sdk.getSdkDir(), sdkDir, new NullProgressMonitor());
		}
		catch (Exception e) {
		}

		prepareServer();

		if (envAction.internal()) {
			IPath bundlesPath = envAction.getBundlesPath();

			IPath source = bundlesPath.append("internal").append("ivy-settings.xml");

			File dest = new File(sdkDir, "ivy-settings.xml");

			FileUtil.copyFile(source.toFile(), dest);
		}
	}

	public void prepareServer() {
		File serverZip = envAction.getBundleFile(_server.getBundleZip());

		File serverDir = new File(getPath(), "bundles");

		serverDir.mkdirs();

		try {
			ZipUtil.unzip(serverZip, _server.getBundleDir(), serverDir, new NullProgressMonitor());
		}
		catch (IOException ioe) {
		}
	}

	private SdkInfo _sdk = envAction.getSdkInfos()[0];
	private BundleInfo _server = envAction.getBundleInfos()[0];

}