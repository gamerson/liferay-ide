/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.alloy.core.model.internal;

import com.liferay.ide.alloy.core.util.AlloyUtil;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Simon Jiang
 */

public final class ProjectActionLabelService extends ValueLabelService
{

    @Override
    public String provide( final String value )
    {
        if( value != null )
        {
           return AlloyUtil.getProjectAction( value );
        }
        return value;
    }


}
