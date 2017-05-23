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
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.JavaUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class WebsphereRuntime extends PortalRuntime implements IWebsphereRuntime, IWebsphereRuntimeWorkingCopy
{

    public static final int DEFAULT_JMX_PORT = 2099;
    public static final int INVALID_STUB_CODE = 325;

    public static final String RUNTIME_TYPE_ID = "com.liferay.ide.eclipse.server.ee.websphere.runtime.6";

    protected static String filter( String s )
    {
        s = s.replace( '/', '_' );
        s = s.replace( '\\', '_' );
        s = s.replace( '?', '_' );
        s = s.replace( '*', '_' );
        s = s.replace( '<', '_' );
        s = s.replace( '>', '_' );
        return s.replace( ':', '_' );
    }

    private final String UN_SUPPORTED_SDK =
        "The IBM Java SDK represented by the SDKInfo argument is not supported by the WebSphere Application Server traditional installation.";

    private String defaultSDKId;

    private File currentSDKLocation;

    private List<WebsphereSDKInfo> allSdkInfoCache = new ArrayList<WebsphereSDKInfo>();

    public WebsphereRuntime()
    {
        super();
    }

    public void clearCache()
    {
        this.allSdkInfoCache.clear();
        this.currentSDKLocation = null;
        this.defaultSDKId = null;
    }

    public IVMInstall findBundledJRE( WebsphereSDKInfo webspherSDK )
    {
        boolean hasThisVm = false;
        Path sdkPath = new Path( webspherSDK.getLocation() );
        // make sure we don't have an existing JRE that has the same path
        for( IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes() )
        {
            for( IVMInstall vmInstall : vmInstallType.getVMInstalls() )
            {
                if( vmInstall.getInstallLocation().equals( sdkPath.toFile() ) )
                {
                    hasThisVm = true;
                    return vmInstall;
                }
            }
        }

        if( !hasThisVm )
        {
            IVMInstallType installType = JavaRuntime.getVMInstallType( StandardVMType.ID_STANDARD_VM_TYPE );
            VMStandin newVM = new VMStandin( installType, JavaUtil.createUniqueId( installType ) );
            newVM.setInstallLocation( sdkPath.toFile() );
            newVM.setName( webspherSDK.getDisplayName() );

            // make sure the new VM name isn't the same as existing name
            boolean existingVMWithSameName = ServerUtil.isExistingVMName( newVM.getName() );

            int num = 1;

            while( existingVMWithSameName )
            {
                newVM.setName( webspherSDK.getDisplayName() + "(" + ( num++ ) + ")" );
                existingVMWithSameName = ServerUtil.isExistingVMName( newVM.getName() );
            }

            return newVM.convertToRealVM();
        }

        return null;
    }

    public List<WebsphereSDKInfo> getAllSDKInfo()
    {
        if( this.allSdkInfoCache.isEmpty() )
        {
            this.allSdkInfoCache = WebsphereSDKUtilities.getWebsphereSDKInfo( getRuntimeHome().toOSString() );
        }
        return this.allSdkInfoCache;
    }

    @Override
    public IPath getAppServerDeployDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerLibGlobalDir()
    {
        return null;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        return null;
    }

    public WebsphereSDKInfo getCurrentSDKInfo()
    {
        String currentSDKId = getProperty( PROP_WEBSPHERE_CURRENT_SDK, null );
        WebsphereSDKInfo currentSDKInfo;
        if( currentSDKId != null )
        {
            currentSDKInfo = getSDKInfoById( currentSDKId );
        }
        else
        {
            currentSDKInfo = getDefaultSDKInfo();

            if( getRuntimeWorkingCopy() != null )
            {
                setRuntimePropertyString( PROP_WEBSPHERE_CURRENT_SDK, this.defaultSDKId );
            }
        }
        return currentSDKInfo;
    }

    public WebsphereSDKInfo getDefaultSDKInfo()
    {
        if( this.defaultSDKId == null )
        {
            if( !getRuntimeHome().toFile().exists() )
            {
                return null;
            }
            this.defaultSDKId = WebsphereSDKUtilities.getWebsphereDefaultSDKId( getRuntimeHome().toOSString() );
        }
        return getSDKInfoById( this.defaultSDKId );
    }

    public String[] getHookSupportedProperties()
    {
        return null;
    }

    @Override
    public IPath getLiferayHome()
    {
        return null;
    }

    @Override
    public PortalBundle getPortalBundle()
    {
        return null;
    }

    @Override
    public Properties getPortletCategories()
    {
        return null;
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return null;
    }

    public String getProperty( String id, String defaultValue )
    {
        return getAttribute( id, defaultValue );
    }

    protected String getProposedJREName()
    {
        IRuntime runtime = getRuntime();
        return filter( runtime.getName() );
    }

    @Override
    public List<IRuntimeClasspathEntry> getRuntimeClasspathEntries()
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        final IPath libPath = getRuntime().getLocation().append( "lib" );

        Collection<File> listFiles = FileUtils.listFiles( libPath.toFile(), new String[] { "jar" }, true );

        for( File file : listFiles )
        {
            if( file.exists() )
            {
                entries.add( JavaRuntime.newArchiveRuntimeClasspathEntry( new Path( file.getAbsolutePath() ) ) );
            }
        }

        return entries;
    }

    private IPath getRuntimeHome()
    {
        return getRuntime().getLocation();
    }

    public WebsphereSDKInfo getSDKInfo( String sdkId )
    {
        if( ( sdkId == null ) || ( sdkId.equals( "" ) ) )
        {
            return null;
        }

        List<WebsphereSDKInfo> sdkInfoList = getAllSDKInfo();
        for( WebsphereSDKInfo sdkInfo : sdkInfoList )
        {
            String currentSDKId = sdkInfo.getId();
            if( sdkId.equals( currentSDKId ) )
            {
                return sdkInfo;
            }
        }
        return null;
    }

    private WebsphereSDKInfo getSDKInfoById( String sdkId )
    {
        if( sdkId == null )
        {
            return null;
        }

        for( WebsphereSDKInfo aSDKInfo : getAllSDKInfo() )
        {
            if( sdkId.equals( aSDKInfo.getId() ) )
            {
                return aSDKInfo;
            }
        }
        return null;
    }

    protected File getSDKLocation()
    {
        if( this.currentSDKLocation == null )
        {
            WebsphereSDKInfo currentSDKInfo = getCurrentSDKInfo();
            if( currentSDKInfo != null )
            {
                IPath path = new Path( currentSDKInfo.getLocation() );
                this.currentSDKLocation = path.toFile();
            }
        }
        return this.currentSDKLocation;
    }

    public IPath getSourceLocation()
    {
        return null;
    }

    @Override
    public IPath[] getUserLibs()
    {
        return null;
    }

    public IVMInstall getVMInstall()
    {
        if( getVMInstallTypeId() == null )
        {
            return JavaRuntime.getDefaultVMInstall();
        }

        try
        {
            IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( getVMInstallTypeId() );
            IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
            int size = vmInstalls.length;

            String id = getVMInstallId();

            for( int i = 0; i < size; i++ )
            {
                if( id.equals( vmInstalls[i].getId() ) )
                {
                    return vmInstalls[i];
                }
            }
        }
        catch( Exception e )
        {
            // ignore
        }

        return null;
    }

    public boolean hasDuplicatedJREName()
    {
        IVMInstallType type =
            JavaRuntime.getVMInstallType( "org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType" );
        final String proposedName = MessageFormat.format( "{0} JRE", new Object[] { getProposedJREName() } );
        IRuntimeWorkingCopy rc = getRuntimeWorkingCopy();
        if( rc.getOriginal() != null )
        {
            if( ( !( rc.isDirty() ) ) || ( rc.getName().equals( rc.getOriginal().getName() ) ) )
            {
                return false;
            }
        }
        IVMInstall[] installs = type.getVMInstalls();
        if( installs != null )
        {
            for( int i = 0; i < installs.length; ++i )
            {
                if( proposedName.equals( installs[i].getName() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUsingDefaultJRE()
    {
        return getVMInstallTypeId() == null;
    }

    protected boolean isValidName( String name )
    {
        if( ( name.indexOf( "/" ) > 0 ) || ( name.indexOf( "\\" ) > 0 ) )
        {
            return false;
        }

        if( ( name.indexOf( ":" ) > 0 ) || ( name.indexOf( "*" ) > 0 ) )
        {
            return false;
        }

        if( ( name.indexOf( "<" ) > 0 ) || ( name.indexOf( ">" ) > 0 ) )
        {
            return false;
        }

        if( ( name.indexOf( "|" ) > 0 ) || ( name.indexOf( "\"" ) > 0 ) )
        {
            return false;
        }
        return( name.indexOf( "?" ) <= 0 );
    }

    public void setCurrentSDKInfo( WebsphereSDKInfo currentSDKInfo ) throws CoreException
    {
        if( currentSDKInfo == null )
        {
            IStatus websphereSDKErrorStatus = WebsphereCore.createErrorStatus( UN_SUPPORTED_SDK );
            throw new CoreException( websphereSDKErrorStatus );
        }
        String id = currentSDKInfo.getId();
        if( currentSDKInfo.equals( getSDKInfo( id ) ) )
        {
            setRuntimePropertyString( PROP_WEBSPHERE_CURRENT_SDK, id );
        }
        else
        {
            IStatus websphereSDKErrorStatus = WebsphereCore.createErrorStatus( UN_SUPPORTED_SDK );
            throw new CoreException( websphereSDKErrorStatus );
        }
    }

    public void setDefaults( IProgressMonitor monitor )
    {
        // IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
        //
        // getRuntimeWorkingCopy().setLocation(
        // new Path(WebsphereCore.getPreferences().get(PREF_DEFAULT_RUNTIME_LOCATION_PREFIX + type.getId(), "")));
    }

    public void setPortalStubLocation( IPath curPath )
    {
        setRuntimePropertyString( PROP_LIFERAY_RUNTIME_STUB_LOCATION, curPath.toPortableString() );
    }

    public void setPortalStubType( String curStubType )
    {
        setRuntimePropertyString( PROP_LIFERAY_PORTAL_STUB_TYPE, curStubType );
    }

    public void setRuntimePropertyString( String key, String curValue )
    {
        if( ( key == null ) || ( curValue == null ) )
        {
            return;
        }
        String oldValue = getAttribute( key, "" );

        if( curValue.equals( oldValue ) )
        {
            return;
        }

        setAttribute( key, curValue );
    }

    public void setVMInstall( IVMInstall vmInstall )
    {
        if( vmInstall == null )
        {
            setVMInstall( null, null );
        }
        else
        {
            setVMInstall( vmInstall.getVMInstallType().getId(), vmInstall.getId() );
        }
    }

    @Override
    public IStatus validate()
    {
        IStatus status = Status.OK_STATUS;

        if( !status.isOK() )
        {
            return status;
        }

        if( !( isValidName( getRuntime().getName() ) ) )
        {
            status = new Status( IStatus.ERROR, WebsphereCore.PLUGIN_ID, 0, "The name is invalid", null );
            status =
                WebsphereCore.createErrorStatus( "The name is invalid", IWebsphereRuntime.RUNTIME_NAME_STATUS_CODE );
            return status;
        }

        IPath runtimeLocation = getRuntime().getLocation();
        if( runtimeLocation != null && !CoreUtil.isNullOrEmpty( runtimeLocation.toOSString() ) &&
            runtimeLocation != null )
        {
            if( runtimeLocation != null && ( !( WebshpereRuntimeLocatorDelegate.getIsRuntimeExists(
                runtimeLocation, "com.ibm.websphere.855" ) ) ) )
            {
                status = WebsphereCore.createErrorStatus(
                    "Failed to find valid Websphpere runtime.", IWebsphereRuntime.RUNTIME_LOCATION_STATUS_CODE );
                return status;
            }
        }

        IVMInstall vmInstall = getVMInstall();

        if( status.isOK() && vmInstall == null )
        {
            status = WebsphereCore.createErrorStatus(
                "Invalid JDK.  Edit the runtime and change the JDK location.",
                IWebsphereRuntime.RUNTIME_VM_STATUS_CODE );
            return status;
        }

        return status;
    }

}
