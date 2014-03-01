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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.model.internal.ProjectUpgradeJob;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Simon Jiang
 */

public class ProjectUpgradeTaskWithProgress implements IRunnableWithProgress
{

    private ProjectUpgradeOp op;

    public ProjectUpgradeTaskWithProgress( ProjectUpgradeOp op )
    {
        this.op = op;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
    {
        ProjectUpgradeJob job = new ProjectUpgradeJob( "Upgrading Liferay Plugin Projects", this.op);
        job.setUser( true );
        job.schedule();
    }

}
