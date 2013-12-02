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

package com.liferay.ide.project.core;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;

/**
 * @author Simon Jiang
 */
public class FacetedProjectValidator implements IFacetedProjectValidator
{
    public static final String LOCATION_TARGETED_SDK = "Targeted SDK";

    public static final String ID_PLUGIN_SDK_NOT_SET = "plugin-sdk-not-set";

    public static final String MSG_PLUGIN_SDK_NOT_SET = Msgs.pluginSDKNotSet;

    /*
     * This method validates the SDK project's primary runtime is set and a liferay runtime, if necessary, more
     * validation jobs will be added into it in the future.
     */
    public void validate( IFacetedProject fproj ) throws CoreException
    {
        final IProject proj = fproj.getProject();

        ProjectUtil.deleteProjectMarkers( proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, getMarkerSourceIds() );

        if( ! SDKUtil.isSDKProject( fproj.getProject() ) )
        {
            ProjectUtil.setProjectMarker(
                proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, IMarker.SEVERITY_ERROR,
                MSG_PLUGIN_SDK_NOT_SET, LOCATION_TARGETED_SDK, ID_PLUGIN_SDK_NOT_SET );
        }
    }

    private Set<String> getMarkerSourceIds()
    {
        Set<String> markerSourceIds = new HashSet<String>();

        markerSourceIds.add( ID_PLUGIN_SDK_NOT_SET );

        return markerSourceIds;
    }

    private static class Msgs extends NLS
    {
        public static String pluginSDKNotSet;

        static
        {
            initializeMessages( FacetedProjectValidator.class.getName(), Msgs.class );
        }
    }

}
