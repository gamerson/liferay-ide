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

import com.liferay.ide.core.util.PropertiesUtil;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 */
public class LanguagePropertiesCacheUtil
{

    private static HashMap<String, LanguagePropertiesCache> LanguageProjectMap = null;

    public static HashMap<String, String> getLanguageMapByProject( IProject project )
    {
        IFile languageFile = PropertiesUtil.getDefaultLanguagePropertiesFromProject( project );

        if( ( languageFile == null ) || !languageFile.exists() )
        {
            return null;
        }

        if( LanguageProjectMap == null )
        {
            LanguageProjectMap = new HashMap<String, LanguagePropertiesCache>();
        }

        String projectName = project.getName();

        LanguagePropertiesCache languagePropertiesCache = LanguageProjectMap.get( projectName );

        if( languagePropertiesCache == null )
        {
            languagePropertiesCache = new LanguagePropertiesCache();

            LanguageProjectMap.put( projectName, languagePropertiesCache );
        }

        return languagePropertiesCache.getLanguageMap( languageFile );
    }

}
