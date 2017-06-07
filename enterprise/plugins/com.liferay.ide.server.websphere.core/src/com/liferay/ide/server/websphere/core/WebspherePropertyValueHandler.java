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
package com.liferay.ide.server.websphere.core;

import com.liferay.ide.server.websphere.util.WebsphereUtil;


/**
 * @author Simon Jiang
 */

public class WebspherePropertyValueHandler
{

    protected String wasInstallRoot = null;
    protected String sdkLocationPropertyValue = null;

    protected final String wasInstallRootSymbolicName = "${WAS_INSTALL_ROOT}";
    protected final String wasHomeSymbolicName = "${WAS_HOME}";
    protected final String stubInstallRootSymbolicName = "${STUB_RUNTIME_DIR}";

    public WebspherePropertyValueHandler( String wasInstallRoot )
    {
        this.wasInstallRoot = wasInstallRoot;
    }

    public String convertVariableString( String sdkLocationPropertyValue )
    {
        if( ( sdkLocationPropertyValue == null ) || ( sdkLocationPropertyValue.equals( "" ) ) )
        {
            return null;
        }
        String converted = null;

        if( ( sdkLocationPropertyValue.startsWith( "${WAS_INSTALL_ROOT}" ) ) ||
            ( sdkLocationPropertyValue.startsWith( "${WAS_HOME}" ) ) )
        {
            int offset = "${WAS_INSTALL_ROOT}".length();
            converted = WebsphereUtil.ensureEndingPathSeparator( this.wasInstallRoot, false ) +
                sdkLocationPropertyValue.substring( offset );
        }
        else if( sdkLocationPropertyValue.startsWith( "${STUB_RUNTIME_DIR}" ) )
        {
            int offset = "${STUB_RUNTIME_DIR}".length();
            String runtimeLocation = System.getProperty( "was.runtime" );
            if( runtimeLocation == null )
            {
                return null;
            }

            converted = WebsphereUtil.ensureEndingPathSeparator( runtimeLocation, false ) +
                sdkLocationPropertyValue.substring( offset );
        }
        else
        {
            return sdkLocationPropertyValue;
        }
        return converted;
    }
}
