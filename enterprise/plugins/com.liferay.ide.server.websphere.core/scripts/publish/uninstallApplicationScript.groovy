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
 * appManagementMBean
 * appName
 * monitor
 */


try {
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
					
					if (status.equals(AppNotification.STATUS_FAILED) || status.equals(AppNotification.STATUS_COMPLETED)) {
						uninstallStatus = status
						uninstallMessage = data.message
						listenerThread.done = true
					}
				}
			}
	
	adminClient.addNotificationListener(appManagementMBean, listener, filter, listener)
	
	listenerThread.start()
	
	appManagement.uninstallApplication(appName, options, null)
	
	listenerThread.join()
	
	adminClient.removeNotificationListener(appManagementMBean, listener)
	
	if (uninstallStatus.equals(AppNotification.STATUS_COMPLETED)) {
		uninstallMessage = null // means status is OK
	}
	
	return uninstallMessage
}

catch (Exception e) {
	println e
	e.printStackTrace()
	
	return e
}
