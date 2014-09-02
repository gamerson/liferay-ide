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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Terry Jia
 */
public class LanguagePropertiesCache
{

    private Reference<HashMap<String, String>> languageMap = new WeakReference<HashMap<String, String>>( null );

    private long lastModified = 0;

    private final String noTranslationMessage = "No Translation Message";

    public Map<String, String> getLanguageMap( IFile languageFile )
    {
        if( !languageFile.exists() )
        {
            return null;
        }

        long currentLanguagePropertiesModified = languageFile.getModificationStamp();

        if( ( currentLanguagePropertiesModified > lastModified ) || ( languageMap.get() == null ) )
        {
            lastModified = currentLanguagePropertiesModified;

            loadProperties( languageFile );
        }

        return languageMap.get();
    }

    private void loadProperties( IFile languageFile )
    {
        Properties languageProperties = new Properties();

        try
        {
            InputStream contents = languageFile.getContents();

            languageProperties.load( contents );

            Enumeration<Object> en = languageProperties.keys();

            HashMap<String, String> newLanguageMap = new HashMap<String, String>();

            while( en.hasMoreElements() )
            {
                String languageKey = String.valueOf( en.nextElement() );

                String languageProperty = languageProperties.getProperty( languageKey );

                if( languageProperty.equals( "" ) )
                {
                    languageProperty = noTranslationMessage;
                }

                newLanguageMap.put( languageKey, languageProperty );
            }

            languageMap = new WeakReference<HashMap<String, String>>( newLanguageMap );

            contents.close();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }
    }

}
