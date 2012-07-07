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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ResourceStoreException;

/**
 * @author kamesh
 */
public class LiferayPortletsNavigatorNode extends AbstractPortletsNavigatorNode
{

    private final static Object[] EMPTY = new Object[] {};

    public LiferayPortletsNavigatorNode( LiferayIDENavigatorNode parent, IFile portletXml )
        throws ResourceStoreException,
        CoreException
    {

        super( parent, portletXml );
    }

    public Object[] getChildren()
    {
        return EMPTY;
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode#getModel()
     */
    public IModelElement getModel()
    {
        // TODO this needs to be updated when we have the Liferay Portlet xml editor
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
        // TODO Auto-generated method stub

    }

}
