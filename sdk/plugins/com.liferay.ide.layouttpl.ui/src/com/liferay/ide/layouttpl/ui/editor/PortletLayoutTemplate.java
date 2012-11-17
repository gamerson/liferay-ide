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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.ui.LangMessages;

/**
 * @author Gregory Amerson
 */
public class PortletLayoutTemplate
{

    private int numColumns;
    private int[] weights;

    public PortletLayoutTemplate( int numCols, int... weights )
    {
        if( numCols < 1 )
        {
            throw new IllegalArgumentException( LangMessages.PortletLayoutTemplate_number_of_columns_must_be_greater_than_zero );
        }

        if( numCols != weights.length )
        {
            throw new IllegalArgumentException( LangMessages.PortletLayoutTemplate_number_of_weight_args_must_match_number_of_columns );
        }

        this.numColumns = numCols;
        this.weights = weights;
    }

    public int getNumCols()
    {
        return this.numColumns;
    }

    public int[] getWeights()
    {
        return this.weights;
    }

}
