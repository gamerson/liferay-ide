import com.ibm.websphere.management.AdminClient
import com.ibm.websphere.management.AdminClientFactory
import com.ibm.websphere.management.application.AppConstants
import com.ibm.websphere.management.application.AppManagement
import com.ibm.websphere.management.application.AppManagementProxy
import com.ibm.websphere.management.application.AppNotification
import com.ibm.websphere.management.application.client.AppDeploymentController
import com.ibm.websphere.management.application.client.AppDeploymentTask
import com.ibm.websphere.management.filetransfer.FileTransferConfig
import com.ibm.ws.ssl.core.Constants

import java.io.File
import java.net.URLClassLoader
import java.util.Map
import java.util.Set

import javax.management.Notification
import javax.management.NotificationFilterSupport
import javax.management.NotificationListener
import javax.management.ObjectName
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocketFactory
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathExpression;
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder

import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.codehaus.groovy.runtime.ReflectionMethodInvoker
import org.w3c.dom.NodeList
import org.codehaus.groovy.runtime.ReflectionMethodInvoker


/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereAdminService {

	AdminClient adminClient
	AppManagement appManagement
	def appManagementMBean
	boolean debug = false
	def cellName
	def configServiceMBean
	Map connectOptions
	def connectorHost
	def connectorPort
	def connectorType
	def fileTransferMBean
	def serverName
	def session
	def serverMBean
	URLClassLoader urlClassLoader
    def applicationMBean

	public WebsphereAdminService() {
	}

	AdminClient createAdminClient() {
		Properties connectProps = new Properties()
        String profileLocation = connectOptions.get("websphere-profile-location")
        String connectPort = connectOptions.get( "\"" + profileLocation + "connectorPort" + "\"" )
		connectProps.setProperty(AdminClient.CONNECTOR_TYPE, connectOptions.get( "\"" + profileLocation + "connectorType" + "\"" ))
		connectProps.setProperty(AdminClient.CONNECTOR_HOST, connectOptions.get( "\"" + profileLocation + "connectorHost" + "\"" ))
		connectProps.setProperty(AdminClient.CONNECTOR_PORT, connectOptions.get( "\"" + profileLocation + "connectorPort" + "\"" ))
		connectProps.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true")
		connectProps.setProperty(Constants.SYSTEM_SSLPROP_TRUST_STORE, "ssl_trustStore")
		connectProps.setProperty(Constants.SYSTEM_SSLPROP_KEY_STORE, "ssl_keyStore")
		connectProps.setProperty(Constants.SYSTEM_SSLPROP_TRUST_STORE_PASSWORD, "ssl_trustStorePassword")
		connectProps.setProperty(Constants.SYSTEM_SSLPROP_KEY_STORE_PASSWORD, "ssl_keyStorePassword")
        connectProps.setProperty(AdminClient.CONNECTOR_SOAP_CONFIG, connectOptions.get("\"" + profileLocation + "com.ibm.SOAP.ConfigURL" + "\"" ))

		boolean securityEnabled = connectOptions.get( "\"" + profileLocation + "securityEnabled" + "\"" )
		connectProps.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, (String)securityEnabled)

		if (securityEnabled) {
			SSLSocketFactory.getDefault();
			connectProps.setProperty(AdminClient.USERNAME, connectOptions.get( "\"" + profileLocation + "websphere-security-userid" + "\"" ))
			connectProps.setProperty(AdminClient.PASSWORD, connectOptions.get( "\"" + profileLocation + "websphere-security-passrowd" + "\"" ));
		}

		try {
			def newAdminClient = AdminClientFactory.createAdminClient(connectProps);

			def session = newAdminClient.isAlive()

			if (session != null) {
				return newAdminClient
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
			// look for ssl exception
			while (e != null) {
				if (e.getMessage() != null && e.getMessage().contains("SSLHandshakeException")) {
					throw new SSLHandshakeException()
				}

				e = e.getCause();
			}
		}

		return null
	}

	AdminClient getAdminClient() {
		if (adminClient == null /*|| adminClient.isAlive() == null*/) {
			adminClient = createAdminClient()
		}

		return adminClient
	}

    def getApplicationMBean(String appName) {
        if (applicationMBean == null) {
            String query = "WebSphere:type=J2EEApplication,name=" + appName + ",*"

            ObjectName queryName = new ObjectName(query)

            Set s = adminClient.queryNames(queryName, null)

            if (!s.isEmpty()) {
                applicationMBean = (ObjectName) s.iterator().next()
            }
            else {
                System.out.println("Application MBean for appName " + appName + " was not found.");
                return null
            }
        }

        return applicationMBean
    }
      
	AppManagement getAppManagement() {
		if (appManagement == null) {
			appManagement = AppManagementProxy.getJMXProxyForClient(getAdminClient())
		}

		return appManagement
	}

	def getAppManagementMBean() {
		if (appManagementMBean == null) {
			ObjectName queryName = new ObjectName("WebSphere:type=AppManagement,*")

			Iterator iter = getAdminClient().queryNames(queryName, null).iterator();

			appManagementMBean = (ObjectName) iter.next();
		}

		return appManagementMBean
	}

	def getCellName() {
		if (cellName == null) {
			cellName = getAdminClient().getAttribute(getServerMBean(), "cellName")
		}

		return cellName
	}

	def getConfigServiceAttribute(def session, def parent, def attr) {
		String opName = "getAttribute";

		String[] signature = [
			"com.ibm.websphere.management.Session",
			"javax.management.ObjectName",
			"java.lang.String"
		]

		Object[] params = [session, parent, attr]

		return getAdminClient().invoke(getConfigServiceMBean(), opName, params, signature);
	}

	def getConfigServiceAttributes(def session, def parent) {
		String opName = "getAttributes";

		String[] signature = [
			"com.ibm.websphere.management.Session",
			"javax.management.ObjectName"
		]

		Object[] params = [session, parent]

		return getAdminClient().invoke(getConfigServiceMBean(), opName, params, signature);
	}

	def getConfigServiceMBean() {
		if (configServiceMBean == null) {
			String query = "WebSphere:type=ConfigService,process=" + getServerName() + ",*"

			ObjectName queryName = new ObjectName(query)

			Set s = adminClient.queryNames(queryName, null)

			if (!s.isEmpty()) {
				configServiceMBean = (ObjectName) s.iterator().next()
			}
			else {
				System.out.println("ConfigService MBean for servername " + serverName + "was not found.");
				return null
			}
		}

		return configServiceMBean
	}

	def getConsolePort() {
		if (getConnectOptions().get("securityEnabled")) {
			return getFileTransferProperties().get(FileTransferConfig.SECURE_PORT_KEY)
		}
		else {
			return getFileTransferProperties().get(FileTransferConfig.PORT_KEY)
		}
	}

	String getContextUrl(String appName) {
        def contextUrl
        
        def applicationMBeanSet = getApplicationMBean(appName)
        def applicationMBean = (ObjectName)applicationMBeanSet.iterator().next()
        def applicationXML = (String)adminClient.getAttribute(applicationMBean, "deploymentDescriptor")

        def domFactory = DocumentBuilderFactory.newInstance()
        domFactory.setNamespaceAware( false )
        def builder = domFactory.newDocumentBuilder()
        def doc = builder.parse( new InputSource(new StringReader(applicationXML)) )

        def factory = XPathFactory.newInstance()
        def xpath = factory.newXPath()
        def expr = xpath.compile( "//module/web/context-root/text()" )

        def result = expr.evaluate( doc, XPathConstants.NODESET )
        def nodes = (NodeList) result
        for( int i = 0; i < nodes.getLength(); i++ )
        {
            contextUrl = nodes.item( i ).getNodeValue()
            break;
        }

		return contextUrl
	}

	Map getDebugOptions() {
		def session = getAdminClient().isAlive()

		def serverTypeConfigObjectName = new ObjectName("Websphere:_Websphere_Config_Data_Type=Server")

		Object[] queryConfigParams = [
			session,
			null,
			serverTypeConfigObjectName,
			null
		]

		def configObjects = queryConfigObjects(queryConfigParams)

		serverTypeConfigObjectName = configObjects[0]

		println "configObjectName: " + serverTypeConfigObjectName

		def javaVMConfigObjectName = new ObjectName("Websphere:_Websphere_Config_Data_Type=JavaVirtualMachine")

		queryConfigParams = [
			session,
			serverTypeConfigObjectName,
			javaVMConfigObjectName,
			null
		]

		def javaQueryConfigObjects = queryConfigObjects(queryConfigParams)

		javaVMConfigObjectName = javaQueryConfigObjects[0];

		def debugMode = getConfigServiceAttribute(session, javaVMConfigObjectName, "debugMode")

		def debugArgs = getConfigServiceAttribute(session, javaVMConfigObjectName, "debugArgs")

		return ["debugMode":debugMode,"debugArgs":debugArgs]
	}

	Properties getFileTransferProperties() {
		Set s = getAdminClient().queryNames(new ObjectName("WebSphere:type=FileTransferServer,process=" + getServerName() + ",*"), null)

		if (!s.isEmpty()) {
			fileTransferMBean = (ObjectName) s.iterator().next()
			if (debug) println "Got FileTransfer mbean for servername: " + serverName
		}
		else {
			if (debug) System.out.println("FileTransfer MBean for servername " + serverName + "was not found.");
			return null
		}

		def fileTransferImpl = getAdminClient().invoke(fileTransferMBean, "getServerConfig", null, null)

		def fileTransferProps = fileTransferImpl.getProperties()
		return fileTransferProps
	}

	def getHttpPort() {
		def session = getAdminClient().isAlive()

		def serverIndexConfigObjectName = new ObjectName("Websphere:_Websphere_Config_Data_Type=ServerIndex")

		Object[] queryConfigParams = [
			session,
			null,
			serverIndexConfigObjectName,
			null
		]

		def configObjects = queryConfigObjects(queryConfigParams)

		serverIndexConfigObjectName = configObjects[0]

		def entries = getConfigServiceAttribute(session, serverIndexConfigObjectName, "serverEntries")

		def port

		def findPort = { serverName, serverEntries ->
			serverEntries.each { serverEntry ->
				serverEntry.each { attr ->
					if (attr.getName() == "serverName" && attr.getValue() == serverName) {
						serverEntry.each { attr2 ->
							if (attr2.getName() == "specialEndpoints") {
								attr2.getValue().each { endpoint ->
									endpoint.each { attr3 ->
										if (attr3.getName() == "endPointName" && attr3.getValue() == "WC_defaulthost") {
											endpoint.each { attr4 ->
												if (attr4.getName() == "endPoint") {
													attr4.getValue().each { attr5 ->
														if (attr5.getName() == "port") {
															port = attr5.getValue()
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		findPort(getServerName(), entries)

		return port
	}

	def getServerMBean() {
		if (serverMBean == null) {
			ObjectName queryName = new ObjectName("WebSphere:*,type=Server,j2eeType=J2EEServer")

			Set s = getAdminClient().queryNames(queryName, null)

			if (!s.isEmpty()) {
				serverMBean = (ObjectName)s.iterator().next();
			}
		}

		return serverMBean
	}

	def getServerName() {
		if (serverName == null) {
			serverName = getAdminClient().getAttribute(getServerMBean(), "name")
		}

		return serverName
	}

	String getServerState() {
		AdminClient client = getAdminClient()

		if (client != null) {
			return getAdminClient().getAttribute(getServerMBean(), "state")
		}
		else {
			return null
		}
	}

	String getVersionString() {
		ObjectName bean = getServerMBean();

		String version = bean.getKeyProperty("version");

		return version;
	}
    
    String getHost()
    {
        return connectOptions.get("connectorHost")
    }

	boolean isAlive() {
		AdminClient client = getAdminClient()

		if (client != null && client.isAlive() != null) {
			if (debug) println client.isAlive()
			return true
		}
		else {
			return false
		}
	}

	boolean isAppInstalled(String appName) {
		return getAppManagement().checkIfAppExists(appName, null, null)
	}

	boolean isAppStarted(String appName) {
		String query = "WebSphere:*,type=Application,name=" + appName

		ObjectName queryName = new ObjectName(query)

		Set s = getAdminClient().queryNames(queryName, null)

		ObjectName appObjectName

		if (!s.isEmpty()) {
			appObjectName = (ObjectName) s.iterator().next()
		}

		return appObjectName != null && appObjectName.toString().length() > 0
	}

	Object installApplication(File scriptFile, String earPath, String appName, Object monitor) {
		Properties defaultBnd = new Properties()
		defaultBnd.put(AppConstants.APPDEPL_DFLTBNDG_VHOST, "default_host")

		def prefs = new Hashtable()
		prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault())
		prefs.put(AppConstants.APPDEPL_DFLTBNDG, defaultBnd)

		ReflectionMethodInvoker.invoke(monitor, "subTask", "Processing archive " + appName + "...")

		AppDeploymentController controller = AppDeploymentController.readArchive(earPath, prefs)
		AppDeploymentTask task = controller.getFirstTask()

		//JSPReloadForWebModHelper helper = new JSPReloadForWebModHelper()
		//AppDeploymentTask newTask = helper.createTask(controller, "taskName")


		while (task != null) {
			String taskName = task.getName()
			String[][] taskData = task.getTaskData()

			if (AppConstants.AppDeploymentOptionsTask.equals(taskName)) {
				for (int i = 0; i < taskData[0].length; i++) {
					String taskDataName = taskData[0][i];

					if (AppConstants.APPDEPL_APPNAME.equals(taskDataName)) {
						appName = taskData[1][i];
					}
					else if (AppConstants.APPDEPL_RELOADENABLED.equals(taskDataName)) {
						taskData[1][i] = AppConstants.YES_KEY;
					}
					else if (AppConstants.APPDEPL_RELOADINTERVAL.equals(taskDataName)) {
						taskData[1][i] = "3";
					}
				}
			}
			else if (AppConstants.JSPReloadForWebModTask.equals(taskName)) {
				for (int i = 0; i < taskData[0].length; i++) {
					String taskDataName = taskData[0][i];

					if (AppConstants.APPDEPL_JSP_RELOADENABLED.equals(taskDataName)) {
						taskData[1][i] = AppConstants.YES_KEY;
					}
					else if (AppConstants.APPDEPL_JSP_RELOADINTERVAL.equals(taskDataName)) {
						taskData[1][i] = "3";
					}
				}
			}

			task.setTaskData(taskData)

			task = controller.getNextTask()
		}

		controller.saveAndClose()

		ReflectionMethodInvoker.invoke(monitor, "worked", 10)

		if (ReflectionMethodInvoker.invoke(monitor, "isCanceled", null)) {
			return null;
		}

		def options = controller.getAppDeploymentSavedResults()

		Hashtable module2Server = new Hashtable()
		module2Server.put("*", "WebSphere:cell=" + getCellName() + ",server=" + getServerName());

		options.put(AppConstants.APPDEPL_JSP_RELOADENABLED, true);
		options.put(AppConstants.APPDEPL_JSP_RELOADINTERVAL, "3");
		options.put(AppConstants.APPDEPL_RELOADENABLED, true);
		options.put(AppConstants.APPDEPL_RELOADINTERVAL, "3");

		options.put(AppConstants.APPDEPL_MODULE_TO_SERVER, module2Server);
		options.put("archive.upload", true);

		Object myhandback = String.valueOf(System.currentTimeMillis());
		options.put(AppConstants.APPDEPL_HANDBACK, myhandback);

		NotificationFilterSupport filter = new NotificationFilterSupport()

		filter.enableType(AppConstants.NotificationType);

		def listenerThread = new Thread() {
					boolean done = false

					public void run() {
						while (!done) {
							sleep(10)
						}
					}
				}

		def installMessage
		def installStatus

		def listener = new NotificationListener() {
					public void handleNotification(Notification notify, Object handback) {
						println "***************************************************"
						println "* Notification received at " + new Date().toString()
						println "* type      = " + notify.getType()
						println "* message   = " + notify.getMessage()
						println "* source    = " + notify.getSource()
						println "* seqNum    = " + Long.toString(notify.getSequenceNumber())
						println "* timeStamp = " + new Date(notify.getTimeStamp())
						println "* userData  = " + notify.getUserData()
						println "***************************************************"

						AppNotification data = (AppNotification)notify.getUserData()

						String status = data.taskStatus

						ReflectionMethodInvoker.invoke(monitor, "worked", 5)

						ReflectionMethodInvoker.invoke(monitor, "subTask", "Installing " + appName + ". Current task status [" + status + "]...")

						if (status.equals(AppNotification.STATUS_FAILED) || status.equals(AppNotification.STATUS_COMPLETED)) {
							installStatus = status
							installMessage = data.message
							listenerThread.done = true

							ReflectionMethodInvoker.invoke(monitor, "done", null)
						}
					}
				}

		getAdminClient().addNotificationListener(getAppManagementMBean(), listener, filter, listener)

		listenerThread.start()

		ReflectionMethodInvoker.invoke(monitor, "subTask", "Installing " + appName + "...")

		getAppManagement().installApplication(earPath, appName, options, null)

		ReflectionMethodInvoker.invoke(monitor, "worked", 10)

		listenerThread.join()

		getAdminClient().removeNotificationListener(appManagementMBean, listener)

		if (installStatus.equals(AppNotification.STATUS_COMPLETED)) {
			installMessage = null // means status is OK
		}

		return installMessage
	}

	Vector listApplications() {
		return getAppManagement().listApplications(null, null)
	}

	def queryConfigObjects(def params) {
		String opName = "queryConfigObjects"
		String[] queryConfigObjectsSignature =	[
			"com.ibm.websphere.management.Session",
			"javax.management.ObjectName",
			"javax.management.ObjectName",
			"javax.management.QueryExp"
		]

		getAdminClient().invoke(getConfigServiceMBean(), opName, params, queryConfigObjectsSignature)
	}

	void stopServer(){
		getAdminClient().invoke(getServerMBean(), "stop", null, null);
	}
    	
	void setOptions(Map options) {
		connectOptions = options
		if (debug) println "connectOptions: " + connectOptions

		adminClient = null
		appManagement = null
		appManagementMBean = null
		cellName = null
		configServiceMBean = null
		serverName = null
		serverMBean = null
	}

	void startApplication(String appName) {
		getAppManagement().startApplication(appName, null, null)
	}

	void startApplication(File scriptFile, String appName) {
		getAppManagement().startApplication(appName, null, null)
	}

	void stopApplication(String appName) {
		getAppManagement().stopApplication(appName, null, null)
	}

	Object uninstallApplication(File scriptFile, String appName, Object monitor) {
		Object myhandback = String.valueOf(System.currentTimeMillis());

		Hashtable options = new Hashtable()
		options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault())
		options.put(AppConstants.APPDEPL_HANDBACK, myhandback);

		NotificationFilterSupport filter = new NotificationFilterSupport()

		filter.enableType(AppConstants.NotificationType);

		def listenerThread = new Thread() {
					boolean done = false

					public void run() {
						while (!done) {
							sleep(10)
						}
					}
				}

		def uninstallMessage
		def uninstallStatus

		def listener = new NotificationListener() {
					public void handleNotification(Notification notify, Object handback) {
						println "***************************************************"
						println "* Notification received at " + new Date().toString()
						println "* type      = " + notify.getType()
						println "* message   = " + notify.getMessage()
						println "* source    = " + notify.getSource()
						println "* seqNum    = " + Long.toString(notify.getSequenceNumber())
						println "* timeStamp = " + new Date(notify.getTimeStamp())
						println "* userData  = " + notify.getUserData()
						println "***************************************************"

						AppNotification data = (AppNotification)notify.getUserData()

						String status = data.taskStatus

						ReflectionMethodInvoker.invoke(monitor, "worked", 5)

						ReflectionMethodInvoker.invoke(monitor, "subTask", "Uninstalling " + appName + ". Current task status [" + status + "]...")

						if (status.equals(AppNotification.STATUS_FAILED) || status.equals(AppNotification.STATUS_COMPLETED)) {
							uninstallStatus = status
							uninstallMessage = data.message
							listenerThread.done = true

							ReflectionMethodInvoker.invoke(monitor, "done", null)
						}
					}
				}

		getAdminClient().addNotificationListener(getAppManagementMBean(), listener, filter, listener)

		listenerThread.start()

		getAppManagement().uninstallApplication(appName, options, null)

		listenerThread.join()

		getAdminClient().removeNotificationListener(appManagementMBean, listener)

		if (uninstallStatus.equals(AppNotification.STATUS_COMPLETED)) {
			uninstallMessage = null // means status is OK
		}

		return uninstallMessage
	}

	Object updateApplication(String appName, String pathToContents, Object monitor) {
		Object myhandback = String.valueOf(System.currentTimeMillis());

		Hashtable options = new Hashtable()
		options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault())
		options.put(AppConstants.APPDEPL_HANDBACK, myhandback)
		options.put(AppConstants.APPUPDATE_CONTENTTYPE, AppConstants.APPUPDATE_CONTENT_PARTIALAPP)
		options.put(AppConstants.APPDEPL_ARCHIVE_UPLOAD, Boolean.TRUE)

		NotificationFilterSupport filter = new NotificationFilterSupport()

		filter.enableType(AppConstants.NotificationType);

		def listenerThread = new Thread() {
					boolean done = false

					public void run() {
						while (!done) {
							sleep(10)
						}
					}
				}

		def updateMessage
		def updateStatus

		def listener = new NotificationListener() {
					public void handleNotification(Notification notify, Object handback) {
						println "***************************************************"
						println "* Notification received at " + new Date().toString()
						println "* type      = " + notify.getType()
						println "* message   = " + notify.getMessage()
						println "* source    = " + notify.getSource()
						println "* seqNum    = " + Long.toString(notify.getSequenceNumber())
						println "* timeStamp = " + new Date(notify.getTimeStamp())
						println "* userData  = " + notify.getUserData()
						println "***************************************************"

						AppNotification data = (AppNotification)notify.getUserData()

						String status = data.taskStatus

						ReflectionMethodInvoker.invoke(monitor, "worked", 5)

						ReflectionMethodInvoker.invoke(monitor, "subTask", "Updating " + appName + ". Current task status [" + status + "]...")

						if (status.equals(AppNotification.STATUS_FAILED) || status.equals(AppNotification.STATUS_COMPLETED)) {
							updateStatus = status
							updateMessage = data.message
							listenerThread.done = true

							ReflectionMethodInvoker.invoke(monitor, "done", null)
						}
					}
				}

		getAdminClient().addNotificationListener(getAppManagementMBean(), listener, filter, listener)

		listenerThread.start()

		getAppManagement().updateApplication(appName, null, pathToContents, AppConstants.APPUPDATE_ADDUPDATE, options, null)

		listenerThread.join()

		getAdminClient().removeNotificationListener(appManagementMBean, listener)

		if (updateStatus.equals(AppNotification.STATUS_COMPLETED)) {
			updateMessage = null // means status is OK
		}

		return updateMessage
	}
}
