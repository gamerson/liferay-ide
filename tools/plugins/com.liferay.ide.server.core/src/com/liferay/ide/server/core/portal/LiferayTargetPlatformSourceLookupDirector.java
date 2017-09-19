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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;

/**
 * @author Simon Jiang
 */

public class LiferayTargetPlatformSourceLookupDirector extends AbstractSourceLookupDirector
{

    public void initializeParticipants()
    {
        addParticipants( new ISourceLookupParticipant[] { new JavaSourceLookupParticipant() } );
    }

    @Override
    public synchronized ISourceContainer[] getSourceContainers()
    {
        return super.getSourceContainers();
    }

    @Override
    public void initializeDefaults( ILaunchConfiguration configuration ) throws CoreException
    {
        dispose();
        setLaunchConfiguration( configuration );
        setSourceContainers(
            new ISourceContainer[] { new LiferayTargetPlatformSourceContainer(), new DefaultSourceContainer() } );
        initializeParticipants();
    }
}
