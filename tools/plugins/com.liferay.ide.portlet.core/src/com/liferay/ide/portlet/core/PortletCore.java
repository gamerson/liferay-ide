/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.portlet.core;

import com.liferay.ide.portlet.core.job.BuildLanguageJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

/**
 * The activator class controls the plugin life cycle
 *
 * @auther Greg Amerson
 * @author Cindy Li
 */
public class PortletCore extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.portlet.core"; //$NON-NLS-1$

    public static final String PREF_KEY_PORTLET_SUPERCLASSES_USED = "portlet-superclasses-used"; //$NON-NLS-1$

    // The shared instance
    private static PortletCore plugin;

    public static BuildLanguageJob createBuildLanguageJob( IFile file )
    {
        BuildLanguageJob job = new BuildLanguageJob( file );

        return job;
    }

    public static IStatus createErrorStatus( Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createWarningStatus( String msg )
    {
        return new Status( IStatus.WARNING, PLUGIN_ID, msg );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static PortletCore getDefault()
    {
        return plugin;
    }

    public static Preferences getPreferences()
    {
        @SuppressWarnings( "deprecation" )
        final IScopeContext scope = new InstanceScope();

        return scope.getNode( PLUGIN_ID );
    }

    public static void logError( Exception ex )
    {
        getDefault().getLog().log( createErrorStatus( ex ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    /**
     * The constructor
     */
    public PortletCore()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {

        super.start( context );

        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {

        plugin = null;

        super.stop( context );
    }

}
