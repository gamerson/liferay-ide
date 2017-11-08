/**
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
 */

package com.liferay.ide.xml.search.ui.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider;
import org.eclipse.wst.xml.search.core.resource.IURIResolverProvider;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractWebResourcesQuerySpecification
	implements IResourceProvider, IResourceRequestorProvider, IURIResolverProvider {

	/**
	 * (non-Javadoc)
	 * @see IResourceRequestorProvider# getRequestor()
	 */
	public IResourceRequestor getRequestor()
	{

		return WebContentResourcesRequestor.INSTANCE;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.search.core.queryspecifications.container.
	 * IResourceProvider#getResource(java.lang.Object, IResource)
	 */
	public IResource getResource(Object selectedNode, IResource resource)
	{

		return resource.getParent().getParent();
	}

}