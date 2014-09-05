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

import com.liferay.ide.layouttpl.core.model.LayoutTpl;
import com.liferay.ide.layouttpl.core.model.PortletColumn;
import com.liferay.ide.layouttpl.core.model.PortletLayout;

import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnsWeightSumValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final PortletLayout portletLayout = context( PortletLayout.class );
        final Version version = portletLayout.nearest( LayoutTpl.class ).getVersion().content();

        int currentWeightSum = 0;
        int weightSum = 0;

        if( version.compareTo( new Version( "6.2" ) ) >=0  )
        {
            weightSum = 12;
        }
        else
        {
            weightSum = 100;
        }

        for( PortletColumn column : portletLayout.getPortletColumns() )
        {
            currentWeightSum += column.getWeight().content().intValue();
        }

        if( currentWeightSum != weightSum )
        {
            retval = Status.createErrorStatus( " The sum of weight of columns should be: " + weightSum );
        }

        return retval;
    }

}
