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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public abstract class GroupIdDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String groupId = null;

        groupId =  getMavenParentPomVersion();

        if( groupId == null )
        {
            groupId = getDefaultMavenGroupId();

            if( CoreUtil.isNullOrEmpty( groupId ) )
            {
                groupId = "com.example.plugins";
            }
        }

        return groupId;
    }

    private String getDefaultMavenGroupId()
    {
        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };
        final String defaultMavenGroupId =
            Platform.getPreferencesService().getString(
                ProjectCore.PLUGIN_ID, getPrefDefaultMavenGroupId(), null, prefContexts );
        return defaultMavenGroupId;
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

    protected abstract String getPrefDefaultMavenGroupId();

    protected abstract void attachedListener( final Listener listener );

    protected abstract String getMavenParentPomVersion();
}
