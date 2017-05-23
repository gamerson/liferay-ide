import javax.management.ObjectName;

def serverName
def fileTransferMBean

try {
	serverName = adminClient.getAttribute(serverMBean, "name")
	
	String query = "WebSphere:type=FileTransferServer,process=" + serverName + ",*"
	
	queryName = new ObjectName(query)
	
	s = adminClient.queryNames(queryName, null)
	
	if (!s.isEmpty()) {
		fileTransferMBean = (ObjectName) s.iterator().next()
		println "Got FileTransfer mbean for servername: " + serverName
	}
	else {
		System.out.println("FileTransfer MBean for servername " + serverName + "was not found.");
		return null
	}
	
	def fileTransferImpl = adminClient.invoke(fileTransferMBean, "getServerConfig", null, null)
	
	return fileTransferImpl.getProperties()
}
catch (Exception e) {
	println "Error getting FileTransfer properties: " + e
	return null
}


