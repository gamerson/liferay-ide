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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface PortalServerConstants
{
    IEclipsePreferences _defaultPrefs = DefaultScope.INSTANCE.getNode( LiferayServerCore.PLUGIN_ID );

    String DEFAULT_MEMORY_ARGS = _defaultPrefs.get( "default.memory.args", "-Xmx1024m -XX:MaxPermSize=256m" );

    boolean DEFAULT_USE_DEFAULT_PORTAL_SERVER_SETTING = false;

    String DEFAULT_USERNAME = "test@liferay.com";

    boolean DEFAULT_USE_DEVELOPMENT_SERVER_MODE = false;

}
