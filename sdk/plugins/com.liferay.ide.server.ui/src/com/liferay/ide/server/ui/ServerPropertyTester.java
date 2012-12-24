/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.wst.server.core.IServerAttributes;

/**
 * @author Cindy Li
 */
public class ServerPropertyTester extends PropertyTester
{
    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        try
        {
            IServerAttributes server = (IServerAttributes) receiver;
            if( server.getServerType().supportsRemoteHosts() )
            {
                return true;
            }

        }
        catch( Exception e )
        {
            // ignore
        }
        return false;
    }

}
