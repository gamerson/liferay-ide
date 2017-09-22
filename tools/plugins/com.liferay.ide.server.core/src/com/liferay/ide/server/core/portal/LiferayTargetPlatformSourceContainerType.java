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

package com.liferay.ide.server.core.portal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainerTypeDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Simon Jiang
 */

public class LiferayTargetPlatformSourceContainerType extends AbstractSourceContainerTypeDelegate
{

    private static final String ELEMENT_NAME = "liferay";

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainerType#getMemento
     * (org.eclipse.debug.internal.core.sourcelookup.ISourceContainer)
     */
    @Override
    public String getMemento( ISourceContainer container ) throws CoreException
    {
        Document document = newDocument();
        Element element = document.createElement( ELEMENT_NAME );
        document.appendChild( element );
        return serializeDocument( document );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainerType# createSourceContainer(java.lang.String)
     */
    @Override
    public ISourceContainer createSourceContainer( String memento ) throws CoreException
    {
        Node node = parseDocument( memento );
        if( node.getNodeType() == Node.ELEMENT_NODE )
        {
            Element element = (Element) node;
            if( ELEMENT_NAME.equals( element.getNodeName() ) )
            {
                return new LiferayTargetPlatformSourceContainer();
            }
            abort( "Unable to restore Liferay Target Platform source lookup path - expecting bnd element.", null );
        }
        abort( "Unable to restore Liferay Target Platform source lookup path - invalid memento.", null );
        return null;
    }

}
