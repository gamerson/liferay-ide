try {	
	state = adminClient.getAttribute(serverMBean, "state")
	println "Current server state: " + state
	return state
}
catch (Exception e) {
	println "Error in getting server state: " + e
	return null
}
