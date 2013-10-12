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

import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.ui.ServerUIUtil;

import com.liferay.ide.project.core.FacetedSDKProjectValidator;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class FacetedSDKProjectMarkerResolutionGenerator implements IMarkerResolutionGenerator2
{

    public static final String TARGETED_RUNTIMES_PROPERTY_PAGE_ID =
        "org.eclipse.wst.common.project.facet.ui.RuntimesPropertyPage"; //$NON-NLS-1$

    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        IMarkerResolution resolution = null;

        final String markerMsg = getMarkerMsg( marker );

        if( markerMsg.equals( FacetedSDKProjectValidator.PRIMARY_RUNTIME_NOT_SET ) )
        {
            resolution = new primaryRuntimeNotSetResolution();
        }
        else if( markerMsg.equals( FacetedSDKProjectValidator.PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME ) )
        {
            resolution = new primaryRuntimeNotLiferayRuntimeResolution();
        }

        return new IMarkerResolution[] { resolution };
    }

    private String getMarkerMsg( final IMarker marker )
    {
        String markerMsg = null;

        try
        {
            markerMsg = (String) marker.getAttribute( IMarker.MESSAGE );
        }
        catch( CoreException e )
        {
            ProjectUIPlugin.logError( "Marker cannot be found.", e ); //$NON-NLS-1$
        }

        return markerMsg;
    }

    public boolean hasResolutions( IMarker marker )
    {
        return true;
    }

    /*
     * IDE-1179, Quick fix for the project of which primary runtime is not set.
     */
    private final class primaryRuntimeNotSetResolution implements IMarkerResolution
    {

        public String getLabel()
        {
            return Msgs.setPrimaryRuntimeForProject;
        }

        public void run( IMarker marker )
        {
            if( marker.getResource() instanceof IProject )
            {
                final IProject proj = (IProject) marker.getResource();

                final IFacetedProject fproj = ProjectUtil.getFacetedProject( proj );

                /*
                 * Let users set a Liferay server runtime when there is no available one.
                 */
                if( ServerUtil.getAvaiableRuntimes().size() == 0 )
                {
                    boolean openNewServerWizard =
                        MessageDialog.openQuestion( null, null, Msgs.noLiferayRuntimeAvailable );

                    if( openNewServerWizard )
                    {
                        ServerUIUtil.showNewServerWizard( null, null, null, "com.liferay." ); //$NON-NLS-1$
                    }
                }

                /*
                 * Let users confirm when there is only one available Liferay runtime.
                 * 
                 * If the previous judgment block is executed, the size of available targeted runtimes 
                 * will increase to 1.
                 */
                if( ServerUtil.getAvaiableRuntimes().size() == 1 )
                {
                    final Set<IRuntime> availableRuntimes = ServerUtil.getAvaiableRuntimes();

                    String runtimeName = ( (IRuntime) availableRuntimes.toArray()[0] ).getName();

                    boolean setAsPrimary =
                        MessageDialog.openQuestion( null, null, NLS.bind( Msgs.setOnlyRuntimeAsPrimary, runtimeName ) );

                    if( setAsPrimary )
                    {
                        try
                        {
                            fproj.setTargetedRuntimes( availableRuntimes, null );
                            fproj.setPrimaryRuntime( (IRuntime) availableRuntimes.toArray()[0], null );
                        }
                        catch( CoreException e )
                        {
                        }
                    }
                }

                /*
                 * Open the "Targeted Runtimes" property page and let users set a runtime as the primary one when there
                 * are multiple Liferay runtimes available.
                 */
                if( ServerUtil.getAvaiableRuntimes().size() > 1 )
                {
                    boolean openRuntimesProperty =
                        MessageDialog.openQuestion( null, null, Msgs.multipleAvailableRuntimes );

                    if( openRuntimesProperty )
                    {
                        PropertyDialog.createDialogOn( null, TARGETED_RUNTIMES_PROPERTY_PAGE_ID, proj ).open();
                    }
                }
            }
        }

    }

    // The condition does not occur.
    private final class primaryRuntimeNotLiferayRuntimeResolution implements IMarkerResolution
    {

        public String getLabel()
        {
            return "Have not been implemented"; //$NON-NLS-1$
        }

        public void run( IMarker marker )
        {
        }
    }

    private static class Msgs extends NLS
    {

        public static String noLiferayRuntimeAvailable;
        public static String setOnlyRuntimeAsPrimary;
        public static String multipleAvailableRuntimes;
        public static String setPrimaryRuntimeForProject;

        static
        {
            initializeMessages( FacetedSDKProjectMarkerResolutionGenerator.class.getName(), Msgs.class );
        }
    }

}
