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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayOsgiModuleProvider;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.gradle.core.modules.LiferayActivatorModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayMvcPortletModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayPortletModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayServiceModuleOperation;
import com.liferay.ide.project.core.NewLiferayOsgiModuleProvider;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;
import com.liferay.ide.project.core.modules.NewModuleOp;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Simon Jiang
 */
public class GradleModuleProvider extends AbstractLiferayOsgiModuleProvider
    implements NewLiferayOsgiModuleProvider<NewModuleOp>
{

    private static final String projectType = "gradle";

    @Override
    public String getProjectType()
    {
        return projectType;
    }

    @Override
    public void createNewModule( NewModuleOp op, IProgressMonitor monitor ) throws CoreException
    {
        ILiferayModuleOperation<NewModuleOp> moduleOperation = null;

        final String templateName = op.getComponentTemplateName().content( true );

        if( templateName.equals( "mvcportlet" ) )
        {
            moduleOperation = new LiferayMvcPortletModuleOperation( op );
        }
        else if( templateName.equals( "portlet" ) )
        {
            moduleOperation = new LiferayPortletModuleOperation( op );
        }
        else if( templateName.equals( "service" ) || ( templateName.equals( "servicewrapper" ) ) )
        {
            moduleOperation = new LiferayServiceModuleOperation( op );
        }
        else if( templateName.equals( "activator" ) )
        {
            moduleOperation = new LiferayActivatorModuleOperation( op );
        }

        if( moduleOperation != null )
        {
            moduleOperation.doExecute();
        }
    }

    public boolean provides( IProject project )
    {
        boolean retval = false;

        try
        {
            if( LiferayNature.hasNature( project ) && GradleProjectNature.INSTANCE.isPresentOn( project ) )
            {
                retval = true;
            }
        }
        catch( Exception e )
        {
        }

        return retval;
    }
}
