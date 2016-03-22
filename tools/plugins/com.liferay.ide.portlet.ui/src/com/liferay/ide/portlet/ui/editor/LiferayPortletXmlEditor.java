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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletXml;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletXml70;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class LiferayPortletXmlEditor extends SapphireEditorForXml
{

    static ElementType liferayPortletXml = LiferayPortletXml.TYPE;

    static
    {
        try
        {
            if( CoreUtil.compareVersions( new Version( SDKUtil.getWorkspaceSDK().getVersion() ), ILiferayConstants.V700 ) >=0 )
            {
                liferayPortletXml = LiferayPortletXml70.TYPE;
            }
        }
        catch( CoreException e )
        {
        }
    }

    public LiferayPortletXmlEditor()
    {
        super( liferayPortletXml, null );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        addDeferredPage( 1, "Overview", "liferay-portlet-app.editor" );
    }

}
