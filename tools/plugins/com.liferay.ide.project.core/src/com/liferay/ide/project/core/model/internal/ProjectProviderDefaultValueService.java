/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;
import com.liferay.ide.project.core.LiferayProjectCore;

/**
 * @author Simon Jiang
 */
public class ProjectProviderDefaultValueService extends DefaultValueService
{

    @Override
    protected DefaultValueServiceData compute()
    {
        String retval = null;

        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };
        final String defaultProjectBuildType =
            Platform.getPreferencesService().getString(
                LiferayProjectCore.PLUGIN_ID, LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION, null,
                    prefContexts );

        if( defaultProjectBuildType == null )
        {
            retval = "ant";
        }
        else
        {
            retval = defaultProjectBuildType;
        }

        return new DefaultValueServiceData( retval );
    }

}
