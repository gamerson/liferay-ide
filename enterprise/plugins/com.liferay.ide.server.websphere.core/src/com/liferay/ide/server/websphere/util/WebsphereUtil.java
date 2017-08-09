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

package com.liferay.ide.server.websphere.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.websphere.core.IWebsphereRuntime;
import com.liferay.ide.server.websphere.core.IWebsphereServer;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereProfile;
import com.liferay.ide.server.websphere.core.WebsphereProfileProperties;
import com.liferay.ide.server.websphere.core.WebspherePropertyValueHandler;
import com.liferay.ide.server.websphere.core.WebsphereServerProductInfoHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereUtil
{

    public static final String[] WAS_EDITION_IDS = { "BASE", "ND", "EXPRESS", "embeddedEXPRESS", "NDDMZ" };
    private static final String DEFAULT_LIFERAY_PORTAL_APP_NAME = ".*liferay.*";
    private static final char[] _HEX_CHARACTERS =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final int _STATE_ABSENT = 1;

    private static final int _STATE_EXPIRED = 2;

    private static final int _STATE_GOOD = 3;

    private static final int _STATE_INACTIVE = 4;

    private static final int _STATE_INVALID = 5;

    private static final int _STATE_OVERLOAD = 6;

    private static final int[] BAD_STATES =
        { _STATE_ABSENT, _STATE_EXPIRED, _STATE_INACTIVE, _STATE_INVALID, _STATE_OVERLOAD };

    private static final String PRODUCT_ID_PORTAL = "Portal";

    private static String _digest( MessageDigest messageDigest, String text )
    {
        messageDigest.update( text.getBytes() );

        byte[] bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder( bytes.length << 1 );

        for( int i = 0; i < bytes.length; i++ )
        {
            int byte_ = bytes[i] & 0xff;

            sb.append( _HEX_CHARACTERS[byte_ >> 4] );
            sb.append( _HEX_CHARACTERS[byte_ & 0xf] );
        }

        return sb.toString();
    }

    private static String _digest( String productId, String uuid, int licenseState ) throws Exception
    {

        MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );

        String digest = _digest( messageDigest, uuid + productId );

        int length = digest.length();

        StringBuilder sb = new StringBuilder( length + ( length / 4 ) );

        for( int i = 0; i < ( length / 2 ); i++ )
        {
            if( ( i % 2 ) == 0 )
            {
                sb.append( licenseState );
            }

            sb.append( digest.charAt( i ) );
            sb.append( digest.charAt( length - i - 1 ) );
        }

        return _digest( messageDigest, sb.toString() );
    }

    private static void addRemoveProps(
        IPath deltaPath, IResource deltaResource, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
        String deletePrefix ) throws IOException
    {
        String archive = removeArchive( deltaPath.toPortableString() );

        ZipEntry zipEntry = null;

        // check to see if we already have an entry for this archive
        for( ZipEntry entry : deleteEntries.keySet() )
        {
            if( entry.getName().startsWith( archive ) )
            {
                zipEntry = entry;
            }
        }

        if( zipEntry == null )
        {
            zipEntry = new ZipEntry( archive + "META-INF/" + deletePrefix + "-partialapp-delete.props" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        String existingFiles = deleteEntries.get( zipEntry );

        // String file =
        // encodeRemovedPath(deltaPath.toPortableString().substring(archive.length()));
        String file = deltaPath.toPortableString().substring( archive.length() );

        if( deltaResource.getType() == IResource.FOLDER )
        {
            file += "/.*"; //$NON-NLS-1$
        }

        deleteEntries.put( zipEntry, ( existingFiles != null ? existingFiles : StringPool.EMPTY ) + ( file + "\n" ) ); //$NON-NLS-1$
    }

    private static void addToZip( IPath path, IResource resource, ZipOutputStream zip, boolean adjustGMTOffset )
        throws IOException, CoreException
    {
        switch( resource.getType() )
        {
        case IResource.FILE:
            ZipEntry zipEntry = new ZipEntry( path.toString() );

            zip.putNextEntry( zipEntry );

            InputStream contents = ( (IFile) resource ).getContents();

            if( adjustGMTOffset )
            {
                TimeZone currentTimeZone = TimeZone.getDefault();
                Calendar currentDt = new GregorianCalendar( currentTimeZone, Locale.getDefault() );

                // Get the Offset from GMT taking current TZ into account
                int gmtOffset = currentTimeZone.getOffset(
                    currentDt.get( Calendar.ERA ), currentDt.get( Calendar.YEAR ), currentDt.get( Calendar.MONTH ),
                    currentDt.get( Calendar.DAY_OF_MONTH ), currentDt.get( Calendar.DAY_OF_WEEK ),
                    currentDt.get( Calendar.MILLISECOND ) );

                zipEntry.setTime( System.currentTimeMillis() + ( gmtOffset * -1 ) );
            }

            try
            {
                IOUtils.copy( contents, zip );
            }
            finally
            {
                contents.close();
            }

            break;

        case IResource.FOLDER:
        case IResource.PROJECT:
            IContainer container = (IContainer) resource;

            IResource[] members = container.members();

            for( IResource res : members )
            {
                addToZip( path.append( res.getName() ), res, zip, adjustGMTOffset );
            }
        }
    }

    private static void bindPort( String host, int port ) throws Exception
    {
        Socket s = new Socket();
        s.bind( new InetSocketAddress( host, port ) );
        s.close();
    }

    public static boolean canCreateWASProfile( File wasHome )
    {
        String wasHomeLocation = ensureEndingPathSeparator( wasHome.getPath(), true );
        File wasProperties = new File( wasHomeLocation + "properties" );
        boolean canWriteToWASProperties = canWriteToDirectory( wasProperties );

        File wasManageProfilesLogs = new File( wasHomeLocation + "logs/manageprofiles" );
        boolean canWriteToWASManageProfilesLogs = true;
        if( wasManageProfilesLogs.exists() )
        {
            canWriteToWASManageProfilesLogs = canWriteToDirectory( wasManageProfilesLogs );
        }

        return( ( canWriteToWASProperties ) && ( canWriteToWASManageProfilesLogs ) );
    }

    public static boolean canWriteToDirectory( File file )
    {
        if( CoreUtil.isWindows() )
        {
            String baseTempFile = ensureEndingPathSeparator( file.getPath(), true ) + "ST_TEST_FILE";
            String testFile = baseTempFile;

            File tempFile = new File( testFile );

            for( int i = 0; tempFile.exists(); ++i )
            {
                testFile = baseTempFile + i;
                tempFile = new File( testFile );
            }
            try
            {
                if( tempFile.createNewFile() )
                {
                    boolean canWrite = true;
                    if( isFileInVistaVirtualStore( testFile ) )
                    {
                        canWrite = false;
                    }

                    return canWrite;
                }

                return false;
            }
            catch( IOException e )
            {
                return false;
            }
        }

        return file.canWrite();
    }

    public static void checkLicenseState( URL serverUrl, String login, String password ) throws CoreException
    {
        String uuid = UUID.randomUUID().toString();

        try
        {
            String contextUrl = serverUrl.toExternalForm();

            if( !contextUrl.endsWith( "/" ) )
            {
                contextUrl = contextUrl.concat( "/" );
            }

            URL url = new URL( contextUrl + "c/portal/license?cmd=validateState" );

            URLConnection urlConnection = url.openConnection();

            String val = login + ":" + password;

            byte[] base = val.getBytes();

            String authorizationString = "Basic " + new String( new Base64().encode( base ) );

            urlConnection.setRequestProperty( "Authorization", authorizationString );

            urlConnection.setDoOutput( true );

            OutputStream outputStream = urlConnection.getOutputStream();

            String request = "productId=" + PRODUCT_ID_PORTAL + "&uuid=" + uuid;

            outputStream.write( request.getBytes( "UTF-8" ) );

            InputStream is = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( is ) );

            String line = bufferedReader.readLine();

            if( line != null )
            {
                String hash = _digest( PRODUCT_ID_PORTAL, uuid, _STATE_GOOD );

                if( !line.equals( hash ) )
                {
                    for( int state : BAD_STATES )
                    {
                        hash = _digest( PRODUCT_ID_PORTAL, uuid, state );

                        if( line.equals( hash ) )
                        {
                            throw new CoreException(
                                WebsphereCore.createErrorStatus(
                                    "Liferay Portal Enterprise Edition license is not valid. Please update license on remote server then try again." ) );
                        }
                    }
                }
            }
            else
            {
                throw new CoreException(
                    WebsphereCore.createErrorStatus(
                        "Could not validate Liferay Portal. Please make sure OmniAdmin username and password are correct." ) );
            }

            bufferedReader.close();
            outputStream.close();
            is.close();
        }
        catch( CoreException ce )
        {
            throw ce;
        }
        catch( Exception e )
        {
            throw new CoreException( WebsphereCore.createErrorStatus( "Could not validate Liferay Portal." ) );
        }
    }

    public static File createPartialEAR(
        String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, String deltaPrefix,
        boolean adjustGMTOffset )
    {
        IPath path = LiferayServerCore.getTempLocation( "partial-ear", archiveName ); //$NON-NLS-1$

        FileOutputStream outputStream = null;
        ZipOutputStream zip = null;
        File file = path.toFile();

        file.getParentFile().mkdirs();

        try
        {
            outputStream = new FileOutputStream( file );
            zip = new ZipOutputStream( outputStream );

            Map<ZipEntry, String> deleteEntries = new HashMap<ZipEntry, String>();

            processResourceDeltasZip( deltas, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );

            for( ZipEntry entry : deleteEntries.keySet() )
            {
                zip.putNextEntry( entry );
                zip.write( deleteEntries.get( entry ).getBytes() );
            }

            // if ((removedResources != null) && (removedResources.size() > 0))
            // {
            // writeRemovedResources(removedResources, zip);
            // }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if( zip != null )
            {
                try
                {
                    zip.close();
                }
                catch( IOException localIOException1 )
                {

                }
            }
        }

        return file;
    }

    public static String ensureEndingPathSeparator( String curPath, boolean isEndWithPathSeparator )
    {
        if( curPath != null )
        {
            boolean curIsEndWithPathSeparator = curPath.replace( '\\', '/' ).endsWith( "/" );
            if( curIsEndWithPathSeparator != isEndWithPathSeparator )
            {
                if( isEndWithPathSeparator )
                {
                    curPath = curPath + "/";
                }
                else
                {
                    curPath = curPath.substring( 0, curPath.length() - 1 );
                }
            }
        }
        return curPath;
    }

    public static String[] extractLiferayAppFolder( IPath profileLocation, String cellName, String nodeName )
    {
        List<String> liferayAppNameValues = new ArrayList<String>();

        if( profileLocation == null || !profileLocation.toFile().exists() )
        {
            return null;
        }

        Pattern pattern = Pattern.compile( DEFAULT_LIFERAY_PORTAL_APP_NAME );

        IPath serverIndexFile =
            profileLocation.append( "config" ).append( "cells" ).append( cellName ).append( "nodes" ).append(
                nodeName ).append( "serverindex.xml" );
        try(InputStreamReader reader =
            new InputStreamReader( new FileInputStream( serverIndexFile.toFile() ), "utf-8" ))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document document = parser.parse( new InputSource( reader ) );
            NodeList extendApplications = document.getElementsByTagName( "extendedApplicationDataElements" );

            for( int j = 0; j < extendApplications.getLength(); j++ )
            {
                final Node extendApplication = extendApplications.item( j );
                Node applicationNameValueNode = extendApplication.getAttributes().getNamedItem( "applicationName" );

                if( applicationNameValueNode != null )
                {
                    String applicationNamve = applicationNameValueNode.getNodeValue();
                    if( pattern.matcher( applicationNamve ).matches() )
                    {
                        liferayAppNameValues.add( applicationNamve );
                        Node extendApplicatonNode =
                            extendApplication.getAttributes().getNamedItem( "standaloneModuleName" );
                        liferayAppNameValues.add( extendApplicatonNode.getNodeValue() );
                    }
                }
            }
        }
        catch( IOException | SAXException | ParserConfigurationException e )
        {
            WebsphereCore.logError( e );
        }
        return liferayAppNameValues.toArray( new String[liferayAppNameValues.size()] );
    }

    public static String getAppName( IProject project )
    {
        return project.getName() + "-plugin";
    }

    public static WebsphereProfile getDefaultProfile( IPath sWASHome ) throws Exception
    {
        List<WebsphereProfile> listProfiles = getProfileList( sWASHome );

        for( int i = 0; i < listProfiles.size(); ++i )
        {
            WebsphereProfile profileThis = listProfiles.get( i );

            if( profileThis.isDefault() )
            {
                return profileThis;
            }
        }

        return null;
    }

    public static String getLiferayPortalVersionInfo( URL portalUrl )
    {
        try
        {
            Map<String, List<String>> fieldsMap = portalUrl.openConnection().getHeaderFields();
            if( fieldsMap != null )
            {
                List<String> portalField = fieldsMap.get( "Liferay-Portal" );

                if( portalField != null )
                {
                    return portalField.get( 0 );
                }
            }
        }
        catch( IOException e )
        {
        }

        return null;
    }

    public static IProgressMonitor getMonitorFor( IProgressMonitor monitor )
    {
        if( monitor == null )
            return new NullProgressMonitor();
        return monitor;
    }

    public static File getNormalizedPath( File file )
    {
        if( file == null )
        {
            return null;
        }

        String sPath = file.getAbsolutePath();

        boolean fIsTrailingFileSep = sPath.endsWith( File.separator );
        boolean fIsStartingFileSep = sPath.startsWith( File.separator );

        StringTokenizer st = new StringTokenizer( sPath, File.separator );
        List<String> listPathNames = new Vector<String>();

        while( st.hasMoreTokens() )
        {
            String sToken = st.nextToken();

            if( ".".equals( sToken ) )
            {
                continue;
            }

            if( ( listPathNames.size() > 1 ) && CoreUtil.isWindows() && ( "..".equals( sToken ) ) )
            {
                listPathNames.remove( listPathNames.size() - 1 );
            }
            else if( ( listPathNames.size() > 0 ) && ( !CoreUtil.isWindows() ) && ( "..".equals( sToken ) ) )
            {
                listPathNames.remove( listPathNames.size() - 1 );
            }
            else if( !( "..".equals( sToken ) ) )
            {
                listPathNames.add( sToken );
            }
        }

        StringBuffer sb = new StringBuffer();

        if( fIsStartingFileSep )
        {
            sb.append( File.separator );
        }

        Iterator<String> i = listPathNames.iterator();
        while( i.hasNext() )
        {
            sb.append( i.next() );
            if( !( i.hasNext() ) )
            {
                continue;
            }
            sb.append( File.separator );
        }

        if( fIsTrailingFileSep )
        {
            sb.append( File.separator );
        }
        else if( CoreUtil.isWindows() && ( listPathNames.size() == 1 ) )
        {
            sb.append( File.separator );
        }
        return new File( sb.toString() );
    }

    public static WebsphereProfile getProfile( IPath sWASHome, String sProfileName ) throws Exception
    {
        List<WebsphereProfile> listProfiles = getProfileList( sWASHome );

        for( int i = 0; i < listProfiles.size(); ++i )
        {
            WebsphereProfile profileThis = listProfiles.get( i );

            if( profileThis.getName().equals( sProfileName ) )
            {
                return profileThis;
            }
        }

        return null;
    }

    public static List<WebsphereProfile> getProfileList( IPath websphereHomeLocation ) throws Exception
    {
        return listProfilesInRegistry( getRegistryFile( websphereHomeLocation ) );
    }

    public static File getRegistryFile( IPath websphereHomeLocation )
    {
        IPath profileRegistryPath = null;
        try
        {
            WebsphereProfileProperties websphereProfileProperties =
                new WebsphereProfileProperties( websphereHomeLocation );
            profileRegistryPath = websphereProfileProperties.getProperty( "WS_PROFILE_REGISTRY" );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return profileRegistryPath.toFile();
    }

    public static IRuntime getRuntimeByName( String runtimeName )
    {
        IRuntime retval = null;
        IRuntime[] runtimes = WebsphereCore.getWebsphereRuntimes();

        if( !CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                if( runtime.getName().equals( runtimeName ) )
                {
                    retval = runtime;
                    break;
                }
            }
        }

        return retval;
    }

    public static IProgressMonitor getSubMonitorFor( IProgressMonitor monitor, int ticks )
    {
        if( monitor == null )
        {
            return new NullProgressMonitor();
        }

        if( monitor instanceof NullProgressMonitor )
        {
            return monitor;
        }
        return new SubProgressMonitor( monitor, ticks );
    }

    public static IProgressMonitor getSubMonitorFor( IProgressMonitor monitor, int ticks, int style )
    {
        if( monitor == null )
        {
            return new NullProgressMonitor();
        }
        if( monitor instanceof NullProgressMonitor )
        {
            return monitor;
        }
        return new SubProgressMonitor( monitor, ticks, style );
    }

    public static int getWebsphereProcessPID( IWebsphereServer server )
    {
        IPath websphereProfileLocation = new Path( server.getWebsphereProfileLocation() );
        String websphereServerName = server.getWebsphereServerName();
        IPath pidFolder = websphereProfileLocation.append( "logs" ).append( websphereServerName );

        IPath pidLocation = null;
        if( pidFolder != null && pidFolder.toFile().exists() )
        {
            pidLocation = pidFolder.append( websphereServerName ).append( ".pid" );

            if( !( pidLocation.toFile().exists() ) )
            {
                return 0;
            }
        }

        int pid = -1;

        try(BufferedReader bufferReader = new BufferedReader( new FileReader( pidLocation.toFile() ) ))
        {
            pid = Integer.parseInt( bufferReader.readLine() );
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return pid;
    }

    public static IWebsphereRuntime getWebsphereRuntime( IRuntime runtime )
    {
        if( runtime != null )
        {
            Object websphereRuntime = runtime.loadAdapter( IWebsphereRuntime.class, null );

            if( websphereRuntime instanceof IWebsphereRuntime )
            {
                return (IWebsphereRuntime) websphereRuntime;
            }
        }

        return null;
    }

    public static String[] getWebsphereRuntimeNames()
    {
        List<String> ids = new ArrayList<String>();

        IRuntime[] runtimes = WebsphereCore.getWebsphereRuntimes();

        if( !CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                ids.add( runtime.getName() );
            }
        }

        return ids.toArray( new String[0] );
    }

    public static IWebsphereServer getWebsphereServer( IServer server )
    {
        if( server != null )
        {
            return (IWebsphereServer) server.loadAdapter( IWebsphereServer.class, null );
        }

        return null;
    }

    public static boolean isFileInVistaVirtualStore( String filePath )
    {
        if( ( filePath == null ) || ( filePath.length() == 0 ) )
        {
            return false;
        }
        String testPath = System.getenv( "LOCALAPPDATA" );

        if( ( testPath == null ) || ( testPath.length() == 0 ) )
        {
            return false;
        }
        testPath = testPath + "\\VirtualStore";
        int i = filePath.indexOf( ":" );

        if( i != -1 )
        {
            if( i + 1 != filePath.length() )
            {
                testPath = testPath + filePath.substring( i + 1 );
            }
        }
        else
        {
            testPath = testPath + filePath;
        }

        File tempFile = new File( testPath );
        return tempFile.exists();
    }

    public static boolean isPortAvailable( int port )
    {
        try
        {
            bindPort( "0.0.0.0", port );
            bindPort( InetAddress.getLocalHost().getHostAddress(), port );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public static boolean isWebsphereRunning( int pid )
    {
        if( pid <= 0 )
        {
            return false;
        }
        String[] cmd = null;

        boolean isRunning = false;

        if( CoreUtil.isWindows() )
        {
            cmd = new String[] { "tasklist" };
        }
        else if( CoreUtil.isLinux() )
        {
            cmd = new String[] { "ps", "-ef" };
        }
        else
        {
            return false;
        }

        String pidVal = String.valueOf( pid );
        int len = pidVal.length();
        if( cmd != null )
        {

        }
        try(BufferedReader pidBuffer =
            new BufferedReader( new InputStreamReader( Runtime.getRuntime().exec( cmd ).getInputStream() ) ))
        {
            String line = null;
            while( ( line = pidBuffer.readLine() ) != null )
            {
                int pos = line.indexOf( pidVal );

                if( pos == -1 )
                {
                    continue;
                }

                String charBefore = line.substring( pos - 1, pos );
                String charAfter = line.substring( pos + len, pos + len + 1 );

                if( ( charBefore.equals( " " ) ) && ( charAfter.equals( " " ) ) )
                {
                    isRunning = true;
                }
            }
        }
        catch( IOException e )
        {
            WebsphereCore.logError( e );
        }

        return isRunning;
    }

    public static boolean isWebsphereRuntime( IRuntime runtime )
    {
        return runtime != null &&
            runtime.getRuntimeType().getId().contains( "com.liferay.ide.eclipse.server.websphere.runtime" ) &&
            getWebsphereRuntime( runtime ) != null;
    }

    public static void killWebsphereProcess( int pid )
    {
        if( !( isWebsphereRunning( pid ) ) )
        {
            return;
        }
        try
        {
            String[] killCommand = null;

            if( CoreUtil.isWindows() )
            {
                killCommand = new String[] { "taskkill", "/F", "/PID", String.valueOf( pid ) };
            }
            else if( CoreUtil.isLinux() )
            {
                killCommand = new String[] { "kill", "-9", String.valueOf( pid ) };
            }
            else
            {
                return;
            }
            Runtime.getRuntime().exec( killCommand );
        }
        catch( IOException e )
        {
            WebsphereCore.logError( e );
        }
    }

    public static List<WebsphereProfile> listProfilesInRegistry( File registryFile ) throws Exception
    {
        List<WebsphereProfile> listProfiles = readProfileRegistry( registryFile );

        return listProfiles;
    }

    private static void processResourceDeltasZip(
        IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries, String deletePrefix,
        String deltaPrefix, boolean adjustGMTOffset ) throws IOException, CoreException
    {
        for( IModuleResourceDelta delta : deltas )
        {
            int deltaKind = delta.getKind();

            IResource deltaResource = (IResource) delta.getModuleResource().getAdapter( IResource.class );

            IProject deltaProject = deltaResource.getProject();

            // IDE-110 IDE-648
            IFolder webappRoot = CoreUtil.getDefaultDocrootFolder( deltaProject );

            IPath deltaPath = null;

            if( webappRoot != null )
            {
                final IPath deltaFullPath = deltaResource.getFullPath();
                final IPath containerFullPath = webappRoot.getFullPath();
                deltaPath = new Path( deltaPrefix + deltaFullPath.makeRelativeTo( containerFullPath ) );

                if( deltaPath != null && deltaPath.segmentCount() > 0 )
                {
                    break;
                }
            }

            if( deltaKind == IModuleResourceDelta.ADDED || deltaKind == IModuleResourceDelta.CHANGED )
            {
                addToZip( deltaPath, deltaResource, zip, adjustGMTOffset );
            }
            else if( deltaKind == IModuleResourceDelta.REMOVED )
            {
                addRemoveProps( deltaPath, deltaResource, zip, deleteEntries, deletePrefix );
            }
            else if( deltaKind == IModuleResourceDelta.NO_CHANGE )
            {
                IModuleResourceDelta[] children = delta.getAffectedChildren();
                processResourceDeltasZip( children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );
            }
        }
    }

    private static Vector<WebsphereProfile> readProfileRegistry( File registryFile ) throws Exception
    {
        if( registryFile != null && !registryFile.exists() )
        {
            return new Vector<WebsphereProfile>();
        }

        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        try
        {
            Document document = documentbuilderfactory.newDocumentBuilder().parse( registryFile );

            NodeList nodelistProfiles = document.getElementsByTagName( "profile" );

            return unmarshallProfilesFromDOM( nodelistProfiles );
        }
        catch( Exception e )
        {
        }
        return null;
    }

    private static String removeArchive( String archive )
    {
        int index = Math.max( archive.lastIndexOf( ".war" ), archive.lastIndexOf( ".jar" ) ); //$NON-NLS-1$ //$NON-NLS-2$

        if( index >= 0 )
        {
            return archive.substring( 0, index + 5 );
        }

        return StringPool.EMPTY;
    }

    public static String resolveVariablesIfNecessary( String wasInstallLocation, String locationPropertyValue )
    {
        WebspherePropertyValueHandler mapVariable = new WebspherePropertyValueHandler( wasInstallLocation );
        String result = mapVariable.convertVariableString( locationPropertyValue );
        return result;
    }

    private static Vector<WebsphereProfile> unmarshallProfilesFromDOM( NodeList nodelistProfiles )
    {
        Vector<WebsphereProfile> vprofiles = new Vector<WebsphereProfile>();

        for( int i = 0; i < nodelistProfiles.getLength(); ++i )
        {
            Node nodeProfileThis = nodelistProfiles.item( i );

            boolean fIsDefault = Boolean.valueOf(
                nodeProfileThis.getAttributes().getNamedItem( "isDefault" ).getNodeValue() ).booleanValue();

            String sName = nodeProfileThis.getAttributes().getNamedItem( "name" ).getNodeValue();

            String sPath = nodeProfileThis.getAttributes().getNamedItem( "path" ).getNodeValue();

            WebsphereProfile profileThis = new WebsphereProfile( sName, new File( sPath ), fIsDefault );

            vprofiles.add( profileThis );
        }

        return vprofiles;
    }

    public static boolean getIsRuntimeExists( IPath path, String targetRuntimeVersion )
    {
        if( ( path == null ) || ( !( path.toFile().exists() ) ) )
        {
            return false;
        }

        boolean result = true;

        try
        {
            IPath wasProductPath = path.append( "/properties/version/WAS.product" );

            if( wasProductPath.toFile().exists() )
            {
                WebsphereServerProductInfoHandler wasInfo =
                    new WebsphereServerProductInfoHandler( wasProductPath.toOSString() );

                if( ( wasInfo != null ) && ( wasInfo.getProductId() != null ) )
                {
                    String id = wasInfo.getProductId();
                    boolean matched = false;

                    for( String ids : WAS_EDITION_IDS )
                    {
                        if( ids.equals( id ) )
                        {
                            matched = true;
                            break;
                        }
                    }

                    if( !( matched ) )
                    {
                        result = false;
                    }

                    if( result )
                    {
                        String version = wasInfo.getReleaseVersion();
                        Version relVersion = new Version( version );

                        if( relVersion != null )
                        {
                            Integer major = Integer.valueOf( relVersion.getMajor() );
                            Integer minor = Integer.valueOf( relVersion.getMinor() );
                            Integer micro = Integer.valueOf( relVersion.getMicro() );
                            String targetRuntimeType =
                                "com.ibm.websphere." + major.toString() + minor.toString() + micro.toString();

                            if( !( targetRuntimeType.equals( targetRuntimeVersion ) ) )
                                result = false;
                        }
                    }
                }
            }
            else
            {
                result = false;
            }
        }
        catch( Exception e )
        {
            result = false;
        }

        return result;
    }
}
