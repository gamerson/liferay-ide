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
package com.liferay.ide.project.core.model.internal;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Tao Tao
 * @author Simon Jiang
 */
public abstract class ArtifactVersionDefaultValueService extends DefaultValueService
{
    @Override
    protected String compute()
    {
        String data = null;

        data = getMavenParentPomVersion();

        if( data == null )
        {
            data = "1.0.0-SNAPSHOT";
        }

        return data;
    }

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        attachedListener(listener);
    }


    protected abstract void attachedListener( final Listener listener );

    protected abstract String getMavenParentPomVersion();
}
