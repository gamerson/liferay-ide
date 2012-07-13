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
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class PortletResourcesRootNode
{
    private final IProject liferayProject;

    private NavigatorTreeNode[] nodes;

    private IFile portletXmlFile;

    public PortletResourcesRootNode( IProject project )
    {
        this.liferayProject = project;
    }
    
    public IFile getPortletXmlFile()
    {
        return this.portletXmlFile;
    }

    public IProject getProject()
    {
        return this.liferayProject;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#addNodes(com.liferay.ide.eclipse.ui.navigator.
     * LiferayIDENavigatorNode[])
     */
    public void addNodes( NavigatorTreeNode... navigatorNodes )
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
    public NavigatorTreeNode getParent()
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

    public boolean hasChildren( Object element )
    {
        if( this.portletXmlFile != null && this.portletXmlFile.exists() )
        {
            return true;
        }
        
        return false;
    }

}
