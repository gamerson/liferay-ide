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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.model.NewLiferayModuleProjectOpMethods;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class ModuleProjectGroupIdDefaultValueService extends GroupIdDefaultValueService
{
    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

    @Override
    protected String getPrefDefaultMavenGroupId()
    {
        return ProjectCore.PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID;
    }

    @Override
    protected void attachedListener( Listener listener )
    {
        op().getLocation().attach( listener );
        op().getProjectName().attach( listener );
    }

    @Override
    protected String getMavenParentPomVersion()
    {
        final Path location = op().getLocation().content();

        if( location != null )
        {
            final NewLiferayModuleProjectOp op = op();
            final IPath parentProjectOsPath = PathBridge.create( location );
            final String projectName = op().getProjectName().content();

            return NewLiferayModuleProjectOpMethods.getMavenParentPomGroupId( op, projectName, parentProjectOsPath );

        }
        return null;
    }
}
