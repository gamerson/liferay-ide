/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.groovy.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KaleoGroovyUI extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.kaleo.groovy.ui"; //$NON-NLS-1$

	// The shared instance
	private static KaleoGroovyUI groovyPlugin;

	/**
	 * The constructor
	 */
	public KaleoGroovyUI()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		groovyPlugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    groovyPlugin = null;
		super.stop(context);
	}

	public static void logError( String msg, Exception e )
	{
		getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static KaleoGroovyUI getDefault()
	{
		return groovyPlugin;
	}

	public static void logError( Exception e )
	{
		getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
	}

	public static IStatus createErrorStatus( String msg, Exception e )
	{
		return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
	}

}
