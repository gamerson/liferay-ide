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

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Terry Jia
 */
public abstract class AbstractEditorMarkerResolution implements IMarkerResolution2
{

    public String getDescription()
    {
        return getLabel();
    }

    public Image getImage()
    {
        final URL url = LiferayXMLSearchUI.getDefault().getBundle().getEntry( "/icons/portlet.png" );
        return ImageDescriptor.createFromURL( url ).createImage();
    }

    public void run( IMarker marker )
    {
        promptUser( marker );

        CoreUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );
    }

    protected String getLanguageKey( String markerMessage )
    {
        String languageKey = "";

        int firstCharAt = markerMessage.indexOf( "\"" );
        int secondCharAt = markerMessage.lastIndexOf( "\"" );

        if( firstCharAt > 0 && secondCharAt > 0 )
        {
            languageKey = markerMessage.substring( firstCharAt + 1, secondCharAt );
        }

        return languageKey;
    }

    protected String getLanguageMessage( String languageKey )
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

    protected void openEditor( IFile file ) throws PartInitException
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IDE.openEditor( page, file );
    }

    protected abstract void promptUser( IMarker marker );

}