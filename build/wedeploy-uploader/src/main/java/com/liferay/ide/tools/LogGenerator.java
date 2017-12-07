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

package com.liferay.ide.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Terry Jia
 */
public class LogGenerator {

	private static final String _serverIp = "http://192.168.130.85:9090/";
	private static final String _checkerName = "LIFERAY_IDE_UI_TESTS-Pull-Request-Tester";
	private static final String _parent = "/home/build/wedeploy/liferayide/";
	//private static final String _parent = "/var/lib/jenkins/userContent/liferayide/";
	private static final int _max = 20;

	public static void main(String[] args) {
		if (args.length == 0 || args[0] == null || args[0].equals("")) {
			System.out.println("Must have build number!");

			return;
		}

		int buildNumber = -1;

		try {
			buildNumber = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			System.out.println("The build number must be integer!");

			return;
		}

		if (buildNumber < 0) {
			System.out.println("The build number must greater than 0!");

			return;
		}

		File uiTestingReportsFolder = new File(_parent, "ui-testing-reports");

		uiTestingReportsFolder.mkdirs();

		File[] files = uiTestingReportsFolder.listFiles();

		if (files != null && files.length > 0) {
			Arrays.sort(files, new Comparator<File>() {

				public int compare(File file1, File file2) {
					return file1.lastModified() > file2.lastModified() ? -1 : 1;
				}
			});
		}

		if (files != null && files.length > _max) {
			int i = files.length - 1;

			while (i > _max) {
				try {
					FileUtils.deleteDirectory(files[i]);
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				i--;
			}
		}

		File newBuildLogFolder = new File(uiTestingReportsFolder, String.valueOf(buildNumber));

		newBuildLogFolder.mkdirs();

		StringBuilder sb = new StringBuilder();

		sb.append(_serverIp);
		sb.append("view/checker/job/");
		sb.append(_checkerName);
		sb.append("/");
		sb.append(buildNumber);
		sb.append("/consoleText");

		HttpGet request = new HttpGet(sb.toString());

		HttpClient client = HttpClientBuilder.create().build();

		String log = "";

		try {
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("response status is not 200!");

				return;
			}

			HttpEntity entity = response.getEntity();

			log = EntityUtils.toString(entity, "utf-8");
		}
		catch (Exception e) {
			System.out.println("Unable to get log from server!");

			return;
		}

		if (log.equals("")) {
			System.out.println("log is empty!");

			return;
		}

		File logFile = new File(newBuildLogFolder, "checker.log");

		try {
			logFile.createNewFile();
		}
		catch (IOException e1) {
			System.out.println("create log file error!");

			e1.printStackTrace();
		}

		try (FileWriter writer = new FileWriter(logFile)){
			writer.write(log);
		}
		catch (IOException e) {
			System.out.println("write log file error!");

			e.printStackTrace();

			return;
		}
	}

}
