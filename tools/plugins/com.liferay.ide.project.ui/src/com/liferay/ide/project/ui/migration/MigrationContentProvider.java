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

import blade.migrate.api.MigrationConstants;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Gregory Amerson
 */
public class MigrationContentProvider implements ITreeContentProvider
{

    List<IResource> _resources;
    MPTree _root;

    @Override
    public void dispose()
    {
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement != null && parentElement.equals( _root ) )
        {
            final MPNode commonRoot = _root.getCommonRoot();

            commonRoot.data = commonRoot.incrementalPath;

            if( commonRoot.data.equals( "" ) )
            {
                return commonRoot.childs.toArray();
            }
            else
            {
                return new Object[] { commonRoot };
            }

        }
        else if( parentElement instanceof MPNode )
        {
            MPNode node = (MPNode) parentElement;

            if( node.isLeaf() )
            {
                return new Object[] { CoreUtil.getWorkspace().getRoot().getFile( new Path( node.incrementalPath ) ) };
            }

            else
            {
                final List<Object> children = new ArrayList<>();

                for( MPNode child : node.childs )
                {
                    MPNode collaspeNode = checkIfCollapse( child );
                    if( collaspeNode != null )
                    {
                        children.add( collaspeNode );
                    }
                    else
                    {
                        children.add( child );
                    }
                }

                for( MPNode leaf : node.leafs )
                {
                    children.add( CoreUtil.getWorkspace().getRoot().getFile( new Path( leaf.incrementalPath ) ) );
                }

                return children.toArray();
            }

        }

        return null;
    }

    private MPNode checkIfCollapse( MPNode node )
    {
        MPNode collapseNode = node;

        while( collapseNode.childs.size() == 1 && collapseNode.leafs.size() == 0 )
        {
            collapseNode = collapseNode.childs.get( 0 );

            if( collapseNode.childs.size() == 0 && collapseNode.leafs.size() >= 1 )
            {
                // make the current path to node data
                String data =
                    node.data +
                        collapseNode.incrementalPath.substring(
                            node.incrementalPath.length(), collapseNode.incrementalPath.length() );
                collapseNode.data = data;

                return collapseNode;
            }
        }

        return null;
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return new Object[] { _root };
    }

    private MPTree getFileTree( IMarker[] markers )
    {
        final MPTree tree = new MPTree( new MPNode( "", "" ) );

        for( IMarker marker : markers )
        {
            final IResource resource = marker.getResource();

            if( ! _resources.contains( resource ) )
            {
                _resources.add( resource );
                tree.addElement( resource.getFullPath().toPortableString() );
            }
        }

        return tree;
    }

    @Override
    public Object getParent( Object element )
    {
        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        if( element instanceof MPTree )
        {
            return true;
        }
        else if( element instanceof MPNode )
        {
            MPNode node = (MPNode) element;

            return node.childs.size() > 0 || node.leafs.size() > 0;
        }

        return false;
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        if( newInput instanceof IWorkspaceRoot )
        {
            final IWorkspaceRoot root = (IWorkspaceRoot) newInput;

            _resources = new ArrayList<>();

            try
            {
                final IMarker[] markers =
                    root.findMarkers( MigrationConstants.MARKER_TYPE, true, IResource.DEPTH_INFINITE );

                _root = getFileTree( markers );
            }
            catch( CoreException e )
            {
            }
        }
    }

}
