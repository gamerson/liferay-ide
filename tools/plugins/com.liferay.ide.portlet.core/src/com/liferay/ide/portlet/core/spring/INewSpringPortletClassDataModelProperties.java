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

package com.liferay.ide.portlet.core.spring;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;

/**
 * @author Terry Jia
 */
public interface INewSpringPortletClassDataModelProperties extends INewPortletClassDataModelProperties
{

    String SPRING_VIEW_MODE_TEMPLATE = "com.liferay.ide.templates.portlet.spring.view"; //$NON-NLS-1$

    String DEFAULT_VIEW_CONTROLLER_CLASS = "INewSpringPortletClassDataModelProperties.DEFAULT_VIEW_CONTROLLER_CLASS"; //$NON-NLS-1$

    String DEFAULT_PORTLET_CONTROLLER_NAME = "PortletViewController"; //$NON-NLS-1$

}