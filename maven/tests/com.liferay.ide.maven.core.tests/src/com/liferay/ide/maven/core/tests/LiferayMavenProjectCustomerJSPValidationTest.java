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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.LiferayMavenCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.RequireMavenExecutionContext;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
@RequireMavenExecutionContext
public class LiferayMavenProjectCustomerJSPValidationTest extends AbstractMavenProjectTestCase
{

    @Test
    public void testNoCustomerJspValidationConfigured() throws Exception
    {

        final IEclipsePreferences defaultPrefs = LiferayMavenCore.getDefaultPrefs();

        defaultPrefs.putBoolean( LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION, false );

        IProject project = importProject( "projects/configurators/noCustomJspValidation/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertTrue( CoreUtil.isLiferayProject( project ) );

        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        projectConfigurationManager.updateProjectConfiguration(
            new MavenUpdateRequest( project, mavenConfiguration.isOffline(), true ), monitor );

        waitForJobsToComplete();

        IMarker[] markers =
            project.findMarkers( "org.eclipse.jst.jsp.core.validationMarker", true, IResource.DEPTH_INFINITE );

        assertEquals( true, markers.length > 0 );

    }

    @Test
    public void testCustomerJspValidationConfigured() throws Exception
    {

        final IEclipsePreferences defaultPrefs = LiferayMavenCore.getDefaultPrefs();

        defaultPrefs.putBoolean( LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION, true );

        IProject project = importProject( "projects/configurators/hasCustomJspValidation/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertTrue( CoreUtil.isLiferayProject( project ) );
        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        projectConfigurationManager.updateProjectConfiguration(
            new MavenUpdateRequest( project, mavenConfiguration.isOffline(), true ), monitor );

        waitForJobsToComplete();

        IMarker[] markers =
            project.findMarkers( "org.eclipse.jst.jsp.core.validationMarker", true, IResource.DEPTH_INFINITE );

        assertEquals( true, markers.length == 0 );

    }
}
