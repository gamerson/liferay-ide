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

package com.liferay.ide.maven.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.MavenProjectBuilder;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.maven.ui.LiferayMavenUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BuildServiceGoalAction extends MavenGoalAction
{

    @Override
    protected String getMavenGoals()
    {
        if( CoreUtil.compareVersions( new Version( plugin.getVersion() ), new Version( "1.0.145" ) ) >= 0 &&
            plugin.getArtifactId().equals( getPluginKey() ) )
        {
            return "service-builder:build";
        }
        else
        {
            return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE;
        }
    }

    @Override
    protected void updateProject( IProject p, IProgressMonitor monitor )
    {
        final MavenProjectBuilder builder = new MavenProjectBuilder( p );

        try
        {
            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( p, monitor );

            builder.refreshSiblingProject( projectFacade, monitor );
        }
        catch( CoreException e )
        {
            LiferayMavenUI.logError( "Unable to refresh sibling project", e );
        }
    }

    @Override
    protected String getPluginKey()
    {
        return ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY;
    }

    @Override
    protected String getMavenGoalName()
    {
        return "build-service";
    }

}
