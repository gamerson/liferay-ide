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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.server.ui.navigator.PropertiesFile;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class OpenPropertiesFolderAction extends SelectionProviderAction
{

    protected Shell shell;

    public OpenPropertiesFolderAction( ISelectionProvider sp )
    {
        super( sp, "Open Properties Folder" );
    }

    public OpenPropertiesFolderAction( ISelectionProvider selectionProvider, String text )
    {
        this( null, selectionProvider, text );
    }

    public OpenPropertiesFolderAction( Shell shell, ISelectionProvider selectionProvider, String text )
    {
        super( selectionProvider, text );

        this.shell = shell;

        setEnabled( false );
    }

    public boolean accept( Object node )
    {
        return node instanceof PropertiesFile;
    }

    public Shell getShell()
    {
        return this.shell;
    }

    public void perform( Object entry )
    {
        if( entry instanceof PropertiesFile )
        {
            final PropertiesFile workflowEntry = (PropertiesFile) entry;

            final String path = workflowEntry.getPath();

            final String explorerCommand = ServerUIUtil.getSystemExplorerCommand();

            if( !CoreUtil.isNullOrEmpty( path ) && !CoreUtil.isNullOrEmpty( explorerCommand ) )
            {
                Path filePath = new Path( path );

                try
                {
                    openInExplorer( explorerCommand, filePath.removeLastSegments( 1 ).toOSString() );
                }
                catch( IOException e )
                {
                    LiferayServerUIPlugin.logError( "Error opening properties folder.", e );
                }
            }
        }
    }

    @SuppressWarnings( "rawtypes" )
    public void run()
    {
        Iterator iterator = getStructuredSelection().iterator();

        if( !iterator.hasNext() )
        {
            return;
        }

        Object obj = iterator.next();

        if( accept( obj ) )
        {
            perform( obj );
        }

        selectionChanged( getStructuredSelection() );
    }

    /**
     * Update the enabled state.
     *
     * @param sel
     *            a selection
     */
    @SuppressWarnings( "rawtypes" )
    public void selectionChanged( IStructuredSelection sel )
    {
        if( sel.isEmpty() )
        {
            setEnabled( false );

            return;
        }

        boolean enabled = false;
        Iterator iterator = sel.iterator();

        while( iterator.hasNext() )
        {
            Object obj = iterator.next();

            if( obj instanceof PropertiesFile )
            {
                final PropertiesFile node = (PropertiesFile) obj;

                if( accept( node ) )
                {
                    enabled = true;
                }
            }
            else
            {
                setEnabled( false );

                return;
            }
        }

        setEnabled( enabled );
    }

    protected void openInExplorer( String explorerCommand, String path ) throws IOException
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
