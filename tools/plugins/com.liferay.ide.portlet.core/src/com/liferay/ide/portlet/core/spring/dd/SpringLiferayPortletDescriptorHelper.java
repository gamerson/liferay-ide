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

import com.liferay.ide.portlet.core.dd.LiferayPortletDescriptorHelper;
import com.liferay.ide.portlet.core.spring.INewSpringPortletClassDataModelProperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Terry Jia
 */
public class SpringLiferayPortletDescriptorHelper extends LiferayPortletDescriptorHelper
                                                  implements INewSpringPortletClassDataModelProperties
{

    public SpringLiferayPortletDescriptorHelper()
    {
        super();
    }

    public SpringLiferayPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    protected void addDescriptorOperations()
    {
        super.addDescriptorOperations();
    }

    @Override
    public boolean canAddNewPortlet( IDataModel model )
    {
        return model.getID().contains( "NewSpringPortlet" );
    }

}
