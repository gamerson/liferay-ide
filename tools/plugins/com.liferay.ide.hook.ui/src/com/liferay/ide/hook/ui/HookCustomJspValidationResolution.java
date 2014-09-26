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

package com.liferay.ide.hook.ui;

import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;

/**
 * @author Simon Jiang
 */
public class HookCustomJspValidationResolution implements IMarkerResolution
{

    public String getLabel()
    {
        return Msgs.disableCustomJspValidation;
    }

    public void run( IMarker marker )
    {
        final IProject project = marker.getResource().getProject();

        IPath customJspPath = HookUtil.getCustomJspPath( project );

        if( customJspPath != null )
        {
            HookUtil.configureJSPSyntaxValidationExclude(
                project, project.getFolder( customJspPath.makeRelativeTo( project.getFullPath() ) ) );
        }
    }

    private static class Msgs extends NLS
    {

        public static String disableCustomJspValidation;

        static
        {
            initializeMessages( HookCustomJspValidationResolution.class.getName(), Msgs.class );
        }
    }
}
