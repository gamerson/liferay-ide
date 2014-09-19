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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class PortalLanguagePropertiesCacheUtil
{

    private static WeakReference<HashMap<String, Properties>> LanguagePortalMapReference =
        new WeakReference<HashMap<String, Properties>>( null );

    public static Properties getPortalLanguageProperties( String runtimeId, IPath appServerPortalDir )
    {
        HashMap<String, Properties> LanguageRuntimeMap = LanguagePortalMapReference.get();

        if( LanguageRuntimeMap == null )
        {
            LanguagePortalMapReference =
                new WeakReference<HashMap<String, Properties>>( new HashMap<String, Properties>() );

            LanguageRuntimeMap = LanguagePortalMapReference.get();
        }

        Properties portalLanguageProperties = null;

        JarFile jar = null;

        InputStream in = null;

        try
        {
            portalLanguageProperties = LanguageRuntimeMap.get( runtimeId );

            if( portalLanguageProperties == null )
            {
                if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
                {
                    jar = new JarFile( appServerPortalDir.append( "WEB-INF/lib/portal-impl.jar" ).toFile() );
                    final ZipEntry lang = jar.getEntry( "content/Language.properties" );

                    portalLanguageProperties = new Properties();

                    in = jar.getInputStream( lang );

                    portalLanguageProperties.load( in );
                }

                LanguageRuntimeMap.put( runtimeId, portalLanguageProperties );
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }
        finally
        {
            try
            {
                if( in != null )
                {
                    in.close();
                }

                if( jar != null )
                {
                    jar.close();
                }
            }
            catch( IOException e )
            {
                LiferayCore.logError( e );
            }
        }

        return portalLanguageProperties;
    }

}
