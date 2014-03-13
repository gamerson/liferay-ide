/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.alloy.core.model.internal;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public final class ProjectLabelService extends ValueLabelService
{

    @Override
    public String provide( final String value )
    {
        if( value != null )
        {
            IProject project = ProjectUtil.getProject( value );

            IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

            ILiferayRuntime liferayRuntime =
                ServerUtil.getLiferayRuntime( (BridgedRuntime) facetedProject.getPrimaryRuntime() );

            if( liferayRuntime != null )
            {
                return project.getName() + " (Portal Version:" + liferayRuntime.getPortalVersion() + ")";
            }

            return project.getName();
        }
        return value;
    }

}
