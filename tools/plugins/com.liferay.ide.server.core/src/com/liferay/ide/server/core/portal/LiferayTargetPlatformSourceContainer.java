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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.LiferayServerCore;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.containers.CompositeSourceContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class LiferayTargetPlatformSourceContainer extends CompositeSourceContainer
{

    public static final String TYPE_ID =
        "com.liferay.ide.server.core.portal.sourceContainerTypes.liferayTargetPlatform";

    @Override
    public boolean equals( Object obj )
    {
        return obj instanceof LiferayTargetPlatformSourceContainer;
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    protected ILaunchConfiguration getLaunchConfiguration()
    {
        ISourceLookupDirector director = getDirector();
        if( director != null )
        {
            return director.getLaunchConfiguration();
        }
        return null;
    }

    @Override
    public String getName()
    {
        final LiferayTargetPlatformManager debugManager = LiferayServerCore.getDefault().getDebugManager();
        Job liferayTargetPlatformManagerJob = debugManager.getLiferayTargetPlatformJob();
        if( liferayTargetPlatformManagerJob != null && liferayTargetPlatformManagerJob.getState() == Job.RUNNING )
        {
            return "Liferay Target Platform is downloading.....";
        }
        return "Liferay Target Platform";
    }

    @Override
    public ISourceContainerType getType()
    {
        return getSourceContainerType( TYPE_ID );
    }

    @Override
    protected ISourceContainer[] createSourceContainers() throws CoreException
    {
        List<IRuntimeClasspathEntry> runtimeEntries = new LinkedList<IRuntimeClasspathEntry>();
        final LiferayTargetPlatformManager debugManager = LiferayServerCore.getDefault().getDebugManager();
        ILaunchConfiguration config = getLaunchConfiguration();
        IClasspathEntry[] classpathEntries =
            debugManager.getLiferayTargetPlatformClasspathEntries( config, new NullProgressMonitor() );
        for( IClasspathEntry entry : classpathEntries )
        {
            runtimeEntries.add( new RuntimeClasspathEntry( entry ) );
        }
        return JavaRuntime.getSourceContainers(
            runtimeEntries.toArray( new IRuntimeClasspathEntry[runtimeEntries.size()] ) );
    }
}
