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

package com.liferay.ide.project.ui.action;


/**
 * @author Kuo Zhang
 */
public class LinkToExtDocAction extends LinkToPluginDocAction
{

    private final String docURL =
        "http://www.liferay.com/documentation/liferay-portal/6.2/development/-/ai/" +
            "advanced-customization-with-ext-plugins-liferay-portal-6-2-dev-guide-12-en";

    public LinkToExtDocAction()
    {
        super();
    }

    @Override
    protected String getDocURL()
    {
        return this.docURL;
    }

}
