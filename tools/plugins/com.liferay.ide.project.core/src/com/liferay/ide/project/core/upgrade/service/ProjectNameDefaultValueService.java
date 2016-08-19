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
package com.liferay.ide.project.core.upgrade.service;

import org.eclipse.sapphire.InitialValueService;
import org.eclipse.sapphire.Value;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;

/**
 * @author Lovett Li
 */
public class ProjectNameDefaultValueService extends InitialValueService
{

    @Override
    protected void initInitialValueService()
    {
        super.initInitialValueService();
        CodeUpgradeOp op = context( CodeUpgradeOp.class );
        String layout = op.getLayout().content();
        Value<String> projectName = op.getProjectName();

        if( projectName.empty() )
        {
            if( layout.equals( "Upgrade to liferay plugin sdk 7" ) )
            {
                op.setProjectName( "plugins.sdk-7.0" );
            }
            else
            {
                op.setProjectName( "liferay-workspace" );
            }
        }
    }

    @Override
    protected String compute()
    {
        return null;
    }

}
