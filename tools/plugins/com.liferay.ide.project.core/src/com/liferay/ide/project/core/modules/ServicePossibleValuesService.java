/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.project.core.modules;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ServicePossibleValuesService extends PossibleValuesService
{
    
   
    @Override
    protected void compute( final Set<String> values )
    {
        values.addAll( Arrays.asList( NewLiferayModuleProjectOpMethods.getServices() ) );
    }

}
