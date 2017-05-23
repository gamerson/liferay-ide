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

package com.liferay.ide.server.websphere.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class WebsphereUI extends AbstractUIPlugin {

	// The plugin ID
	public static final String PLUGIN_ID = "com.liferay.ide.server.websphere.ui"; //$NON-NLS-1$

	// The shared instance
	private static WebsphereUI plugin;
	
	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, CoreException e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static WebsphereUI getDefault() {
		return plugin;
	}

	public static void logError(String msg, CoreException e) {
		getDefault().getLog().log(createErrorStatus(msg, e));
	}

	/**
	 * The constructor
	 */
	public WebsphereUI() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
