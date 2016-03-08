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

package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewModuleOp;
import com.liferay.ide.project.core.modules.ServiceCommand;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.core.portal.PortalServer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */

public class LiferayServiceModuleOperation extends LiferayPortletModuleOperation
{

    public LiferayServiceModuleOperation( NewModuleOp op )
    {
        super( op );
    }

    @Override
    protected String doSourceCodeOperation() throws CoreException
    {
        String result = "";
        File[] tempateFiles = getTempateFiles( templateName, "java" );

        if( tempateFiles != null )
        {
            for( File file : tempateFiles )
            {
                String content = FileUtil.readContents( file, true );

                String updatePackageContent = StringUtil.replace( content, "_package_", packageName );
                String updateClassName = StringUtil.replace( updatePackageContent, "_CLASSNAME_", className );
                String importLib = StringUtil.replace( updateClassName, "_SERVICE_FULL_", serviceName );
                final int serviceClassStart = serviceName.lastIndexOf( "." );
                String simpleServiceName = serviceName.substring( serviceClassStart + 1 );
                result = StringUtil.replace( importLib, "_SERVICE_SHORT_", simpleServiceName );
            }
        }
        return result;
    }

    @Override
    protected void doNewDependencyOperation() throws CoreException
    {
        List<IFile> projectGradleFiles = new SearchFilesVisitor().searchFiles( project, "build.gradle" );

        if( projectGradleFiles.size() > 0 )
        {
            IFile[] projectGradleFile = projectGradleFiles.toArray( new IFile[0] );
            File projectDenpendencyFile = projectGradleFile[0].getLocation().toFile();

            String newDenpendencyContent = readDenpendencies( projectDenpendencyFile.getAbsoluteFile() );

            if( !newDenpendencyContent.contains( serviceName ) )
            {
                addNewDependenciesOperation( projectDenpendencyFile, serviceName );
            }
        }
    }

    private void addNewDependenciesOperation( File file, String bundleId )
    {
        IServer runningServer = null;
        final IServer[] servers = ServerCore.getServers();

        for( IServer server : servers )
        {
            if( server.getServerState() == IServer.STATE_STARTED &&
                server.getServerType().getId().equals( PortalServer.ID ) )
            {
                runningServer = server;
                break;
            }
        }

        final ServiceCommand serviceCommand = new ServiceCommand( runningServer, bundleId );

        try
        {
            final String[] osgiService = serviceCommand.execute();

            if( osgiService != null )
            {
                setDenpendencies( file, osgiService[0], osgiService[1] );
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Can't update project denpendencies. ", e );
        }
    }

    private void setDenpendencies( File file, String bundleId, String bundleVersion ) throws Exception
    {
        String content = new String( FileUtil.readContents( file, true ) );

        String head = content.substring( 0, content.lastIndexOf( "dependencies" ) );

        String end = content.substring( content.lastIndexOf( "}" ) + 1, content.length() );

        String dependencies = content.substring( content.lastIndexOf( "{" ) + 2, content.lastIndexOf( "}" ) );

        String appended = "\tcompile 'com.liferay:" + bundleId + ":" + bundleVersion + "'\n";

        StringBuilder preNewContent = new StringBuilder();

        preNewContent.append( head );
        preNewContent.append( "dependencies {\n" );
        preNewContent.append( dependencies + appended );
        preNewContent.append( "}" );
        preNewContent.append( end );

        String newContent = preNewContent.toString();

        if( !content.equals( newContent ) )
        {
            FileUtil.writeFileFromStream( file, new ByteArrayInputStream( newContent.getBytes() ) );
        }
    }
}
