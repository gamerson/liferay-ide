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
package com.liferay.ide.alloy.core.util;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;


/**
 * @author Simon Jiang
 */

public class AlloyUtil
{

    private final static Map<String,String> actionMaps = new  HashMap<String,String>();
    static
    {
        actionMaps.put( "RuntimeUpgrade", "Update targeted runtime to setting" );
        actionMaps.put( "MetadataUpgrade", "Update all deployment descriptor metadata" );
        actionMaps.put( "ServicebuilderUpgrade", "Rebuild Services for service-builder projects" );
        actionMaps.put( "AlloyUIExecute", "Run Liferay Alloy UI Upgrade tool" );
    }

    public static IProject[] getAllLiferaySDKProject()
    {
        final List<IProject> sdkProjects = new ArrayList<IProject>();

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for (IProject project : projects)
        {
            if ( ProjectUtil.isLiferayFacetedProject( project ) )
            {
                if (SDKUtil.isSDKProject( project ) )
                {
                    sdkProjects.add( project );
                }
            }
        }

        return sdkProjects.toArray( new IProject[sdkProjects.size()]);
    }


    public static void addLiferaySDKProject(IProject project, List<IProject> projectList) {
        try
        {
          if( project != null && project.isAccessible() && !projectList.contains(project)) {
            projectList.add(project);
          }
        }
        catch(Exception ex)
        {
          AlloyCore.logError( ex );
        }
      }

    public static Map<String,String> getProjectActions()
    {
        return actionMaps;
    }

    public static String getProjectAction(String actionName)
    {
        return actionMaps.get( actionName );
    }
}
