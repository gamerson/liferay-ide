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

package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.After;
import org.junit.Test;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.workspace.NewLiferayWorkspaceOp;

/**
 * @author Ashley Yuan
 */
public class NewWorkspaceProjectTests
{

    public static final String customBundleUrl =
        "https://sourceforge.net/projects/lportal/files/Liferay%20Portal/7.0.1%20GA2/liferay-ce-portal-tomcat-7.0-ga2-20160610113014153.zip/download";

    public static final String serverName = "server-name";

    @After
    public void clearWorkspace() throws Exception
    {
        //Util.deleteAllWorkspaceProjects();
    }

    public NewLiferayWorkspaceOp newLiferayWorkspaceOp( final String liferayWorkspaceName ) throws Exception
    {
        final NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceName( liferayWorkspaceName );

        op.setProvisionLiferayBundle( true );

        return op;
    }

    @Test
    public void testNewWorkspaceWithDefaultBundleUrl() throws Exception
    {
        final String liferayWorkspaceName = "testWorkspaceWtihDefaultBundleUrl";

        final NewLiferayWorkspaceOp op = newLiferayWorkspaceOp( liferayWorkspaceName );

        // op.setServerName( serverName );
        // op.setBundleUrl( customBundleUrl );

        /****
         * after running op.execute, it created the liferay workspace, but will stuck or maybe it's still downloading
         * portal even if I already have the portal tomact in my users_home/.liferay/bundles folder
         ****/
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals( status.toString(), Status.createOkStatus().message().toLowerCase(),
            status.message().toLowerCase() );

        //Util.waitForBuildAndValidation();

        IProject workspaceProject = CoreUtil.getWorkspaceRoot().getProject( liferayWorkspaceName );

        // workspaceProject.refreshLocal( IResource.DEPTH_INFINITE, null );

        assertNotNull( workspaceProject );

        // LiferayWorkspaceProjectProvider
    }

}
