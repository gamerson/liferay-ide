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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Terry Jia
 */
public class LanguagePropertiesMarkerResolutionGenerator implements IMarkerResolutionGenerator2
{

    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        IMarkerResolution[] retval = null;

        if( correctMarker( marker ) )
        {
            final IProject project = marker.getResource().getProject();

            List<IFile> files = PropertiesUtil.getDefaultLanguagePropertiesFromProject( project );

            if( files != null && files.size() > 0 )
            {
                List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

                for( IFile file : files )
                {
                    resolutions.add( new AddLanguagePropertyMarkerResolution( file ) );
                }

                IMarkerResolution[] markerResolutions = new IMarkerResolution[files.size()];

                retval = resolutions.toArray( markerResolutions );
            }
        }

        return retval;
    }

    public boolean hasResolutions( IMarker marker )
    {
        if( correctMarker( marker ) )
        {
            final IProject project = marker.getResource().getProject();

            List<IFile> files = PropertiesUtil.getDefaultLanguagePropertiesFromProject( project );

            if( files != null && files.size() > 0 )
            {
                return true;
            }
        }

        return false;
    }

    protected boolean correctMarker( IMarker marker )
    {
        try
        {
            return LiferayXMLConstants.LIFERAY_JSP_MARKER_WARNING_ID.equals( marker.getType() );
        }
        catch( CoreException e )
        {
        }

        return false;
    }

}
