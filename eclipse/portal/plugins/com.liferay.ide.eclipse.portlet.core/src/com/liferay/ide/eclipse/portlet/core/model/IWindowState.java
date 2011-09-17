/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Services Pvt Ltd., All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IWindowState extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IWindowState.class );

	/*
	 * state
	 */

	@Label( standard = "state", full = "Window State" )
	@NoDuplicates
	@Whitespace( trim = true )
	@XmlBinding( path = "" )
	// @CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = "window-state" )
	ValueProperty PROP_WINDOW_STATE = new ValueProperty( TYPE, "WindowState" );

	Value<String> getWindowState();

	void setWindowState( String state );
}
