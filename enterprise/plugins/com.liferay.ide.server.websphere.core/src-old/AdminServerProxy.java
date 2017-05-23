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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.scripting.core.GroovyScriptingSupport;
import com.liferay.ide.scripting.core.ScriptingCore;
import com.liferay.ide.eclipse.server.ee.websphere.core.IWebsphereServer;
import com.liferay.ide.eclipse.server.ee.websphere.core.WebsphereCore;
import com.liferay.ide.eclipse.server.ee.websphere.util.SocketUtil;
import com.liferay.ide.eclipse.server.ee.websphere.util.WebsphereUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class AdminServerProxy {

	public static final IEclipsePreferences _defaultPrefs = new DefaultScope().getNode(WebsphereCore.PLUGIN_ID);

	public static final long LOG_QUERY_RANGE = _defaultPrefs.getLong("log.query.range", 51200);

	public final static long OUTPUT_MONITOR_DELAY = _defaultPrefs.getLong("output.monitor.delay", 1000);

	protected String adminConsolePort = null;

	protected URL[] classpath;

	protected IServer server;

	protected IWebsphereAdminService websphereAdminService;

	AdminServerProxy(IServer server, URL[] classpath) {
		this.server = server;
		this.classpath = classpath;
	}

	public boolean canConnect() {
		return SocketUtil.canConnect(server.getHost(), WebsphereUtil.getWebsphereServer(getServer()).getSOAPPort()).isOK();
	}

	public String getConsolePort(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		if (adminConsolePort == null) {
			try {
				Object retval = evaluateScript("admin", "getFileTransferProperties.groovy", monitor);

				if (retval instanceof Properties) {
					Properties props = (Properties) retval;

					adminConsolePort = props.get("port").toString();
				}
			}
			catch (Exception e) {
				WebsphereCore.logError("Could not get console port.", e);
			}

			return adminConsolePort;
		}

		return adminConsolePort;
	}



	@SuppressWarnings("rawtypes")
	public Map getDebugOptions(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			Object retval = evaluateScript("admin", "getDebugOptions.groovy", monitor);

			if (retval instanceof Map) {
				return (Map) retval;
			}
		}
		catch (Exception e) {
			WebsphereCore.logError("error getting debug options.", e);
		}

		return null;
	}

	public String getHost() {
		return server.getHost();
	}

	public IServer getServer() {
		return server;
	}

	public int getServerState(int currentServerState, IProgressMonitor monitor) {
		try {
			if (currentServerState == IServer.STATE_STOPPED) {
				monitor.beginTask("Updating server state for " + server.getName(), 100);
			}

			Object retval = evaluateScript("admin", "getServerState.groovy", monitor);

			if (retval == null) {
				retval = "STOPPED";
			}

			String serverState = retval.toString();

			if ("STARTED".equals(serverState)) {
				return IServer.STATE_STARTED;
			}
			else if ("STOPPED".equals(serverState)) {
				return IServer.STATE_STOPPED;
			}
		}
		catch (Exception e) {
			WebsphereCore.logError("Could not get server state.", e);
			return IServer.STATE_UNKNOWN;
		}

		return IServer.STATE_UNKNOWN;
	}

	public IWebsphereAdminService getWebsphereAdminService() {
		return getWebsphereAdminService(false);
	}

	@SuppressWarnings({
		"rawtypes", "unchecked"
	})
	public IWebsphereAdminService getWebsphereAdminService(boolean create) {
		if (create || websphereAdminService == null) {
			try {

				File groovyFile =
					new File(FileLocator.toFileURL(
						WebsphereCore.getPluginEntry("/scripts/admin/WebsphereAdminService.groovy")).getFile());

				final Object scriptObject =
					ScriptingCore.getGroovyScriptingSupport().createScriptFromFile(groovyFile, classpath);

				websphereAdminService =
					(IWebsphereAdminService) Proxy.newProxyInstance(
						scriptObject.getClass().getClassLoader(), new Class<?>[] {
							IWebsphereAdminService.class
						}, new WebsphereAdminServiceProxy(scriptObject, classpath));

				Map options = new HashMap();
				options.put("connectorType", "SOAP");
				options.put("connectorHost", getServer().getHost());
				options.put("connectorPort", WebsphereUtil.getWebsphereServer(getServer()).getSOAPPort());

				websphereAdminService.setOptions(options);
			}
			catch (Exception e) {
				WebsphereCore.logError("Could not get websphere admin service", e);
			}
		}

		return websphereAdminService;
	}

	public IStatus isAlive(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			Object retval = evaluateScript("admin", "isAlive.groovy", monitor);

			if (Boolean.parseBoolean(retval.toString())) {
				return Status.OK_STATUS;
			}
			else {
				return WebsphereCore.createErrorStatus("WebSphere admin server is not available.");
			}
		}
		catch (Exception ex) {
			return WebsphereCore.createErrorStatus("Could not connect to websphere admin server.", ex);
		}
	}

	public IStatus preConnectXXX() {
		IStatus retval = null;

		try {
			String host = getServer().getHost();
			IWebsphereServer websphereServer = getWebsphereServer();
			String connType = websphereServer.getConnectionType();
			String port = null;

			if (IWebsphereServer.CONNECTION_TYPE_SOAP.equals(connType)) {
				port = websphereServer.getSOAPPort();
			}
			else if (IWebsphereServer.CONNECTION_TYPE_RMI.equals(connType)) {
				port = websphereServer.getRMIPort();
			}

			retval = SocketUtil.canConnect(host, port);
		}
		catch (Exception e) {
			retval = WebsphereCore.createErrorStatus("Could not make connection to server.", e);
		}

		return retval;
	}

	/*
	 * Evaulate groovy script with the admin client jar in the classpath
	 */
	protected Object evaluateScript(String scriptPrefix, String pluginScriptPath, IProgressMonitor monitor)
		throws CoreException {

		try {
			GroovyScriptingSupport groovyScripting = ScriptingCore.getGroovyScriptingSupport();

			InputStream getObjects =
				this.getClass().getResourceAsStream("/scripts/" + scriptPrefix + "/_getObjects.groovy");

			InputStream script =
				this.getClass().getResourceAsStream("/scripts/" + scriptPrefix + "/" + pluginScriptPath);

			String scriptContents = FileUtil.readContents(getObjects) + FileUtil.readContents(script);

			Map<String, Object> variableMap = new HashMap<String, Object>();

			variableMap.put("connectorHost", server.getHost());
			variableMap.put("connectorPort", WebsphereUtil.getWebsphereServer(server).getSOAPPort());
			variableMap.put("connectorType", "SOAP");
			variableMap.put("monitor", monitor);

			return groovyScripting.evaluateScriptText(scriptContents, variableMap, classpath);
		}
		catch (Exception e) {
			WebsphereCore.logError("Unable to evaluate admin client script", e);
			throw new CoreException(WebsphereCore.createErrorStatus("Unable to evaluate admin client script."));
		}
	}

	protected IWebsphereServer getWebsphereServer() {
		return WebsphereUtil.getWebsphereServer(getServer());
	}

}
