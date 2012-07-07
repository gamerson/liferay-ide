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
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorParentNode;

import org.eclipse.core.resources.IProject;

/**
 * @author kamesh
 */
public class PortletsRootNode implements LiferayIDENavigatorParentNode
{

    private final IProject liferayPlugin;

    private LiferayIDENavigatorNode[] nodes;

    public PortletsRootNode( IProject liferayPlugin, LiferayIDENavigatorNode... nodes )
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

    /**
     * @return the nodes
     */
    public LiferayIDENavigatorNode[] getNodes()
    {
        return nodes;
    }

    public IProject getProject()
    {
        return liferayPlugin;
    }

    public void addNodes( LiferayIDENavigatorNode... navigatorNodes )
    {
        if( navigatorNodes != null )
        {
            this.nodes = navigatorNodes;
        }
        else
        {
            this.nodes = new LiferayIDENavigatorNode[0];
        }

    }

    public boolean hasChildren()
    {
        return nodes.length > 0;
    }

}
