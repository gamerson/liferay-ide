/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.navigator;

import com.liferay.ide.eclipse.ui.navigator.NavigatorTreeNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class PortletNode implements NavigatorTreeNode
{

    protected final static Object[] EMPTY = new Object[] {};

    private final NavigatorTreeNode parent;

    private IModelElement model;

    public PortletNode( NavigatorTreeNode parent )
    {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getParent()
     */
    public NavigatorTreeNode getParent()
    {
        // TODO Auto-generated method stub
        return parent;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getChildren()
     */
    public Object[] getChildren()
    {
        return EMPTY;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getResource()
     */
    public IFile getResource()
    {
        return (IFile) parent.getResource();
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#hasChildren()
     */
    public boolean hasChildren()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#addNodes(com.liferay.ide.eclipse.ui.navigator.
     * LiferayIDENavigatorNode[])
     */
    public void addNodes( NavigatorTreeNode... ideNavigatorNodes )
    {

    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getModel()
     */
    public IModelElement getModel()
    {
        return this.model;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#setModel(org.eclipse.sapphire.modeling.IModelElement
     * )
     */
    public void setModel( IModelElement model )
    {
        this.model = model;
    }

}
