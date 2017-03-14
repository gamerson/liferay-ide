import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;

import javax.management.ObjectName;

def debug = false
def adminClient
def session
def serverMBean
def state

Properties connectProps = new Properties();
connectProps.setProperty(AdminClient.CONNECTOR_TYPE, connectorType);
connectProps.setProperty(AdminClient.CONNECTOR_HOST, connectorHost);
connectProps.setProperty(AdminClient.CONNECTOR_PORT, connectorPort);
connectProps.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true");

// Get an AdminClient based on the connector properties
try {
	adminClient = AdminClientFactory.createAdminClient(connectProps)
	
	if (adminClient == null) {
		return null
	}
	
	session = adminClient.isAlive()
	
	String query = "WebSphere:*,type=Server,j2eeType=J2EEServer"
	
	ObjectName queryName = new ObjectName(query)
	
	Set s = adminClient.queryNames(queryName, null)
	
	if (!s.isEmpty()) {
		serverMBean = (ObjectName) s.iterator().next()
		if (debug) println "Found server mbean."
	}
	else {
		if (debug) println "Server MBean was not found."
		return null
	}
}
catch (Exception e) {
	if (debug) println "Error creating admin client: " + e
	return null
}

