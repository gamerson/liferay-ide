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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public interface ILayoutTplDiagramFactory
{

    LayoutTplDiagramElement newLayoutTplDiagram( Version version );

    LayoutTplDiagramElement newLayoutTplDiagramFromFile( IFile file );

    LayoutTplDiagramElement newLayoutTplDiagramFromModel( IDOMModel model );

    PortletColumnElement newPortletColumn( Version version );

    PortletColumnElement newPortletColumnFromElement( IDOMElement portletColumnElement );

    PortletLayoutElement newPortletLayout( Version version );

    PortletLayoutElement newPortletLayoutFromElement( IDOMElement portletLayoutElement );

}
