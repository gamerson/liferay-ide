import javax.management.ObjectName;

def configServiceMBean
def debugMode
def debugArgs

try {
	def serverName = adminClient.getAttribute(serverMBean, "name")
	
	String query = "WebSphere:type=ConfigService,process=" + serverName + ",*"
	
	ObjectName queryName = new ObjectName(query)
	
	Set s = adminClient.queryNames(queryName, null)
	
	if (!s.isEmpty()) {
		configServiceMBean = (ObjectName) s.iterator().next()
		println "Got ConfigService mbean for servername: " + serverName
	}
	else {
		System.out.println("ConfigService MBean for servername " + serverName + "was not found.");
		return null
	}
	
	def serverTypeConfigObjectName = new ObjectName("Websphere:_Websphere_Config_Data_Type=Server")
	
	Object[] queryConfigParams = [
		session,
		null,
		serverTypeConfigObjectName,
		null
	]
	
	def queryConfigObjects = { params ->
		String opName = "queryConfigObjects"
		String[] queryConfigObjectsSignature =	[
			"com.ibm.websphere.management.Session",
			"javax.management.ObjectName",
			"javax.management.ObjectName",
			"javax.management.QueryExp"
		];
		adminClient.invoke(configServiceMBean, opName, params, queryConfigObjectsSignature)
	}
	
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
	
	def getConfigServiceAttribute = { parent, attr ->
		String opName = "getAttribute";
		
		String[] signature = [
			"com.ibm.websphere.management.Session",
			"javax.management.ObjectName",
			"java.lang.String"
		]
		
		Object[] params = [session, parent, attr]
		
		return adminClient.invoke(configServiceMBean, opName, params, signature);
	}
	
	debugMode = getConfigServiceAttribute(javaVMConfigObjectName, "debugMode")
	
	println "debugMode=" + debugMode
	
	debugArgs = getConfigServiceAttribute(javaVMConfigObjectName, "debugArgs")
	
	println "debugArgs=" + debugArgs
}
catch (Exception e) {
	println "Exception in admin client: " + e
	return null
}

return ["debugMode":debugMode,"debugArgs":debugArgs]