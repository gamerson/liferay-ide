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

package com.liferay.ide.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceNature extends AbstractLiferayNature
{

    public static final String NATURE_ID = LiferayCore.PLUGIN_ID + ".liferayWorkspaceNature";

    public LiferayWorkspaceNature()
    {
        super();
    }

    public LiferayWorkspaceNature( IProject project, IProgressMonitor monitor )
    {
        super( project, monitor );
    }

    @Override
    protected String getNatureId()
    {
        return NATURE_ID;
    }

}
