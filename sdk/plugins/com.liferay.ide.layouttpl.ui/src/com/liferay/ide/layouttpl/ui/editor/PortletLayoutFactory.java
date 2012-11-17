/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.ui.LangMessages;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Greg Amerson
 */
public class PortletLayoutFactory implements CreationFactory
{
    protected int numColumns;
    protected int[] weights;

    public PortletLayoutFactory( int numCols, int... weights )
    {
        if( numCols < 1 )
        {
            throw new IllegalArgumentException( LangMessages.PortletLayoutFactory_number_of_columns_must_be_greater_than_zero );
        }

        if( numCols != weights.length )
        {
            throw new IllegalArgumentException( LangMessages.PortletLayoutFactory_number_of_weight_args_must_match_number_of_columns );
        }

        this.numColumns = numCols;
        this.weights = weights;
    }

    public Object getNewObject()
    {
        PortletLayout newRow = new PortletLayout();

        for( int i = 0; i < numColumns; i++ )
        {
            PortletColumn newColumn = new PortletColumn();
            newColumn.setWeight( weights[i] );

            newRow.addColumn( newColumn );
        }

        return newRow;
    }

    public Object getObjectType()
    {
        return PortletLayout.class;
    }

}
