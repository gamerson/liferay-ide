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

package com.liferay.ide.upgrade.plan.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Terry Jia
 */
public class OutlineFetcher {

	public static void main(String[] args) {
		try {
			URL url = new URL(_OUTLINE_URL);

			Document document = Jsoup.parse(url, 10000);

			String protocol = url.getProtocol();

			String authority = url.getAuthority();

			String rootUrl = protocol + "://" + authority;

			String fileName = _OUTLINE_URL.substring(rootUrl.length());

			File outlineDir = new File(_OUTPUT, _OUTLINE_NAME);

			outlineDir.mkdirs();

			File file = new File(outlineDir, fileName);

			File parentFile = file.getParentFile();

			parentFile.mkdirs();

			file.createNewFile();

			String html = document.toString();

			html = html.replaceAll("’", "'");

			try (FileWriter fileWriter = new FileWriter(file)) {
				fileWriter.write(html);

				System.out.println("Fetching done, see " + file);
			}
		}
		catch (MalformedURLException murle) {
		}
		catch (IOException ioe) {
		}
	}

	private static final String _OUTLINE_NAME = "upgrading-code-to-product-ver";

	private static final String _OUTLINE_URL =
		"https://portal.liferay.dev/docs/7-2/tutorials/-/knowledge_base/t/upgrading-code-to-product-ver";

	// should use your ide path

	private static final File _OUTPUT = new File(
		"xxx/liferay-ide/tools/plugins/com.liferay.ide.upgrade.plan.core/resources");

}