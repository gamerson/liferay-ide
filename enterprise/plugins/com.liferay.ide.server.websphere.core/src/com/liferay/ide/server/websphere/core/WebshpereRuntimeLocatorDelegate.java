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

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.osgi.framework.Version;

public class WebshpereRuntimeLocatorDelegate extends RuntimeLocatorDelegate
{

    public static final String[] WAS_EDITION_IDS = { "BASE", "ND", "EXPRESS", "embeddedEXPRESS", "NDDMZ" };
    private static final String[] WAS_DIRS = { "java", "lib", "properties", "bin" };

    public void searchForRuntimes(
        IPath path, RuntimeLocatorDelegate.IRuntimeSearchListener listener, IProgressMonitor monitor )
    {
        if( path != null )
        {
            searchDir( listener, path.toFile(), 4, monitor );
        }
        else
        {
            File[] files = File.listRoots();

            if( files != null )
            {
                int size = files.length;
                int work = 100 / size;
                int workLeft = 100 - ( work * size );

                for( int i = 0; i < size; ++i )
                {
                    if( monitor.isCanceled() )
                    {
                        return;
                    }

                    if( ( files[i] != null ) && ( files[i].isDirectory() ) )
                    {
                        searchDir( listener, files[i], 4, monitor );
                    }
                    monitor.worked( work );
                }
                monitor.worked( workLeft );
            }
            else
            {
                monitor.worked( 100 );
            }
        }
    }

    protected void searchDir(
        RuntimeLocatorDelegate.IRuntimeSearchListener listener, File dir, int depth, IProgressMonitor monitor )
    {
        File[] files = dir.listFiles( new FileFilter()
        {

            public boolean accept( File file )
            {
                return file.isDirectory();
            }
        } );

        if( files != null )
        {
            if( mightBeWASRuntime( files ) )
            {
                IRuntimeWorkingCopy runtime = getRuntimeFromDir( dir, files );

                if( runtime != null )
                {
                    listener.runtimeFound( runtime );
                    return;
                }
            }
            else if( depth > 0 )
            {
                int size = files.length;

                for( int i = 0; i < size; ++i )
                {
                    if( monitor.isCanceled() )
                    {
                        return;
                    }
                    searchDir( listener, files[i], depth - 1, monitor );
                }
            }
        }
    }

    protected boolean mightBeWASRuntime( File[] files )
    {
        if( ( files == null ) || ( files.length < 15 ) || ( files.length > 45 ) )
        {
            return false;
        }
        int count = 0;

        for( int i = 0; i < WAS_DIRS.length; ++i )
        {
            if( containsFile( files, WAS_DIRS[i] ) )
            {
                ++count;
            }
        }
        return( count == WAS_DIRS.length );
    }

    protected boolean containsFile( File[] files, String s )
    {
        if( ( files == null ) || ( s == null ) )
        {
            return false;
        }
        int size = files.length;

        for( int i = 0; i < size; ++i )
        {
            if( s.equals( files[i].getName() ) )
            {
                return true;
            }
        }
        return false;
    }

    public static IPath getRuntimeStubProfileLocation( IPath path )
    {
        if( path == null )
        {
            return null;
        }
        IPath profilePath = path.append( "profiles" );
        boolean isStubProfileFound = false;
        try
        {
            File fp = new File( profilePath.toOSString() );

            if( ( fp.exists() ) && ( fp.isDirectory() ) )
            {
                File[] files = fp.listFiles();

                if( files != null )
                {
                    for( int i = 0; ( !( isStubProfileFound ) ) && ( i < files.length ); ++i )
                    {
                        File curFile = files[i];

                        if( ( curFile == null ) || ( !( curFile.isDirectory() ) ) )
                        {
                            continue;
                        }
                        File configF = new File( new Path( curFile.getPath() ).append( "config" ).toOSString() );

                        if( ( !( configF.exists() ) ) || ( !( configF.isDirectory() ) ) )
                        {
                            continue;
                        }
                        profilePath = profilePath.append( curFile.getName() );
                        isStubProfileFound = true;
                    }

                }
                else
                    isStubProfileFound = false;
            }
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return( ( isStubProfileFound ) ? profilePath : null );
    }

    protected File getPropertiesFile( File path )
    {
        StringBuffer absolutePath = new StringBuffer( path.getAbsolutePath() );
        absolutePath.append( File.separator );
        absolutePath.append( "properties" );
        absolutePath.append( File.separator );
        absolutePath.append( "version" );
        absolutePath.append( File.separator );
        absolutePath.append( "WAS.product" );

        File versionFile = new File( absolutePath.toString() );

        if( versionFile.exists() )
        {
            return versionFile;
        }
        return null;
    }

    protected String getVersionsFrom( String versionContents )
    {
        Pattern p = Pattern.compile( "(\\p{Digit}*)\\.(\\p{Digit}*)(.*)" );
        Matcher m = p.matcher( versionContents );
        int majorVersion = 0;
        int minorVersion = 0;

        if( m.find() )
        {
            try
            {
                majorVersion = Integer.parseInt( m.group( 1 ) );
                minorVersion = Integer.parseInt( m.group( 2 ) );
                return WASVersion.getRuntimeVersionFullName( majorVersion, minorVersion );
            }
            catch( NumberFormatException e )
            {
                WebsphereCore.logError( e );
            }
        }

        return null;
    }

    public String getWASRuntimeVersion( File path )
    {
        File versionFile = getPropertiesFile( path );

        if( versionFile != null )
        {
            try
            {
                WebsphereServerProductInfoHandler spInfoHandler =
                    new WebsphereServerProductInfoHandler( versionFile.toString() );
                return getVersionsFrom( spInfoHandler.getReleaseVersion() );
            }
            catch( Exception e )
            {
                WebsphereCore.logError( e );
            }
        }
        return null;
    }

    protected IRuntimeWorkingCopy getRuntimeFromDir( File dir, File[] files )
    {
        if( !( containsFile( files, "cloudscape" ) ) )
        {
            return getRuntimeFromDir( dir, getWASRuntimeVersion( dir ) );
        }
        return getRuntimeFromDir( dir, "com.ibm.ws.ast.st.runtime.v70" );
    }

    protected IRuntimeWorkingCopy getRuntimeFromDir( File dir, String runtimeTypeId )
    {
        try
        {
            IRuntimeType runtimeType = ServerCore.findRuntimeType( runtimeTypeId );
            IRuntimeWorkingCopy runtime = runtimeType.createRuntime( runtimeType.getName(), null );
            IWebsphereRuntime wasRuntime = (IWebsphereRuntime) runtime.loadAdapter( WebsphereRuntime.class, null );

            if( ( wasRuntime != null ) && ( ( (WebsphereRuntime) wasRuntime ).hasDuplicatedJREName() ) )
            {
                ServerUtil.setRuntimeDefaultName( runtime, 2 );
            }
            else
            {
                ServerUtil.setRuntimeDefaultName( runtime );
            }

            runtime.setLocation( new Path( dir.getAbsolutePath() ) );
            IStatus status = runtime.validate( null );

            if( ( status == null ) || ( status.isOK() ) )
            {
                return runtime;
            }
        }
        catch( Exception e )
        {
        }
        return null;
    }

    static enum WASVersion
    {
            WAS_V_85;

        private String fullName;

        public String toString()
        {
            return this.fullName;
        }

        static String getRuntimeVersionFullName( int majorVersion, int minorVersion )
        {
            switch( majorVersion )
            {
            case 8:
                switch( minorVersion )
                {
                case 5:
                    return WAS_V_85.toString();
                }
                break;
            }
            return null;
        }
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
