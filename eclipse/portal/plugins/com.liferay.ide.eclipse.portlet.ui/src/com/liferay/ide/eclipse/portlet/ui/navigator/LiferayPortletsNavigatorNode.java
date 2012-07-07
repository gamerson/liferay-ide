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

import com.liferay.ide.eclipse.ui.navigator.AbstractPortletNavigatorNode;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorParentNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.ResourceStoreException;

/**
 * @author kamesh
 */
public class LiferayPortletsNavigatorNode extends AbstractPortletNavigatorNode
{

    private final static Object[] EMPTY = new Object[] {};

    public LiferayPortletsNavigatorNode( LiferayIDENavigatorParentNode parent, IFile portletXml ) throws ResourceStoreException,
        CoreException
    {

        super( parent, portletXml );
    }

    public Object[] getChildren()
    {
        return EMPTY;
    }

}
