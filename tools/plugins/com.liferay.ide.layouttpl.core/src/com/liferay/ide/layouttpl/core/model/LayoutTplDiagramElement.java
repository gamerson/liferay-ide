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

package com.liferay.ide.layouttpl.core.model;

import org.osgi.framework.Version;



/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class LayoutTplDiagramElement extends PortletRowLayoutElement
{
    public static final String DEFAULT_ID_ATTR = "main-content"; 
    public static final String DEFAULT_ROLE_ATTR = "main";

    protected String id;
    protected String role;

    public LayoutTplDiagramElement( Version version )
    {
        super( version );

        this.id = DEFAULT_ID_ATTR; //$NON-NLS-1$
        this.role = DEFAULT_ROLE_ATTR; //$NON-NLS-1$
    }

    public String getId()
    {
        return id;
    }

    public String getRole()
    {
        return role;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

}
