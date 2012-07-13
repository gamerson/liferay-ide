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

import com.liferay.ide.eclipse.ui.navigator.NavigatorTreeNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public abstract class AbstractPortletsNode implements NavigatorTreeNode
{

    protected final static Object[] EMPTY = new Object[] {};

    protected final NavigatorTreeNode parent;
    protected final IFile resource;
    protected RootXmlResource rootXmlResource;

    public AbstractPortletsNode( NavigatorTreeNode parent, IFile resource )
        throws ResourceStoreException, CoreException
    {
        this.parent = parent;
        this.resource = resource;

        XmlResourceStore store = new XmlResourceStore( resource.getContents() );

        rootXmlResource = new RootXmlResource( store );

    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.portlet.ui.navigator.LiferayIDENavigatorNode#getResource()
     */
    public final IFile getResource()
    {
        return resource;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.portlet.ui.navigator.LiferayIDENavigatorNode#hasChildren()
     */
    public final boolean hasChildren()
    {
        return ((Object[])getChildren()).length > 0;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.portlet.ui.navigator.LiferayIDENavigatorNode#getParent()
     */
    public final NavigatorTreeNode getParent()
    {
        return this.parent;
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
