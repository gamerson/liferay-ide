import com.ibm.websphere.management.AdminClient
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentTask;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/*
 * Available variables
 * 
 * cellName
 * serverName
 * adminClient
 * appManagement
 * appManagementMBean
 * earPath
 * appName
 * monitor
 */

try {
	Properties defaultBnd = new Properties()
	defaultBnd.put(AppConstants.APPDEPL_DFLTBNDG_VHOST, "default_host")
	
	def prefs = new Hashtable()
	prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault())
	prefs.put(AppConstants.APPDEPL_DFLTBNDG, defaultBnd)
	
	AppDeploymentController controller = AppDeploymentController.readArchive(earPath, prefs)
	AppDeploymentTask task = controller.getFirstTask()
	
	while (task != null) {
		if (AppConstants.AppDeploymentOptionsTask.equals(task.getName())) {
			String[][] data = task.getTaskData()
			
			for (int i = 0; i < data[0].length; i++) {
				if (AppConstants.APPDEPL_APPNAME.equals(data[0][i])) {
					appName = data[1][i]
				}
			}
		}
		
		task = controller.getNextTask()
	}
	
	controller.saveAndClose()
	
	def options = controller.getAppDeploymentSavedResults()
	
	Hashtable module2Server = new Hashtable()
	module2Server.put("*", "WebSphere:cell=" + cellName + ",server=" + serverName);
	
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
					
					if (status.equals(AppNotification.STATUS_FAILED) || status.equals(AppNotification.STATUS_COMPLETED)) {
						installStatus = status
						installMessage = data.message
						listenerThread.done = true
					}
				}
			}
	
	adminClient.addNotificationListener(appManagementMBean, listener, filter, listener)
	
	listenerThread.start()
	
	appManagement.installApplication(earPath, appName, options, null)
	
	listenerThread.join()
	
	appManagement.startApplication(appName, options, null)
	
	adminClient.removeNotificationListener(appManagementMBean, listener)
	
	if (installStatus.equals(AppNotification.STATUS_COMPLETED)) {
		installMessage = null // means status is OK
	}
	
	return installMessage
}

catch (Exception e) {
	println e
	e.printStackTrace()
	
	return e
}
