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

package com.liferay.ide.server.ui.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.ui.LiferayServerUI;

/**
 * @author Eric Min
 */
@SuppressWarnings( "restriction" )
public class OpenDeployedFolderHandler extends AbstractHandler
{
    private static final String VARIABLE_FOLDER = "${selected_resource_parent_loc}";
    private static final String VARIABLE_RESOURCE = "${selected_resource_loc}";
    private static final String VARIABLE_RESOURCE_URI = "${selected_resource_uri}";

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            final Object selected = structuredSelection.getFirstElement();

            if( selected != null )
            {
                final IPath folder = getDeployFolderPath( selected );

                if (folder != null)
                {
                    try
                    {
                        String launchCmd = formShowInSytemExplorerCommand( folder.toFile() );

                        if( Util.isLinux() || Util.isMac() )
                        {
                            Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", launchCmd }, null, folder.toFile() );
                        }
                        else
                        {
                            Runtime.getRuntime().exec( launchCmd, null, folder.toFile() );
                        }
                    }
                    catch( IOException e )
                    {
                        LiferayServerUI.logError( "Unable to execute command", e );
                    }
                }
            }
        }

        return null;
    }

    private String formShowInSytemExplorerCommand( File path ) throws IOException
    {
        String retval = null;

        String command = IDEWorkbenchPlugin.getDefault().getPreferenceStore().getString( "SYSTEM_EXPLORER" );

        if( !CoreUtil.isNullOrEmpty( command ) )
        {
            command = Util.replaceAll( command, VARIABLE_RESOURCE, quotePath( path.getCanonicalPath() ) );
            command = Util.replaceAll( command, VARIABLE_RESOURCE_URI, path.getCanonicalFile().toURI().toString() );

            final File parent = path.getParentFile();

            if( parent != null )
            {
                retval = Util.replaceAll( command, VARIABLE_FOLDER, quotePath( parent.getCanonicalPath() ) );
            }
        }

        return retval;
    }

    private IPath getDeployFolderPath( Object selected )
    {
        IPath retval = null;

        ModuleServer moduleServer = null;

        if( selected != null )
        {
            if( selected instanceof ModuleServer )
            {
                moduleServer = (ModuleServer) selected;
                moduleServer.getModule()[0].getProject();

                final ILiferayServerBehavior liferayServerBehavior =
                    (ILiferayServerBehavior) moduleServer.getServer().loadAdapter( ILiferayServerBehavior.class, null );

                if( liferayServerBehavior != null )
                {
                    retval = liferayServerBehavior.getDeployedPath( moduleServer.getModule() );
                }
            }
        }

        return retval;
    }

    private String quotePath( String path )
    {
        if( Util.isLinux() || Util.isMac() )
        {
            // Quote for usage inside "", man sh, topic QUOTING:
            path = path.replaceAll( "[\"$`]", "\\\\$0" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Windows: Can't quote, since explorer.exe has a very special command line parsing strategy.
        return path;
    }
}