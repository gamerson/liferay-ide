/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BaseTests
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;

    protected IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            liferayBundlesPath = new Path( liferayBundlesDir );
        }

        return liferayBundlesPath;
    }

    protected final IFile createFile( final IProject project, final String path ) throws Exception
    {
        return createFile( project, path, new byte[0] );
    }

    protected final IFile createFile( final IProject project, final String path, final byte[] content ) throws Exception
    {
        return createFile( project, path, new ByteArrayInputStream( content ) );
    }

    protected final IFile createFile( final IProject project, final String path, final InputStream content ) throws Exception
    {
        final IFile file = project.getFile( path );
        final IContainer parent = file.getParent();

        if( parent instanceof IFolder )
        {
            createFolder( (IFolder) parent );
        }

        file.create( content, true, null );

        return file;
    }

    protected final void createFolder( final IFolder folder ) throws Exception
    {
        if( !folder.exists() )
        {
            final IContainer parent = folder.getParent();

            if( parent instanceof IFolder )
            {
                createFolder( (IFolder) parent );
            }

            folder.create( true, true, null );
        }
    }

    protected final IFolder createFolder( final IProject project, final String path ) throws Exception
    {
        final IFolder folder = project.getFolder( path );
        createFolder( folder );
        return folder;
    }

    protected final IProject createProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );
        p.create( null );
        p.open( null );

        return p;
    }

    protected final void deleteProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );

        if( p.exists() )
        {
            p.delete( true, null );
        }
    }

    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    protected String getRuntimeVersion()
    {
        return "6.2.0";
    }

    protected Element getElementFromFile( IProject project, IPath filePath, ElementType type ) throws Exception
    {
        final String filePathValue = filePath.toOSString();
        final IFile file = createFile( project, filePathValue, this.getClass().getResourceAsStream( filePathValue ) );

        assertEquals( file.getFullPath().lastSegment(), filePath.lastSegment() );

        final InputStream contents = file.getContents();
        final Element element = type.instantiate( new RootXmlResource( new XmlResourceStore( contents ) ) );

        contents.close();

        return element;
    }

    protected static IProject project( final String name )
    {
        return workspaceRoot().getProject( name );
    }

    protected String stripCarriageReturns( String value )
    {
        return value.replaceAll( "\r", "" );
    }

    protected static IWorkspace workspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    protected static IWorkspaceRoot workspaceRoot()
    {
        return workspace().getRoot();
    }

    protected IRuntime setupRuntime() throws Exception
    {
        assertNotNull(
            "Expected System.getProperty(\"liferay.bundles.dir\") to not be null",
            System.getProperty( "liferay.bundles.dir" ) );

        assertNotNull( "Expected liferayBundlesDir to not be null", liferayBundlesDir );

        assertEquals(
            "Expected liferayBundlesPath to exist: " + getLiferayBundlesPath().toOSString(), true,
            getLiferayBundlesPath().toFile().exists() );

        // Testing liferay runtime start
        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );
        // Testing liferay runtime end

        return runtime;
    }

}
