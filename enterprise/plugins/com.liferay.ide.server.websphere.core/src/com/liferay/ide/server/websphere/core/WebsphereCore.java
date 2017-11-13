/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.server.websphere.core;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereCore extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.websphere.core"; //$NON-NLS-1$

    // The shared instance
    private static WebsphereCore plugin;

    public static IStatus createErrorStatus( Exception ex )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex );
    }

    public static IStatus createErrorStatus( String msg, int code )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, code, msg, null );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String msg, Exception ex )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, ex );
    }

    public static IStatus createInfoStatus( String message )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, message );
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
    public static WebsphereCore getDefault()
    {
        return plugin;
    }

    public static URL getPluginEntry( String path )
    {
        return getDefault().getBundle().getEntry( path );
    }

    public static IEclipsePreferences getPreferences()
    {
        return InstanceScope.INSTANCE.getNode( PLUGIN_ID );
    }

    public static IPath getTempLocation( String prefix, String fileName )
    {
        return getDefault().getStateLocation().append( "tmp" ).append(
            prefix + "/" + System.currentTimeMillis() + ( CoreUtil.isNullOrEmpty( fileName ) ? "" : "/" + fileName ) );
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( Throwable e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( String msg )
    {
        getDefault().getLog().log( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Exception ex )
    {
        getDefault().getLog().log( createErrorStatus( msg, ex ) );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext bundleContext ) throws Exception
    {
        super.start( bundleContext );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext bundleContext ) throws Exception
    {

        super.stop( bundleContext );

        // delete tmp folder
        File tmpDir = getDefault().getStateLocation().append( "tmp" ).toFile();

        if( tmpDir.exists() )
        {
            FileUtil.deleteDir( tmpDir, true );
        }

        plugin = null;
    }
}
