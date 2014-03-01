/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.alloy.core.model.internal;

import com.liferay.ide.alloy.core.model.AlloyUIUpgradeOp;
import com.liferay.ide.alloy.core.model.ProjectItem;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.osgi.framework.Version;


/**
 * @author Simon Jiang
 */
public class ProjectValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                if( ! event.property().definition().equals( AlloyUIUpgradeOp.PROP_SELECTED_PROJECTS ) )
                {
                    refresh();
                }
            }
        };

        op().attach( listener, "*" ); //$NON-NLS-1$
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if( op().getSelectedProjects().size() < 1 )
        {
            retval = Status.createErrorStatus( "Please select a project to upgrade " );
        }

        ElementList<ProjectItem> projectItems = op().getSelectedProjects();
        for( ProjectItem projectItem : projectItems )
        {
            if( projectItem.getItem().content() != null )
            {
                IProject project = ProjectUtil.getProject( projectItem.getItem().content().toString() );
                final ILiferayProject lProject = LiferayCore.create( project );
                if( lProject != null )
                {
                    final String portalVersion = lProject.getPortalVersion();

                    if( portalVersion != null )
                    {
                        final Version version = new Version( portalVersion );

                        if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                        {
                            retval = Status.createErrorStatus( "Portal version of " + project.getName() + " is greater than " +
                                ILiferayConstants.V620 );
                        }

                    }

                }

            }

        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private AlloyUIUpgradeOp op()
    {
        return context( AlloyUIUpgradeOp.class );
    }

}
