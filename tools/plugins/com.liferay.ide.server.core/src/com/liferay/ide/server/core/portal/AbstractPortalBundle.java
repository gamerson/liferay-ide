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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayPortalVersionLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
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

    private static final String CONFIG_TYPE_SERVER = "server";
    private static final String CONFIG_TYPE_VERSION = "version";
    private static final Version MANIFEST_VERSION_REQUIRED = ILiferayConstants.V700;

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
        this.jmxRemotePort = getDefaultJMXRemotePort();

        this.autoDeployPath = this.liferayHome.append( "deploy" );

        this.version = getPortalVersion( getPortalDir( bundlePath ) );

        this.modulesPath = this.liferayHome.append( "osgi" );
    }

    public void addLibs( IPath libDir, List<IPath> libPathList ) throws MalformedURLException
    {
        if( libDir.toFile().exists() )
        {
            final File[] libs = libDir.toFile().listFiles
            (
                new FilenameFilter()
                {
                    public boolean accept( File dir, String fileName )
                    {
                        return fileName.toLowerCase().endsWith( ".jar" );
                    }
                }
            );

            if( ! CoreUtil.isNullOrEmpty( libs ) )
            {
                for( File portaLib : libs )
                {
                    libPathList.add( libDir.append( portaLib.getName() ) );
                }
            }
        }
    }    

    protected abstract int getDefaultJMXRemotePort();

    protected abstract IPath getPortaGlobalLib();

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
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" );
        }

        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" );
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
            try( FileInputStream fileInput = new FileInputStream( configInfoFile ) )
            {
                properties.load( fileInput );
                String configInfo = (String) properties.get( portalDirKey );

                if( !CoreUtil.isNullOrEmpty( configInfo ) )
                {
                    return configInfo;
                }
            }
            catch(IOException e)
            {
                LiferayServerCore.logError( e );
            }
        }

        return null;
    }

    private String getPortalVersion( IPath portalDir )
    {
        String version = getConfigInfoFromCache( CONFIG_TYPE_VERSION, portalDir );

        if( version == null )
        {
            version = getConfigInfoFromManifest( CONFIG_TYPE_VERSION );

            if( version == null )
            {
                final LiferayPortalVersionLoader loader = new LiferayPortalVersionLoader( getPortaGlobalLib() );

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

    private String getConfigInfoFromManifest( String configType )
    {
        File implJar = getPortaGlobalLib().append( "portal-service.jar" ).toFile();

        String version = null;
        String serverInfo = null;

        if( implJar.exists() )
        {
            try ( JarFile jar = new JarFile( implJar ) )
            {
                Manifest manifest = jar.getManifest();

                Attributes attributes = manifest.getMainAttributes();

                version = attributes.getValue( "Liferay-Portal-Version" );
                serverInfo = attributes.getValue( "Liferay-Portal-Server-Info" );

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
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" );
        }
        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" );
        }

        if( versionsInfoPath != null )
        {
            File versionInfoFile = versionsInfoPath.toFile();

            if( configInfo != null )
            {
                String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );
                Properties properties = new Properties();

                try ( FileInputStream fileInput = new FileInputStream( versionInfoFile ) )
                {
                    properties.load( fileInput );
                }
                catch( Exception e )
                {
                }

                try ( FileOutputStream fileOutput = new FileOutputStream( versionInfoFile ) )
                {
                    properties.put( portalDirKey, configInfo );
                    properties.store( fileOutput, StringPool.EMPTY );
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }
    }
}
