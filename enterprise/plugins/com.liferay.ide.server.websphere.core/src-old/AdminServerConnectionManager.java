/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.ee.websphere.admin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;

/**
 * @author Greg Amerson
 */
public class AdminServerConnectionManager {

	private static AdminServerConnectionManager instance = new AdminServerConnectionManager();

	private static Map<String, URL[]> scriptClasspaths = new HashMap<String, URL[]>();

 	public static AdminServerConnectionManager getInstance() {
		return instance;
	}

	private AdminServerConnectionManager() {
	}

	public AdminServerProxy getAdminServerConnection(IServerWorkingCopy server) {
		return getAdminServerProxy(server.getOriginal());
	}

	public synchronized AdminServerProxy getAdminServerProxy(IServer server) {
		// check for an existing classloader for this URL
		URL[] scriptClasspath = scriptClasspaths.get(server.getId());

		if (scriptClasspath == null) {
			List<URL> scriptUrlList = new ArrayList<URL>();

			File runtimesFolder = server.getRuntime().getLocation().append("runtimes").toFile();

			String[] runtimes = runtimesFolder.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					// return name.contains("com.ibm.ws.admin.client_") && name.endsWith(".jar");
					return (name.contains("com.ibm.ws.admin.client") && name.endsWith(".jar")) ||
						(name.contains("com.ibm.ws.admin.client") && name.endsWith(".jar"));
				}

			});

			for (String runtime : runtimes) {
				File runtimeJar = new File(runtimesFolder, runtime);

				if (runtimeJar.exists()) {
					try {
						scriptUrlList.add(runtimeJar.toURL());
					}
					catch (MalformedURLException e) {
					}
				}
			}

			File libFolder = server.getRuntime().getLocation().append("lib").toFile();

			String[] libs = libFolder.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					// return name.contains("com.ibm.ws.admin.client_") && name.endsWith(".jar");
					return (name.contains("j2ee") && name.endsWith(".jar")) ||
						(name.contains("wsadmin") && name.endsWith(".jar"));
				}

			});

			for (String lib : libs) {
				File libJar = new File(libFolder, lib);

				if (libJar.exists()) {
					try {
						scriptUrlList.add(libJar.toURL());
					}
					catch (MalformedURLException e) {
					}
				}
			}

			File pluginsFolder = server.getRuntime().getLocation().append("plugins").toFile();

			String[] plugins = pluginsFolder.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return (name.contains("com.ibm.ws.runtime.client") && name.endsWith(".jar"));
				}

			});

			for (String plugin : plugins) {
				File pluginJar = new File(pluginsFolder, plugin);

				if (pluginJar.exists()) {
					try {
						scriptUrlList.add(pluginJar.toURL());
					}
					catch (MalformedURLException e) {
					}
				}
			}

			File javaFolder = server.getRuntime().getLocation().append("java/jre/lib").toFile();

			String[] javaLibs = javaFolder.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return (name.contains("ibmjceprovider") && name.endsWith(".jar")) ||
						(name.contains("ibmcertpathprovider") && name.endsWith(".jar")) ||
						(name.contains("ibmjsseprovider") && name.endsWith(".jar")) ||
						(name.contains("ibmjgssprovider") && name.endsWith(".jar")) ||
						(name.contains("ibmcfw") && name.endsWith(".jar")) ||
						(name.contains("ibmjgssprovider") && name.endsWith(".jar"));
				}

			});

			for (String javaLib : javaLibs) {
				File javaJar = new File(javaFolder, javaLib);

				if (javaJar.exists()) {
					try {
						scriptUrlList.add(javaJar.toURL());
					}
					catch (MalformedURLException e) {
					}
				}
			}

			// String lastLib = "java/jre/lib/ext/ibmjceprovider.jar";

			scriptClasspath = scriptUrlList.toArray(new URL[0]);

			scriptClasspaths.put(server.getId(), scriptClasspath);
		}

		return new AdminServerProxy(server, scriptClasspath);
	}

}
