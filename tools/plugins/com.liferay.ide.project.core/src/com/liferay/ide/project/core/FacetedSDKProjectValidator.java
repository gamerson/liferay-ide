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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class FacetedSDKProjectValidator implements IFacetedProjectValidator
{

    public static final String MARKER_TYPE = "com.liferay.ide.project.core.FacetedSDKProjectMarker"; //$NON-NLS-1$

    public static final String MARKER_LOCATION_TARGETED_RUNTIMES = "Targeted Runtimes"; //$NON-NLS-1$

    public static final String MARKER_ID_PRIMARY_RUNTIME_NOT_SET = "Primary runtime not set Marker"; //$NON-NLS-1$
    public static final String MARKER_ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME =
                                                       "Primary runtime not Liferay runtime Marker"; //$NON-NLS-1$

    public static final String MARKER_MSG_PRIMARY_RUNTIME_NOT_SET = Msgs.primaryRuntimeNotSet;
    public static final String MARKER_MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME = Msgs.primaryRuntimeNotLiferayRuntime;

    /*
     * This method validates the SDK project's primary runtime is set and a liferay runtime, if necessary, more
     * validation jobs will be added into it in the future.
     */
    public void validate( IFacetedProject fproj ) throws CoreException
    {
        deletePreviousMarkers( fproj );

        if( SDKUtil.isSDKProject( fproj.getProject() ) )
        {
            if( fproj.getPrimaryRuntime() == null )
            {
                setMarker(
                    fproj, MARKER_MSG_PRIMARY_RUNTIME_NOT_SET, MARKER_LOCATION_TARGETED_RUNTIMES,
                    MARKER_ID_PRIMARY_RUNTIME_NOT_SET );
            }
            else
            {
                if( !ServerUtil.isLiferayRuntime( (BridgedRuntime) fproj.getPrimaryRuntime() ) )
                {
                    setMarker(
                        fproj, MARKER_MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME, MARKER_LOCATION_TARGETED_RUNTIMES,
                        MARKER_ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME );
                }
            }

        }
    }

    protected void deletePreviousMarkers( IFacetedProject fproj )
    {
        try
        {
            ProjectUtil.deleteFacetedProjectMarkers( fproj, MARKER_TYPE );
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( e );
        }
    }

    protected void setMarker( IFacetedProject fproj, String markerMsg, String markerLocation, String markerSourceId )
    {
        try
        {
            ProjectUtil.setFacetedProjectMarker(
                fproj, MARKER_TYPE, IMarker.SEVERITY_ERROR, markerMsg, markerLocation, markerSourceId );
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( e );
        }
    }

    private static class Msgs extends NLS
    {

        public static String primaryRuntimeNotSet;

        public static String primaryRuntimeNotLiferayRuntime;

        static
        {
            initializeMessages( FacetedSDKProjectValidator.class.getName(), Msgs.class );
        }
    }

}
