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
package com.liferay.ide.hook.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.hook.core.model.StrutsAction;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesCacheService;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesService;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LiferayHookModelTests extends ProjectCoreBase
{
	
	private static String _bundleUrl = "https://releases-cdn.liferay.com/portal/7.2.0-ga1/liferay-ce-portal-tomcat-7.2.0-ga1-20190531153709761.tar.gz";
	@Before
	public void initLifeayWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
		
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();
		
		op.setWorkspaceName( "NewLiferayWorkspaceHookModelTestsProject" );
		
        IPath workspaceLocation = CoreUtil.getWorkspaceRoot().getLocation();

        op.setUseDefaultLocation( false );
        op.setLocation( workspaceLocation.toPortableString() );

        op.execute( new ProgressMonitor() );
        
        waitForBuildAndValidation();
        
        assertTrue( CoreUtil.getProject( "NewLiferayWorkspaceHookModelTestsProject" ).exists() );
        
        IProject wspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();
        
        assertNotNull( wspaceProject );
        
        ILiferayProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, wspaceProject );
        
        IWorkspaceProjectBuilder workspaceBuilder = workspaceProject.adapt(IWorkspaceProjectBuilder.class);
        
        assertNotNull( workspaceBuilder );
        
        workspaceBuilder.initBundle(wspaceProject, _bundleUrl, new NullProgressMonitor());
	}
	
    @Test
    public void strutsActionPathPossibleValuesCacheService() throws Exception
    {
        if( shouldSkipBundleTests() ) return;
        
        final NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();
        
        op.setProjectName("testPossibleValuesCache");
        
        op.setProjectTemplateName("war-hook");

        Status exStatus =
                NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        
        assertTrue( exStatus.ok() );
        
        IProject hookProject = CoreUtil.getProject( op.getProjectName().content() );

        hookProject.open( new NullProgressMonitor() );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, hookProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );

        final Hook hook =
            Hook6xx.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( hookXml.getContents() ) ) );

        assertNotNull( hook );

        final ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, hookProject );
        final ILiferayPortal portal = liferayProject.adapt( ILiferayPortal.class );

        final IPath strutsConfigPath = portal.getAppServerPortalDir().append( "WEB-INF/struts-config.xml" );

        final StrutsAction strutsAction = hook.getStrutsActions().insert();

        final Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

        final TreeSet<String> vals1 =
            strutsActionPath.service( StrutsActionPathPossibleValuesCacheService.class ).getPossibleValuesForPath(
                strutsConfigPath );

        final TreeSet<String> vals2 =
            strutsActionPath.service( StrutsActionPathPossibleValuesCacheService.class ).getPossibleValuesForPath(
                strutsConfigPath );

        assertTrue( vals1 == vals2 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void strutsActionPathPossibleValuesService() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();
        
        op.setProjectName("testPossibleValues");
        
        op.setProjectTemplateName("war-hook");

        Status exStatus =
                NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        
        assertTrue( exStatus.ok() );
        
        IProject hookProject = CoreUtil.getProject( op.getProjectName().content() );

        hookProject.open( new NullProgressMonitor() );        
        
        final IFolder webappRoot = LiferayCore.create( IWebProject.class, hookProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );

        final XmlResourceStore store = new XmlResourceStore( hookXml.getContents() )
        {
            public <A> A adapt( Class<A> adapterType )
            {
                if( IProject.class.equals( adapterType ) )
                {
                    return adapterType.cast( hookProject );
                }

                return super.adapt( adapterType );
            }
        };

        final Hook hook = Hook6xx.TYPE.instantiate( new RootXmlResource( store ) );

        assertNotNull( hook );

        final StrutsAction strutsAction = hook.getStrutsActions().insert();

        final Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

        final Set<String> values = strutsActionPath.service( StrutsActionPathPossibleValuesService.class ).values();

        assertNotNull( values );

        assertTrue( values.size() > 10 );
    }
}
