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

package com.liferay.ide.server.websphere.admin;

import com.liferay.ide.scripting.core.GroovyScriptProxy;
import com.liferay.ide.server.websphere.core.IWebsphereRuntime;
import com.liferay.ide.server.websphere.core.IWebsphereServer;
import com.liferay.ide.server.websphere.core.WebsphereCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereAdminServiceProxy extends GroovyScriptProxy
{

    protected static Map<String, Boolean> certificateCache = new HashMap<String, Boolean>();

    private static File getCertFile( File javaHome )
    {
        File securityDir = new File( javaHome, "lib" + File.separatorChar + "security" );
        File certFile = new File( securityDir, "jssecacerts" );

        if( !certFile.exists() )
        {
            certFile = new File( securityDir, "cacerts" );
        }

        return certFile;
    }

    private static KeyStore getKeyStore( File javaHome )
    {
        KeyStore retval = null;

        File certFile = getCertFile( javaHome );

        try
        {
            KeyStore keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
            FileInputStream fin = new FileInputStream( certFile );
            keyStore.load( fin, getPassphrase() );
            fin.close();

            retval = keyStore;
        }
        catch( Exception e )
        {
            WebsphereCore.logError( "Could not get key store at " + javaHome, e );
        }

        return retval;
    }

    private static LocalTrustManager getLocalTrustManager( KeyStore keyStore )
    {
        LocalTrustManager retval = null;

        try
        {
            TrustManagerFactory trustFactory =
                TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            trustFactory.init( keyStore );

            retval = new LocalTrustManager( (X509TrustManager) trustFactory.getTrustManagers()[0] );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( "Error creating local trust manager", e );
        }

        return retval;
    }

    private static String getMapKey( String host, String port )
    {
        return host + ":" + port;
    }

    private static char[] getPassphrase()
    {
        return "changeit".toCharArray();
    }

    private static SSLSocket getSSLSocket( String host, int port, KeyStore keyStore, TrustManager trustManager )
    {
        SSLSocket retval = null;

        try
        {
            SSLContext context = SSLContext.getInstance( "TLS" );
            context.init( null, new TrustManager[] { trustManager }, null );

            SSLSocketFactory sslFactory = context.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket( host, port );
            sslSocket.setSoTimeout( 6000 );

            retval = sslSocket;
        }
        catch( Exception e )
        {
            WebsphereCore.logError( "Error trying to get ssl socket", e );
        }

        return retval;
    }

    protected static boolean acceptSSLCertificate( String host, String port, File javaHome )
    {
        boolean retval = false;

        KeyStore keyStore = getKeyStore( javaHome );
        LocalTrustManager trustManager = getLocalTrustManager( keyStore );

        try
        {
            int portValue = Integer.parseInt( port );
            SSLSocket sslSocket = getSSLSocket( host, portValue, keyStore, trustManager );
            sslSocket.startHandshake();
            sslSocket.close();
        }
        catch( SSLException sslException )
        {
            X509Certificate[] certificates = trustManager.certificates;

            try
            {
                int num = 1;

                for( X509Certificate certificate : certificates )
                {
                    String alias = host + ":" + num;
                    keyStore.setCertificateEntry( alias, certificate );
                }

                OutputStream out = new FileOutputStream( getCertFile( javaHome ) );
                keyStore.store( out, getPassphrase() );
                out.close();

                retval = true;
            }
            catch( Exception ex )
            {
                WebsphereCore.logError( "Error writing to certificate trust store.", ex );
            }
        }
        catch( IOException e )
        {
            WebsphereCore.logError( "Error configuring SSL cert chain." );
        }

        return retval;
    }

    /**
     * Checks ssl cert for host and port. If return value is null that means some other exception prevented a proper
     * check. If returns true, then SSL connection is valid. If returns false then received an SSL exception, invalid
     * certificate
     * 
     * @param host
     * @param port
     * @param javaHome
     * @return
     */
    protected static Boolean checkSSLCertificate( String host, String port, File javaHome )
    {
        Boolean check = certificateCache.get( getMapKey( host, port ) );

        if( check != null && check.booleanValue() )
        {
            return true;
        }

        KeyStore keyStore = getKeyStore( javaHome );

        if( keyStore != null )
        {
            LocalTrustManager trustManager = getLocalTrustManager( keyStore );

            int portValue = Integer.parseInt( port );
            SSLSocket sslSocket = getSSLSocket( host, portValue, keyStore, trustManager );

            if( sslSocket != null )
            {
                try
                {
                    sslSocket.startHandshake();
                    sslSocket.close();
                    check = true;
                }
                catch( SSLException sslException )
                {
                    check = false;
                }
                catch( IOException ioe )
                {
                    WebsphereCore.logError( "", ioe );
                }
            }
        }

        certificateCache.put( getMapKey( host, port ), check );

        return check;
    }

    protected File getJavaHome()
    {
        if( runtime != null )
        {
            return runtime.getVMInstall().getInstallLocation();
        }
        return new File( System.getProperty( "java.home" ) );
    }

    protected IWebsphereServer websphereServer;
    protected IWebsphereRuntime runtime;

    public WebsphereAdminServiceProxy( IWebsphereServer server )
    {
        this.runtime =
            (IWebsphereRuntime) server.getRuntime().loadAdapter( IWebsphereRuntime.class, new NullProgressMonitor() );
        this.websphereServer = server;
    }

    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
    {
        Object retval = null;
        Throwable error = null;

        configureClassloader();

        try
        {
            if( method.getName().equals( "checkSecureConnection" ) )
            {
                retval = checkSSLCertificate(
                    websphereServer.getHost(), this.websphereServer.getWebsphereSOAPPort(), getJavaHome() );
            }
            else if( method.getName().equals( "acceptSecureConnection" ) )
            {
                retval = acceptSSLCertificate(
                    websphereServer.getHost(), this.websphereServer.getWebsphereSOAPPort(), getJavaHome() );
            }
            else
            {
                Object serviceObject = getServiceObject();

                Method serviceMethod =
                    serviceObject.getClass().getMethod( method.getName(), method.getParameterTypes() );

                retval = serviceMethod.invoke( serviceObject, args );
            }
        }
        catch( Throwable t )
        {
            error = t;
        }
        finally
        {
            unconfigureClassloader();
        }

        if( error != null )
        {
            throw new RuntimeException( "Error in websphere admin service proxy.", error.getCause() );
        }

        return retval;
    }

    protected URL[] getProxyClasspath() throws CoreException
    {
        List<URL> scriptUrlList = new ArrayList<URL>();

        IRuntime serverRuntime = websphereServer.getRuntime();

        if( serverRuntime == null )
        {
            throw new CoreException( WebsphereCore.createErrorStatus( "Could not get server runtime." ) );
        }

        File runtimesFolder = serverRuntime.getLocation().append( "runtimes" ).toFile();

        String[] runtimes = runtimesFolder.list( new FilenameFilter()
        {

            public boolean accept( File dir, String name )
            {
                return ( name.contains( "com.ibm.ws.admin.client" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "com.ibm.ws.orb" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "com.ibm.ws.runtime" ) && name.endsWith( ".jar" ) );
            }

        } );

        for( String runtime : runtimes )
        {
            File runtimeJar = new File( runtimesFolder, runtime );

            if( runtimeJar.exists() )
            {
                try
                {
                    scriptUrlList.add( runtimeJar.toURI().toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        File libFolder = serverRuntime.getLocation().append( "lib" ).toFile();

        String[] libs = libFolder.list( new FilenameFilter()
        {

            public boolean accept( File dir, String name )
            {
                return ( name.contains( "j2ee" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "wsadmin" ) && name.endsWith( ".jar" ) );
            }

        } );

        for( String lib : libs )
        {
            File libJar = new File( libFolder, lib );

            if( libJar.exists() )
            {
                try
                {
                    scriptUrlList.add( libJar.toURI().toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        File pluginsFolder = serverRuntime.getLocation().append( "plugins" ).toFile();

        String[] plugins = pluginsFolder.list( new FilenameFilter()
        {

            public boolean accept( File dir, String name )
            {
                return ( name.contains( "com.ibm.ws.runtime.client" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "com.ibm.ws.runtime" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "com.ibm.ws.security.crypto" ) && name.endsWith( ".jar" ) );
            }

        } );

        for( String plugin : plugins )
        {
            File pluginJar = new File( pluginsFolder, plugin );

            if( pluginJar.exists() )
            {
                try
                {
                    scriptUrlList.add( pluginJar.toURI().toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        IPath vmPath = new Path( runtime.getVMInstall().getInstallLocation().getAbsolutePath() );
        File javaFolder = vmPath.append( "jre" ).append( "lib" ).toFile();

        String[] javaLibs = javaFolder.list( new FilenameFilter()
        {

            public boolean accept( File dir, String name )
            {
                return ( name.contains( "ibmjceprovider" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmcertpathprovider" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmjsseprovider" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmjgssprovider" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmcfw" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmorb" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmorbapi" ) && name.endsWith( ".jar" ) ) ||
                    ( name.contains( "ibmjgssprovider" ) && name.endsWith( ".jar" ) );
            }

        } );

        for( String javaLib : javaLibs )
        {
            File javaJar = new File( javaFolder, javaLib );

            if( javaJar.exists() )
            {
                try
                {
                    scriptUrlList.add( javaJar.toURI().toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        return scriptUrlList.toArray( new URL[0] );
    }

    protected File getGroovyFile() throws Exception
    {
        return new File(
            FileLocator.toFileURL(
                WebsphereCore.getPluginEntry( "/scripts/admin/WebsphereAdminService.groovy" ) ).getFile() );
    }

}
