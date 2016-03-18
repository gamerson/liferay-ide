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
package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.modules.template.LiferayDSComponentActivatorOperation;
import com.liferay.ide.project.core.modules.template.LiferayDSComponentMvcPortletOperation;
import com.liferay.ide.project.core.modules.template.LiferayDSComponentPortletOperation;
import com.liferay.ide.project.core.modules.template.LiferayDSComponentServiceOperation;
import com.liferay.ide.project.core.modules.template.LiferayDSComponentServiceWrapperOperation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Simon Jiang
 */

public class DSComponentProvider implements IDSComponentProvider
{
    @Override
    public void createNewModule( NewModuleOp op, IProgressMonitor monitor ) throws CoreException
    {
        ILiferayModuleOperation<NewModuleOp> moduleOperation = null;

        final String templateName = op.getComponentTemplateName().content( true );

        if( templateName.equals( "mvcportlet" ) )
        {
            moduleOperation = new LiferayDSComponentMvcPortletOperation( op );
        }
        else if( templateName.equals( "portlet" ) )
        {
            moduleOperation = new LiferayDSComponentPortletOperation( op );
        }
        else if( templateName.equals( "service" ) )
        {
            moduleOperation = new LiferayDSComponentServiceOperation( op );
        }
        else if( templateName.equals( "servicewrapper" )  )
        {
            moduleOperation = new LiferayDSComponentServiceWrapperOperation( op );
        }        
        else if( templateName.equals( "activator" ) )
        {
            moduleOperation = new LiferayDSComponentActivatorOperation( op );
        }

        if( moduleOperation != null )
        {
            moduleOperation.doExecute();
        }
    }

}