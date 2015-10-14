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

package com.liferay.ide.project.ui.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class MigrationViewTreeUtil
{

    private final String CONTENT_PROVIDER_ID = "com.liferay.ide.project.ui.migration.content";

    private CommonViewer _commonViewer;

    private List<IResource> _treeList;

    public MigrationViewTreeUtil( CommonViewer commonViewer )
    {
        this._commonViewer = commonViewer;
    }

    public int getIndexFromSelection( File file )
    {
        List<IResource> list = getTreeList();

        for( int i = 0; i < list.size(); i++ )
        {
            if( list.get( i ).equals( file ) )
            {
                return i;
            }
        }

        return -1;
    }

    public List<IResource> getTreeList()
    {
        if( _treeList == null || _treeList.size() == 0 )
        {
            final ITreeContentProvider contentProvider =
                _commonViewer.getNavigatorContentService().getContentExtensionById( CONTENT_PROVIDER_ID ).getContentProvider();

            if( contentProvider != null && contentProvider instanceof MigrationContentProvider )
            {
                final MigrationContentProvider mcp = (MigrationContentProvider) contentProvider;

                _treeList = sortTreeList( mcp._resources );
            }
        }

        return _treeList;
    }

    public boolean isFirstElement( File file )
    {
        return getTreeList().get( 0 ).equals( file );
    }

    public boolean isLastElement( File file )
    {
        return getTreeList().get( getTreeList().size() - 1 ).equals( file );
    }

    private List<IResource> sortTreeList( List<IResource> treeList )
    {
        List<IResource> list = new ArrayList<IResource>();

        for( IResource r : treeList )
        {
            if( r instanceof File )
            {
                list.add( r );
            }
        }

        Collections.sort( list, new Comparator<IResource>()
        {

            @Override
            public int compare( IResource r1, IResource r2 )
            {
                String[] s = { r1.getFullPath().toString(), r2.getFullPath().toString() };

                Arrays.sort( s );

                if( s[0].equals( r1.getFullPath().toString() ) )
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        } );

        return list;
    }

}
