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
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Simon Jiang
 */
public class NewLiferayModuleProjectOpMethods
{
    public static final Status execute( final NewLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayModuleProjectOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static String getMavenParentPomGroupId( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( ! groupId.isEmpty() )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !version.isEmpty() )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    public static void updateLocation( final NewLiferayModuleProjectOp op, final Path baseLocation )
    {
        op.setLocation( baseLocation );
    }

    public static void addProperties( File dest, List<String> properties ) throws Exception
    {

        if( properties == null || properties.size() < 1 )
        {
            return;
        }

        String content = new String( FileUtil.readContents( dest, true ) );

        String fontString = content.substring( 0, content.indexOf( "property" ) );

        String endString = content.substring( content.indexOf( "}," ) + 2 );

        String property = content.substring( content.indexOf( "property" ), content.indexOf( "}," ) );

        property = property.substring( property.indexOf( "{" ) + 1 );

        StringBuilder sb = new StringBuilder();

        sb.append( "property = {\n" );

        if( !CoreUtil.isNullOrEmpty( property ) )
        {
            property = property.substring( 1 );
            property = property.substring( 0, property.lastIndexOf( "\t" ) );
            property += ",\t";
            sb.append( property );
        }

        for( String str : properties )
        {
            sb.append( "\t\t\"" + str + "\",\t" );
        }

        sb.deleteCharAt( sb.toString().length() - 2 );

        sb.append( "\t}," );

        StringBuilder all = new StringBuilder();

        all.append( fontString );
        all.append( sb.toString() );
        all.append( endString );

        String newContent = all.toString();

        if( !content.equals( newContent ) )
        {
            FileUtil.writeFileFromStream( dest, new ByteArrayInputStream( newContent.getBytes() ) );
        }
    }
}
