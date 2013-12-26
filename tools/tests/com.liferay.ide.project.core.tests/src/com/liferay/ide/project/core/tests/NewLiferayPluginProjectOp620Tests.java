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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayPluginProjectOp620Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return portalBundlesPath.append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return portalBundlesPath.append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }

    protected NewLiferayProfile newLiferayProfile( NewLiferayPluginProjectOp op )
    {
        op.setProjectProvider( "maven" );
        final NewLiferayProfile newLiferayProfile = op.getNewLiferayProfiles().insert();

        return newLiferayProfile;
    }

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        return op;
    }

    protected void removeRuntimes() throws CoreException
    {
        IRuntime[] runtimes = ServerCore.getRuntimes();

        if( runtimes != null )
        {
            for( IRuntime runtime : runtimes )
            {
                runtime.delete();
            }
        }
    }

    @Test
    public void testLocationListener() throws Exception
    {
        super.testLocationListener();
    }

    @Test
    @Ignore
    public void testNewJsfRichfacesProjects() throws Exception
    {
        super.testNewJsfRichfacesProjects();
    }

    @Test
    public void testNewLiferayProfileIdDefaultValue() throws Exception
    {
        final String projectName = "test-new-liferay-profile-dafault-value-service";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        final NewLiferayProfile newLiferayProfile = newLiferayProfile( op );

        assertEquals( "6 2 0", newLiferayProfile.getRuntimeName().content() );
        assertEquals( "6-2-0", newLiferayProfile.getId().content() );

        removeRuntimes();
        assertEquals( "<None>", newLiferayProfile.getRuntimeName().content() );
        assertEquals( null, newLiferayProfile.getId().content() );
    }

    @Test
    public void testNewLiferayProfileIdValidation() throws Exception
    {
        final String projectName = "test-new-liferay-profile-id-validation-service";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        final NewLiferayProfile newLiferayProfile = newLiferayProfile( op );
        final ValidationService vs = newLiferayProfile.getId().service( ValidationService.class );

        removeRuntimes();
        assertEquals( null, newLiferayProfile.getId().content() );

        final String expected1 = "Profile id can not be empty.";
        assertEquals( expected1, vs.validation().message() );
        assertEquals( expected1, newLiferayProfile.getId().validation().message() );

        newLiferayProfile.setId( "" );
        assertEquals( expected1, vs.validation().message() );
        assertEquals( expected1, newLiferayProfile.getId().validation().message() );

        final Set<String> profiles = NewLiferayPluginProjectOpMethods.getPossibleProfileIds( op, false );
        assertNotNull( profiles );

        final String expected2 = "Profile already exists.";

        for( String profile : profiles )
        {
            newLiferayProfile.setId( profile );

            assertEquals( expected2, vs.validation().message() );
            assertEquals( expected2, newLiferayProfile.getId().validation().message() );
        }

        newLiferayProfile.setId( "profile id" );
        final String expected3 = "No spaces are allowed in profile id.";
        assertEquals( expected3, vs.validation().message() );
        assertEquals( expected3, newLiferayProfile.getId().validation().message() );
    }

    @Test
    public void testNewLiferayProfileRuntimeValidation() throws Exception
    {
        final String projectName = "test-new-liferay-profile-runtime-validation";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        final NewLiferayProfile newLiferayProfile = newLiferayProfile( op );
        final ValidationService vs = newLiferayProfile.getRuntimeName().service( ValidationService.class );

        removeRuntimes();
        String expected = "Liferay runtime must be configured.";
        assertEquals( expected, vs.validation().message() );
        assertEquals( expected, newLiferayProfile.getRuntimeName().validation().message() );
    }

    @Test
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        super.testNewProjectCustomLocationPortlet();
    }

    @Test
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        super.testNewProjectCustomLocationWrongSuffix();
    }

    @Test
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        super.testNewSDKProjectCustomLocation();
    }

    @Test
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        super.testNewSDKProjectEclipseWorkspace();
    }

    @Test
    public void testPluginTypeListener() throws Exception
    {
        super.testPluginTypeListener( true );
    }

    @Test
    public void testUseSdkLocationListener() throws Exception
    {
        super.testUseSdkLocationListener();
    }
}
