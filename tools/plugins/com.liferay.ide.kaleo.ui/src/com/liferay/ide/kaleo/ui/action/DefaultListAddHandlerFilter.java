/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.action;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;


/**
 * @author Gregory Amerson
 */
public class DefaultListAddHandlerFilter extends SapphireActionHandlerFilter
{

    @Override
    public boolean check( SapphireActionHandler handler )
    {
        return ( !handler.getAction().getId().equals( "Sapphire.Add" ) ) ||
            handler instanceof DefaultListAddActionHandler;
    }

}
