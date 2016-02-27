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

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public interface ILiferayModuleOperation
{

    public void doExecute() throws CoreException;

    public void init(
        final String projectName, final String packageName, final String className, final String templateName,
        final String serviceName, List<String> properties ) throws CoreException;
}
