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
 *   Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.navigator;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletResourcesLabelProvider extends LabelProvider
{
    private final Image liferayImage;
    private final Image portletsImage;
    private final Image portletImage;
    private final Image liferayModulesImage;

    public PortletResourcesLabelProvider()
    {
        super();
        
        this.liferayImage =
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/liferay.png" ).createImage();
        this.liferayModulesImage =
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/liferay_modules.png" ).createImage();
        this.portletsImage =
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlets_16x16.png" ).createImage();
        this.portletImage =
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlet_16x16.png" ).createImage();
    }

    @Override
    public void dispose()
    {
        this.imageRegistry.dispose();
    }
    
    @Override
    public Image getImage( Object element )
    {
        if( element instanceof PortletResourcesRootNode )
        {
            return liferayModulesImage;
        }
        else if( element instanceof AbstractPortletsNode )
        {
            return this.imageRegistry.get( PORTLETS );
        }
        else if( element instanceof PortletNode )
        {
            return this.imageRegistry.get( PORTLET );
        }
        
        return null;
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof PortletResourcesRootNode )
        {
            return "Liferay Portal Resources";
        }
        else if( element instanceof PortletsNode )
        {
            return "Portlets";
        }
        else if( element instanceof PortletNode )
        {
            Value<String> label = null;
            PortletNode leaf = (PortletNode) element;

            if( leaf.getModel() != null && leaf.getModel() instanceof IPortlet )
            {
                IPortlet portlet = (IPortlet) leaf.getModel();

                label = portlet.getPortletName();
            }

            return label.getLocalizedText( CapitalizationType.TITLE_STYLE, false );
        }

        return null;
    }
}
