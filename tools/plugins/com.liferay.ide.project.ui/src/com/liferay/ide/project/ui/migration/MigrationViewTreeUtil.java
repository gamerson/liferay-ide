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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Terry Jia
 */
public class MigrationViewTreeUtil
{

    private final String CONTENT_PROVIDER_ID = "com.liferay.ide.project.ui.migration.content";

    private CommonViewer _commonViewer;

    private List<MPNode> _treeList;
    private List<IResource> _treeResources;

    public MigrationViewTreeUtil( CommonViewer commonViewer )
    {
        this._commonViewer = commonViewer;
    }

    public int getIndexFromSelection( IFile file )
    {
        List<MPNode> list = getTreeList();

        for( int i = 0; i < list.size(); i++ )
        {
            if( list.get( i ).equals( file ) )
            {
                return i;
            }
        }

        return -1;
    }

    public List<MPNode> getTreeList()
    {
        if( _treeList == null || _treeList.size() == 0 )
        {
            final ITreeContentProvider contentProvider =
                _commonViewer.getNavigatorContentService().getContentExtensionById( CONTENT_PROVIDER_ID ).getContentProvider();

            if( contentProvider != null && contentProvider instanceof MigrationContentProvider )
            {
                final MigrationContentProvider mcp = (MigrationContentProvider) contentProvider;

                _treeList = new ArrayList<MPNode>();
                fetchTreeList( mcp._root.root, _treeList );
                _treeResources = mcp._resources;
            }
        }

        return _treeList;
    }

    private void fetchTreeList( MPNode treeNode, List<MPNode> list )
    {
        if( treeNode.childs.size() > 0 )
        {
            for( int i = 0; i < treeNode.childs.size(); i++ )
            {
                fetchTreeList( treeNode.childs.get( i ), list );
            }
        }

        if( treeNode.leafs.size() > 0 )
        {
            for( int i = 0; i < treeNode.leafs.size(); i++ )
            {
                MPNode node = treeNode.leafs.get( i );

                if( node.incrementalPath.endsWith( "jsp" ) || node.incrementalPath.endsWith( "jspf" ) ||
                    node.incrementalPath.endsWith( "xml" ) || node.incrementalPath.endsWith( "java" ) ||
                    node.incrementalPath.endsWith( "properties" ) )
                {
                    list.add( node );
                }
            }
        }
    }

    public IResource getNextResource( IFile file )
    {
        String path = "";
        int index = 0;

        for( int i = 0; i < getTreeList().size(); i++ )
        {
            MPNode node = getTreeList().get( i );
            if( file.getFullPath().toString().endsWith( node.incrementalPath ) )
            {
                index = i;
                break;
            }
        }

        if( index < getTreeList().size() - 1 )
        {
            path = getTreeList().get( index + 1 ).incrementalPath;
        }
        else if( index == getTreeList().size() - 1 )
        {
            path = getTreeList().get( index ).incrementalPath;
        }

        for( IResource r : _treeResources )
        {
            if( r.getFullPath().toString().endsWith( path ) )
            {
                return r;
            }
        }
        return null;
    }

    public IResource getUpResource( IFile file )
    {
        String path = "";
        int index = 0;

        for( int i = 0; i < getTreeList().size(); i++ )
        {
            MPNode node = getTreeList().get( i );

            if( file.getFullPath().toString().endsWith( node.incrementalPath ) )
            {
                index = i;
                break;
            }
        }

        if( index > 0 )
        {
            path = getTreeList().get( index - 1 ).incrementalPath;
        }
        else if( index == 0 )
        {
            path = getTreeList().get( index ).incrementalPath;
        }

        for( IResource r : _treeResources )
        {
            if( r.getFullPath().toString().endsWith( path ) )
            {
                return r;
            }
        }

        return null;
    }

    public IResource getFirsttResource()
    {
        if( getTreeList().size() > 0 )
        {
            String path = getTreeList().get( 0 ).incrementalPath;

            for( IResource r : _treeResources )
            {
                if( r.getFullPath().toString().endsWith( path ) )
                {
                    return r;
                }
            }

        }

        return null;
    }
}
