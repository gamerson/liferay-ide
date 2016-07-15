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

package com.liferay.ide.layouttpl.core.model.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.DefaultValueService;
import org.osgi.framework.Version;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

/**
 * @author Joye Luo
 */
public class IsSDK62DefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        try
        {
            final SDK sdk = SDKUtil.getWorkspaceSDK();

            if( sdk != null )
            {
                final Version workSpaceSDKVersion = new Version( sdk.getVersion() );
                final Version sdk62 = ILiferayConstants.V620;

                if( CoreUtil.compareVersions( workSpaceSDKVersion, sdk62 ) == 0 )
                {
                    return "true";
                }
            }
        }
        catch( CoreException e )
        {
        }

        return "false";
    }
}
