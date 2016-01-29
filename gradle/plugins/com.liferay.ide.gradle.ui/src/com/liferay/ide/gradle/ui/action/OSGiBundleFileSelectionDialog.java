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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.gradle.core.modules.OSGiCustomJSP;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

/**
 * @author Terry Jia
 */
public class OSGiBundleFileSelectionDialog extends ElementTreeSelectionDialog
{

    private static ElementList<OSGiCustomJSP> jsps;

    protected static class FileContentProvider implements ITreeContentProvider
    {

        private final Object[] EMPTY = new Object[0];

        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            return EMPTY;
        }

        public Object[] getElements( Object element )
        {
            Set<String> possibleValues = new HashSet<String>();

            if( element instanceof File )
            {
                JarFile jar = null;

                File file = (File) element;
                if( file.exists() )
                {
                    try
                    {
                        jar = new JarFile( file );
                        Enumeration<JarEntry> enu = jar.entries();
                        while( enu.hasMoreElements() )
                        {
                            JarEntry entry = enu.nextElement();
                            String name = entry.getName();

                            if( name.startsWith( "META-INF/resources/" ) &&
                                ( name.endsWith( ".jsp" ) || name.endsWith( ".jspf" ) ) )
                            {
                                possibleValues.add( name );
                            }
                        }
                    }
                    catch( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            }

            for( OSGiCustomJSP jsp : jsps )
            {
                String jspFile = jsp.getValue().content();

                possibleValues.remove( jspFile );
            }

            return possibleValues.toArray();
        }

        public Object getParent( Object element )
        {
            return null;
        }

        public boolean hasChildren( Object element )
        {
            return false;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    protected static class FileLabelProvider extends LabelProvider
    {

        private final Image IMG_FILE =
            PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FILE );

        public Image getImage( Object element )
        {
            return IMG_FILE;
        }

        public String getText( Object element )
        {
            return element.toString();
        }
    }

    public OSGiBundleFileSelectionDialog( Shell parent, ElementList<OSGiCustomJSP> currentJSPs )
    {
        super( parent, new FileLabelProvider(), new FileContentProvider() );

        jsps = currentJSPs;

        setComparator( new ViewerComparator() );
    }

}
