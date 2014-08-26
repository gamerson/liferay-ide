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

package com.liferay.ide.project.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Terry Jia
 */
public class LanguagePropertiesCache
{

    private HashMap<String, String> languageMap = new HashMap<String, String>();

    private long lastModified = 0;

    private final String noTranslationMessage = "No Translation Message";

    public HashMap<String, String> getLanguageMap( IFile languageFile )
    {
        if( !languageFile.exists() )
        {
            return null;
        }

        long currentLanguagePropertiesModified = languageFile.getModificationStamp();

        if( currentLanguagePropertiesModified > lastModified )
        {
            lastModified = currentLanguagePropertiesModified;

            loadProperties( languageFile );
        }

        return languageMap;
    }

    private void loadProperties( IFile languageFile )
    {
        Properties languageProperties = new Properties();

        try
        {
            InputStream contents = languageFile.getContents();

            languageProperties.load( contents );

            Enumeration<Object> en = languageProperties.keys();

            HashMap<String,String> newLanguageMap = new HashMap<String, String>();

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

            languageMap = newLanguageMap;

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
