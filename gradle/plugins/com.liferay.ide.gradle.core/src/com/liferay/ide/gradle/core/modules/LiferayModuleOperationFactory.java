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
package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.gradle.core.modules.LiferayMvcPortletModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayPortletModuleOperation;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;

/**
 * @author Simon Jiang
 */

public class LiferayModuleOperationFactory
{

    private static LiferayModuleOperationFactory instance = null;

    public static LiferayModuleOperationFactory getInstance()
    {
        if( instance == null )
        {
            instance = new LiferayModuleOperationFactory();
        }

        return instance;
    }

    public ILiferayModuleOperation getModuleOperation( String templateName )
    {
        if( templateName.equals( "mvcportlet" ) )
        {
            return new LiferayMvcPortletModuleOperation();
        }
        else if( templateName.equals( "portlet" ) )
        {
            return new LiferayPortletModuleOperation();
        }
        else if( templateName.equals( "service" ) || ( templateName.equals( "servicewrapper" ) ) )
        {
            return new LiferayServiceModuleOperation();
        }
        else if( templateName.equals( "activator" ) )
        {
            return new LiferayActivatorModuleOperation();
        }
        return null;
    }
}
