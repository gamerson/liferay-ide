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

package com.liferay.ide.server.websphere.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.websphere.admin.IWebsphereAdminService;
import com.liferay.ide.server.websphere.admin.WebsphereAdminServiceProxy;
import com.liferay.ide.server.websphere.util.WebsphereUtil;

import java.io.File;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereCore extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.ee.websphere.core"; //$NON-NLS-1$

    // The shared instance
    private static WebsphereCore plugin;

    private static Map<String, IWebsphereAdminService> services = null;

    // private static IWebsphereStub[] websphereStubs;

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

    public static IWebsphereAdminService getWebsphereAdminService( final IWebsphereServer server )
    {
        if( services == null )
        {
            services = new HashMap<String, IWebsphereAdminService>();

            ServerCore.addServerLifecycleListener( new IServerLifecycleListener()
            {

                public void serverAdded( IServer server )
                {
                }

                public void serverChanged( IServer server )
                {
                }

                public void serverRemoved( IServer s )
                {
                    if( server.equals( s ) )
                    {
                        IWebsphereAdminService service = services.get( server.getId() );

                        if( service != null )
                        {
                            service = null;
                            services.put( server.getId(), null );
                        }
                    }
                }
            } );
        }

        IWebsphereAdminService service = services.get( server.getId() );

        if( service == null )
        {
            Class<?>[] interfaces = new Class<?>[] { IWebsphereAdminService.class };

            service = (IWebsphereAdminService) Proxy.newProxyInstance(
                IWebsphereAdminService.class.getClassLoader(), interfaces, new WebsphereAdminServiceProxy( server ) );

            updateConnectionSettings( server.getHost(), server, service );

            services.put( server.getId(), service );
        }

        return service;
    }

    public static IRuntime[] getWebsphereRuntimes()
    {
        List<IRuntime> websphereRuntimes = new ArrayList<IRuntime>();
        IRuntime[] runtimes = ServerCore.getRuntimes();

        if( !CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                if( WebsphereUtil.isWebsphereRuntime( runtime ) )
                {
                    websphereRuntimes.add( runtime );
                }
            }
        }

        return websphereRuntimes.toArray( new IRuntime[0] );
    }

    public static void logError( Exception e )
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

    public static void updateConnectionSettings( String host, IWebsphereServer server )
    {
        updateConnectionSettings( host, server, getWebsphereAdminService( server ) );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public static void updateConnectionSettings( String host, IWebsphereServer server, IWebsphereAdminService service )
    {
        Map options = new HashMap();
        String websphereProfileLocation = server.getWebsphereProfileLocation();
        options.put( IWebsphereServer.WEBSPHERE_PROFILE_LOCATION, websphereProfileLocation );
        options.put( "\"" + websphereProfileLocation + "connectorHost" + "\"", host );
        options.put( "\"" + websphereProfileLocation + "connectorPort" + "\"", server.getWebsphereSOAPPort() );
        options.put( "\"" + websphereProfileLocation + "connectorType" + "\"", "SOAP" );
        options.put( "\"" + websphereProfileLocation + "securityEnabled" + "\"", server.getWebsphereSecurityEnabled() );
        options.put(
            "\"" + websphereProfileLocation + IWebsphereServer.WEBSPHERE_SECURITY_USERID + "\"",
            server.getWebsphereUserId() );
        options.put(
            "\"" + websphereProfileLocation + IWebsphereServer.WEBSPHERE_SECURITY_PASSWORD + "\"",
            server.getWebsphereUserPassword() );

        IPath profileLocation = new Path( server.getWebsphereProfileLocation() );
        options.put(
            "\"" + websphereProfileLocation + IWebsphereServer.WEBSPHERE_SOPA_CONFIG_URL + "\"",
            "file:" + profileLocation.append( "properties" ).append( "soap.client.props" ) );
        service.setOptions( options );
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
