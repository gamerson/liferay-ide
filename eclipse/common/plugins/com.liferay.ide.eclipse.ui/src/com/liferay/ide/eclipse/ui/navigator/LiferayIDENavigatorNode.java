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

package com.liferay.ide.eclipse.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author kamesh
 */
public interface LiferayIDENavigatorNode
{

    public LiferayIDENavigatorNode getParent();

    public Object[] getChildren();

    public IFile getResource();

    public boolean hasChildren();

    /**
     * @param portletsNavigatorNode
     * @param liferayPortletsNavigatorNode
     */
    public void addNodes( LiferayIDENavigatorNode... ideNavigatorNodes );

    public IModelElement getModel();

    public void setModel( IModelElement model );

}
