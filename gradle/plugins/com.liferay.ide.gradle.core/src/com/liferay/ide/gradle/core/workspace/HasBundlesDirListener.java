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
package com.liferay.ide.gradle.core.workspace;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Andy Wu
 */
public class HasBundlesDirListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        boolean hasBundles = op( event ).getHasBundlesDir().content();

        if( hasBundles )
        {
            op( event ).setProvisionLiferayBundle( true );
        }
        else
        {
            op( event ).setProvisionLiferayBundle( false );
        }
    }

    protected ImportLiferayWorkspaceOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( ImportLiferayWorkspaceOp.class );
    }


}
