package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayPortalValueLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public abstract class AbstractPortalBundle implements PortalBundle
{
    
    private static final String CONFIG_TYPE_SERVER = "server"; //$NON-NLS-1$
    private static final String CONFIG_TYPE_VERSION = "version"; //$NON-NLS-1$
    private static final Version MANIFEST_VERSION_REQUIRED = ILiferayConstants.V620;


    protected IPath autoDeployPath;
    protected IPath liferayHome;
    protected IPath modulesPath;
    protected IPath bundlePath;
    protected String version;
    protected int jmxRemotePort;
 
    public AbstractPortalBundle( IPath path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException( "path cannot be null" );
        }

        this.bundlePath = path;
        
        this.liferayHome = bundlePath.append( ".." );
        this.jmxRemotePort = detectJmxRemotePort();

        this.autoDeployPath = this.liferayHome.append( "deploy" );

        this.version = getPortalVersion( this.bundlePath, getPortalDir( bundlePath ) );

        if( this.version != null && this.version.startsWith( "6" ) )
        {
            this.modulesPath = this.liferayHome.append( "data/osgi" );
        }
        else
        {
            this.modulesPath = this.liferayHome.append( "osgi" );
        }
    }
    
    protected abstract int detectJmxRemotePort();
    
    protected abstract IPath getPortalDir( IPath portalDir );
    
    @Override
    public int getJmxRemotePort()
    {
        return this.jmxRemotePort;
    }

    public IPath getAutoDeployPath()
    {
        return this.autoDeployPath;
    }

    public IPath getModulesPath()
    {
        return this.modulesPath;
    }

    public IPath getLiferayHome()
    {
        return this.liferayHome;
    }

    @Override
    public String getVersion()
    {
        return this.version;
    }

    private String getConfigInfoFromCache( String configType, IPath portalDir )
    {
        IPath configInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" ); //$NON-NLS-1$
        }

        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" ); //$NON-NLS-1$
        }

        else
        {
            return null;
        }

        File configInfoFile = configInfoPath.toFile();

        String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );

        Properties properties = new Properties();

        if( configInfoFile.exists() )
        {
            try
            {
                FileInputStream fileInput = new FileInputStream( configInfoFile );
                properties.load( fileInput );
                fileInput.close();
                String configInfo = (String) properties.get( portalDirKey );

                if( !CoreUtil.isNullOrEmpty( configInfo ) )
                {
                    return configInfo;
                }
            }
            catch( Exception e )
            {
            }
        }

        return null;
    }

    private String getPortalVersion( IPath location, IPath portalDir )
    {
        String version = getConfigInfoFromCache( CONFIG_TYPE_VERSION, portalDir );

        if( version == null )
        {
            version = getConfigInfoFromManifest( CONFIG_TYPE_VERSION, portalDir );

            if( version == null )
            {
                final LiferayPortalValueLoader loader = new LiferayPortalValueLoader( location, portalDir );

                final Version loadedVersion = loader.loadVersionFromClass();

                if( loadedVersion != null )
                {
                    version = loadedVersion.toString();
                }
            }

            if( version != null )
            {
                saveConfigInfoIntoCache( CONFIG_TYPE_VERSION, version, portalDir );
            }
        }

        return version;
    }

    private String getConfigInfoFromManifest( String configType, IPath portalDir )
    {
        File implJar = portalDir.append( "/WEB-INF/lib/portal-impl.jar").toFile(); //$NON-NLS-1$

        String version = null;
        String serverInfo = null;

        if( implJar.exists() )
        {
            try
            {
                @SuppressWarnings( "resource" )
                JarFile jar = new JarFile( implJar );

                Manifest manifest = jar.getManifest();

                Attributes attributes = manifest.getMainAttributes();

                version = attributes.getValue( "Liferay-Portal-Version" ); //$NON-NLS-1$
                serverInfo = attributes.getValue( "Liferay-Portal-Server-Info" ); //$NON-NLS-1$

                if( CoreUtil.compareVersions( Version.parseVersion( version ), MANIFEST_VERSION_REQUIRED ) < 0 )
                {
                    version = null;
                    serverInfo = null;
                }
            }
            catch( IOException e )
            {
                LiferayServerCore.logError( e );
            }
        }

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            return version;
        }

        if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            return serverInfo;
        }

        return null;
    }

    private void saveConfigInfoIntoCache( String configType, String configInfo, IPath portalDir )
    {
        IPath versionsInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" ); //$NON-NLS-1$
        }
        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" ); //$NON-NLS-1$
        }

        if( versionsInfoPath != null )
        {
            File versionInfoFile = versionsInfoPath.toFile();

            if( configInfo != null )
            {
                String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );
                Properties properties = new Properties();

                try
                {
                    FileInputStream fileInput = new FileInputStream( versionInfoFile );
                    properties.load( fileInput );
                    fileInput.close();
                }
                catch( FileNotFoundException e )
                {
                    // ignore filenotfound we likely just haven't had a file written yet.
                }
                catch( IOException e )
                {
                    LiferayServerCore.logError( e );
                }

                properties.put( portalDirKey, configInfo );

                try
                {
                    FileOutputStream fileOutput = new FileOutputStream( versionInfoFile );
                    properties.store( fileOutput, StringPool.EMPTY );
                    fileOutput.close();
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }
    }
}
