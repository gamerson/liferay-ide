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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

			_writeFile(fileName, document.toString());

			Elements roots = document.select(".root");

			Element root = roots.get(0);

			_loopChildren(root);
		}
		catch (MalformedURLException murle) {
		}
		catch (IOException ioe) {
		}
	}

	private static void _loopChildren(Element olElement) throws IOException {
		Elements children = olElement.children();

		for (Element child : children) {
			String html = child.toString();

			if (html.startsWith("<li")) {
				Elements pTags = child.getElementsByTag("p");

				Element titleElement = pTags.get(0);

				Elements aTags = titleElement.getElementsByTag("a");

				if (aTags.size() > 0) {
					Element aTag = aTags.get(0);

					String href = aTag.attr("href");

					URL u = new URL(_OUTLINE_URL);

					String protocol = u.getProtocol();

					String authority = u.getAuthority();

					String rootUrl = protocol + "://" + authority;

					URL u1 = new URL(rootUrl + href);

					Document document = Jsoup.parse(u1, 10000);

					_writeFile(href, document.toString());
				}

				Element titleNextElement = titleElement.nextElementSibling();

				if (titleNextElement != null) {
					if ("p".equals(titleNextElement.nodeName())) {
						titleNextElement = titleNextElement.nextElementSibling();
					}

					if ((titleNextElement != null) && "ol".equals(titleNextElement.nodeName())) {
						_loopChildren(titleNextElement);
					}
				}
			}
		}
	}

	private static void _writeFile(String fileName, String contents) throws IOException {
		File outlineDir = new File(_OUTPUT, _OUTLINE_NAME);

		outlineDir.mkdirs();

		File file = new File(outlineDir, fileName);

		File parentFile = file.getParentFile();

		parentFile.mkdirs();

		file.createNewFile();

		contents = contents.replaceAll("’", "'");

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(contents);

			System.out.println("Fetching done, see " + file);
		}
	}

	private static final String _OUTLINE_NAME = "upgrading-to-product-ver";

	private static final String _OUTLINE_URL =
		"https://portal.liferay.dev/docs/7-2/deploy/-/knowledge_base/d/upgrading-to-product-ver";

	// should use your ide path

	private static final File _OUTPUT = new File(
		"/Users/terryjia/work/github/liferay/liferay-ide/tools/plugins/com.liferay.ide.upgrade.plan.core/resources");

}