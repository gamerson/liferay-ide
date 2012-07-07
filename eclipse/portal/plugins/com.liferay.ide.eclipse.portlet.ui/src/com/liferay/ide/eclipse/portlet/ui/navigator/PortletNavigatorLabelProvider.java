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
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author kamesh
 */
public class PortletNavigatorLabelProvider extends LabelProvider
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object element )
    {

        if( element instanceof PortletsRootNode )
        {
            return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/liferay.png" ).createImage();
        }
        else if( element instanceof LiferayIDENavigatorNode )
        {
            return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlets_16x16.png" ).createImage();
        }
        else if( element instanceof IPortlet )
        {
            return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlet_16x16.png" ).createImage();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element )
    {
        if( element instanceof PortletsRootNode )
        {
            return "Liferay Portlet Resources";
        }
        else if( element instanceof PortletsNavigatorNode )
        {
            return "Portlets";
        }
        else if( element instanceof LiferayPortletsNavigatorNode )
        {
            return "Liferay Portlets";
        }
        else if( element instanceof IPortlet )
        {
            IPortlet portlet = (IPortlet) element;
            return portlet.getPortletName().getContent();
        }

        return null;
    }
}
