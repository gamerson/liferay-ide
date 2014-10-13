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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.editor.LiferayPropertiesEditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Terry Jia
 */
public class AddLanguagePropertyMarkerResolution extends AbstractEditorMarkerResolution
{

    private IFile languageFile = null;

    public AddLanguagePropertyMarkerResolution( IFile languageFile )
    {
        this.languageFile = languageFile;
    }

    public String getLabel()
    {
        return "Add the language key to " + languageFile.getName();
    }

    protected void promptUser( final IMarker marker )
    {
        String message = marker.getAttribute( IMarker.MESSAGE, "" );

        if( ( message == null ) || ( languageFile == null ) )
        {
            return;
        }

        InputStream is = null;

        try
        {
            is = languageFile.getContents();

            int firstCharAt = message.indexOf( "\"" );
            int secondCharAt = message.lastIndexOf( "\"" );

            String languageKey = message.substring( firstCharAt + 1, secondCharAt );

            final Properties properties = new Properties();

            properties.load( is );

            if( properties.get( languageKey ) != null )
            {
                is.close();

                return;
            }

            String languageMessage = getLanguageMessage( languageKey );

            String languagePropertyLine = languageKey + "=" + languageMessage;

            String contents = CoreUtil.readStreamToString( languageFile.getContents() );

            StringBuffer contentSb = new StringBuffer();

            contentSb.append( contents );

            if( !contents.endsWith( "\n" ) )
            {
                contentSb.append( "\n" );
            }

            contentSb.append( languagePropertyLine );

            languageFile.setContents(
                new ByteArrayInputStream( contentSb.toString().getBytes( "UTF-8" ) ), IResource.FORCE,
                new NullProgressMonitor() );

            final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            page.openEditor( new FileEditorInput( languageFile ), LiferayPropertiesEditor.ID );

            is.close();
        }
        catch( IOException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        catch( CoreException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
    }

    private String getLanguageMessage( String languageKey )
    {
        String[] words = languageKey.split( "-" );

        StringBuffer sb = new StringBuffer();

        for( int i = 0; i < words.length; i++ )
        {
            String word = words[i];

            if( i == 0 )
            {
                word = word.replaceFirst( word.substring( 0, 1 ), word.substring( 0, 1 ).toUpperCase() );
            }

            sb.append( word );

            sb.append( " " );
        }

        return sb.toString().trim();
    }

}
