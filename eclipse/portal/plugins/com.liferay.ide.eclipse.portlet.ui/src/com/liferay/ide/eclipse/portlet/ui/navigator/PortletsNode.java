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

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.ui.navigator.NavigatorTreeNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ResourceStoreException;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class PortletsNode extends AbstractPortletsNode
{

    private final ModelElementType portletAppModelElementType = IPortletApp.TYPE;

    private final IPortletApp portletApp;

    public PortletsNode( NavigatorTreeNode parent, IFile portletXmlPath ) throws ResourceStoreException,
        CoreException
    {
        super( parent, portletXmlPath );

        portletApp = portletAppModelElementType.instantiate( rootXmlResource );
    }

    public PortletsNode( PortletResourcesRootNode rootNode, IFile portletXmlFile ) throws ResourceStoreException, CoreException
    {
        this( (NavigatorTreeNode) null, (IFile)null );
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.portlet.ui.navigator.AbstractPortletsNavigatorNode#getModel()
     */
    @Override
    public IModelElement getModel()
    {
        return portletApp;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.liferay.ide.eclipse.portlet.ui.navigator.AbstractPortletsNavigatorNode#setModel(org.eclipse.sapphire.modeling
     * .IModelElement)
     */
    @Override
    public void setModel( IModelElement model )
    {

    }

    public Object[] getChildren()
    {
        if( this.portletApp != null )
        {
            ModelElementList<IPortlet> portlets = portletApp.getPortlets();

            PortletNode[] portletNodes = new PortletNode[portlets.size()];

            int i = 0;
            for( IPortlet iPortlet : portlets )
            {
                PortletNode portletNode = new PortletNode( this );
                portletNode.setModel( iPortlet );
                portletNodes[i] = portletNode;
                i++;
            }

            return portletNodes;
        }
        return EMPTY;
    }

}
