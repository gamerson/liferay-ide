/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt. Ltd., All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.pml.core.model;


import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IPage extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IPage.class );

}
