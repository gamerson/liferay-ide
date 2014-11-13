/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.server.ui.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author Terry Jia
 */
public class ServerUIUtil
{

    private static final String EXPLORER = "explorer";
    private static final String FINDER = "open";
    private static final String OTHER = "other";

    public static String detectLinuxSystemCommand()
    {
        String result = executeCommand( "which nautilus" );

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = executeCommand( "which dolphin" );
        }

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = executeCommand( "which thunar" );
        }

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = executeCommand( "which pcmanfm" );
        }

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = executeCommand( "which rox" );
        }

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = executeCommand( "xdg-open" );
        }

        if( CoreUtil.isNullOrEmpty( result ) )
        {
            result = OTHER;
        }

        String[] pathnames = result.split( File.separator );

        return pathnames[pathnames.length - 1];
    }

    public static String executeCommand( String command )
    {
        String result = "";

        try
        {
            Process process = Runtime.getRuntime().exec( command );

            result = FileUtil.readContents( process.getInputStream() );

            result = result.trim();
            result = result.replace( "\n", "" );
            result = result.replace( "\r", "" );
        }
        catch( IOException e )
        {
            LiferayUIPlugin.logError( e );
        }

        return result;
    }

    public static String getSystemExplorerCommand()
    {
        String explorerCommand = "";

        if( CoreUtil.isWindows() )
        {
            explorerCommand = EXPLORER;
        }
        else if( CoreUtil.isLinux() )
        {
            explorerCommand = detectLinuxSystemCommand();
        }
        else if( CoreUtil.isMac() )
        {
            explorerCommand = FINDER;
        }

        return explorerCommand;
    }

    public static void openInExplorer( String explorerCommand, String path ) throws IOException
    {

        if( CoreUtil.isWindows() )
        {
            Runtime.getRuntime().exec( explorerCommand + " \"" + path + "\"" );
        }
        else
        {
            Runtime.getRuntime().exec( new String[] { explorerCommand, path } );
        }

    }

}
