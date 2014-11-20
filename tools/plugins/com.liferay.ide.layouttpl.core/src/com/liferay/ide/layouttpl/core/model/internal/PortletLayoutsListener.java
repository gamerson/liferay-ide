/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Kuo Zhang
 *
 */
public class PortletLayoutsListener extends PortletColumnsListener
{
    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        // add a column if a empty row is added
        if( event.property().element() instanceof CanAddPortletLayouts )
        {
            CanAddPortletLayouts layoutContainer = ( CanAddPortletLayouts )event.property().element();

            for( PortletLayoutElement layout : layoutContainer.getPortletLayouts() )
            {
                if( layout.getPortletColumns().size() == 0 )
                {
                    layout.getPortletColumns().insert();
                }
            }
        }

        super.handleTypedEvent( event );
    }

}
