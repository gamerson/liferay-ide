/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IMarkerResolution;

import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * 
 * @author Kuo Zhang
 *
 */
public class LanguageFileEncodingNotDefaultResolution implements IMarkerResolution
{

    public void run( IMarker marker )
    {
        if( marker.getResource() instanceof IProject )
        {
            ProjectUtil.encodeLanguageFilesToDefault( (IProject) marker.getResource() );
        }
    }

    public String getLabel()
    {
        return "Encode Language Files to Default (UTF-8).";
    }

}
