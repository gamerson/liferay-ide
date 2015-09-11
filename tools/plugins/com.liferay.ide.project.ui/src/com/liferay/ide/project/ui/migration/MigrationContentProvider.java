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

import org.eclipse.core.resources.IFile;
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

    List<IFile> _files;
    private MXMTree _root;

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        if( newInput instanceof IWorkspaceRoot )
        {
            final IWorkspaceRoot root = (IWorkspaceRoot) newInput;

            _files = new ArrayList<>();

            try
            {
                final IMarker[] markers =
                    root.findMarkers( MigrationConstants.MIGRATION_MARKER_TYPE, true, IResource.DEPTH_INFINITE );

                _root = getFileTree( markers );
            }
            catch( CoreException e )
            {
            }
        }
    }

    private MXMTree getFileTree( IMarker[] markers )
    {
        final MXMTree tree = new MXMTree( new MXMNode( "", "" ) );

        for( IMarker marker : markers )
        {
            final IFile file = (IFile) marker.getResource();

            if( ! _files.contains(  file ) )
            {
                _files.add( file );
                tree.addElement( file.getFullPath().toPortableString() );
            }
        }

        return tree;
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return new Object[] { _root };
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement != null && parentElement.equals( _root ) )
        {

            final MXMNode commonRoot = _root.getCommonRoot();
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
        else if( parentElement instanceof MXMNode )
        {
            MXMNode node = (MXMNode) parentElement;

            if( node.isLeaf() )
            {
                return new Object[] { CoreUtil.getWorkspace().getRoot().getFile( new Path( node.incrementalPath ) ) };
            }
            else
            {
                final List<Object> children = new ArrayList<>();

                children.addAll( node.childs );

                for( MXMNode leaf : node.leafs )
                {
                    children.add( CoreUtil.getWorkspace().getRoot().getFile( new Path( leaf.incrementalPath ) ) );
                }

                return children.toArray();
            }
        }

        return null;
    }

    @Override
    public Object getParent( Object element )
    {
        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        if( element instanceof MXMTree )
        {
            return true;
        }
        else if( element instanceof MXMNode )
        {
            MXMNode node = (MXMNode) element;

            return node.childs.size() > 0 || node.leafs.size() > 0;
        }

        return false;
    }

}
