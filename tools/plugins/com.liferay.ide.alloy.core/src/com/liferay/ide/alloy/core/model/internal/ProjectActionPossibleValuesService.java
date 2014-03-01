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

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */

public final class ProjectActionPossibleValuesService extends PossibleValuesService
{

    private final static String[] actions= {"RuntimeUpgrade","MetadataUpgrade","ServicebuilderUpgrade","AlloyUIExecute"};

    @Override
    protected void compute( final Set<String> values )
    {
        for( String action : actions)
        {
            values.add( action );
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();

    }

    public boolean isCaseSensitive()
    {
        return false;
    }

    @Override
    public boolean ordered()
    {
       return true;
    }

    @Override
    public org.eclipse.sapphire.modeling.Status problem( Value<?> value )
    {
        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

}
