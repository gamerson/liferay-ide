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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class LiferaySDKValidationTests extends ProjectCoreBase
{
    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();

        IPath sdkPath = ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-7.0" );

        if ( sdkPath != null && sdkPath.toFile() != null )
        {
            sdkPath.toFile().delete();
        }
    }

    @Test
    public void testSDKLocationValidation() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewLiferayPluginProjectOp op = newProjectOp( "test-sdk" );

        op.setProjectProvider( "ant" );

        op.execute( new ProgressMonitor() );

        SDK sdk = SDKUtil.getWorkspaceSDK();

        IPath sdkLocation = sdk.getLocation();

        if( sdk != null )
        {
            CoreUtil.getWorkspaceRoot().getProject( sdk.getName() ).delete( false, false, null );
        }

        // set existed project name
        IProject project = getProject( "portlets", "test-sdk-" + getRuntimeVersion() + "-portlet" );
        project.delete( false, false, null );
        op.setSdkLocation( sdkLocation.toOSString() );
        assertTrue( op.validation().message().contains(
            "is not valid because a project already exists at that location." ) );

        op = newProjectOp( "test2-sdk" );

        op.setSdkLocation( "" );
        assertEquals( "This sdk location is empty ", op.validation().message() );

        op.setSdkLocation( sdk.getLocation().getDevice() + "/" );
        assertEquals( "This sdk location is not correct", op.validation().message() );

        // sdk has no build.USERNAME.properties file
        sdkLocation.append( "build." + System.getenv().get( "USERNAME" ) + ".properties" ).toFile().delete();
        sdk.getBuildProperties( true );
        op.setSdkLocation( sdkLocation.toOSString() );
        assertEquals(
            "The app.server.deploy.dir(${sdk.dir}/../bundles/tomcat-7.0.42/webapps) is not absolute path.",
            op.validation().message() );
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-7.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-7.0-ce-m5-20150515112305685.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-7.0/";
    }

}
