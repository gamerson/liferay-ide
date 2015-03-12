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

package com.liferay.ide.portlet.core.spring.dd;

import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.core.spring.INewSpringPortletClassDataModelProperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Terry Jia
 */
public class SpringPortletDescriptorHelper extends PortletDescriptorHelper
                                        implements INewSpringPortletClassDataModelProperties
{

    public SpringPortletDescriptorHelper()
    {
        super();
    }

    public SpringPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    public boolean canAddNewPortlet( IDataModel model )
    {
        return model.getID().contains( "NewSpringPortlet" );
    }

    @Override
    protected String getPortletClassText( IDataModel model )
    {
        return "org.springframework.web.portlet.DispatcherPortlet";
    }

}
