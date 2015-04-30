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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class LiferayPortalVersionLoader
{

    protected IPath[] extraLibs;
    protected IPath globalDir;

    public LiferayPortalVersionLoader( IPath appServerGlobalDir )
    {
        this.globalDir = appServerGlobalDir;
    }

    protected void addLibs( File libDir, List<URL> libUrlList ) throws MalformedURLException
    {
        if( libDir.exists() )
        {
            final File[] libs = libDir.listFiles
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
                    libUrlList.add( portaLib.toURI().toURL() );
                }
            }
        }
    }

    private Object getMethodValueFromClass( String loadClassName, String methodName)
    {
        Object retval = null;

        try
        {
            final Class<?> classRef = loadClass( loadClassName );
            final Method method = classRef.getMethod( methodName );
            retval = method.invoke( null );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

    @SuppressWarnings( "resource" )
    protected Class<?> loadClass( String className ) throws Exception
    {
        final List<URL> libUrlList = new ArrayList<URL>();

        if ( globalDir != null )
        {
            final File libDir = globalDir.toFile();

            addLibs( libDir, libUrlList );
        }

        final URL[] urls = libUrlList.toArray( new URL[libUrlList.size()] );

        return new URLClassLoader( urls ).loadClass( className );
    }

    public String loadServerInfoFromClass()
    {
        final String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo"; //$NON-NLS-1$
        final String methodName = "getServerInfo"; //$NON-NLS-1$

        return ( String )getMethodValueFromClass( loadClassName, methodName);
    }

    public Version loadVersionFromClass()
    {
        final String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo"; //$NON-NLS-1$
        final String methodName = "getVersion"; //$NON-NLS-1$

        Version retval = null;

        try
        {
            final String versionString = ( String )getMethodValueFromClass( loadClassName, methodName);
            retval = Version.parseVersion( versionString );
        }
        catch( Exception e )
        {
            retval = Version.emptyVersion;
            LiferayServerCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

}
