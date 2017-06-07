/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.websphere.core;

import com.liferay.ide.server.websphere.util.WebsphereUtil;

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
