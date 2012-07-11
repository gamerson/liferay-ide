/*******************************************************************************
 *    Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *   
 *    This library is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by the Free
 *    Software Foundation; either version 2.1 of the License, or (at your option)
 *    any later version.
 *  
 *    This library is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *    FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *    details.
 *   
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.navigator;

import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author kamesh
 */
public class PortletResourcesRootNode implements LiferayIDENavigatorNode
{

    private final IProject liferayPlugin;

    private LiferayIDENavigatorNode[] nodes;

    public PortletResourcesRootNode( IProject liferayPlugin, LiferayIDENavigatorNode... nodes )
    {
        this.liferayPlugin = liferayPlugin;
        if( nodes != null )
        {
            this.nodes = nodes;
        }
        else
        {
            this.nodes = new LiferayIDENavigatorNode[0];
        }

    }

    public IProject getProject()
    {
        return liferayPlugin;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#addNodes(com.liferay.ide.eclipse.ui.navigator.
     * LiferayIDENavigatorNode[])
     */
    public void addNodes( LiferayIDENavigatorNode... navigatorNodes )
    {
        if( navigatorNodes != null )
        {
            this.nodes = navigatorNodes;
        }
        else
        {
            this.nodes = new AbstractPortletsNode[0];
        }

    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#hasChildren()
     */
    public boolean hasChildren()
    {
        return nodes.length > 0;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getParent()
     */
    public LiferayIDENavigatorNode getParent()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getChildren()
     */
    public Object[] getChildren()
    {
        return this.nodes;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getResource()
     */
    public IFile getResource()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getModel()
     */
    public IModelElement getModel()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#setModel(org.eclipse.sapphire.modeling.IModelElement
     * )
     */
    public void setModel( IModelElement model )
    {
    }

}
