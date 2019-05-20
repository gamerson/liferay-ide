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

package com.liferay.ide.upgrade.plan.core;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Terry Jia
 */
public interface UpgradePlanProperty extends Element {

	public ElementType TYPE = new ElementType(UpgradePlanProperty.class);

	public Value<String> getKey();

	public Value<Path> getValue();

	public void setKey(String key);

	public void setValue(String value);

	@Enablement(expr = "false")
	@Label(standard = "Key")
	public ValueProperty PROP_KEY = new ValueProperty(TYPE, "Key");

	@AbsolutePath
	@Label(standard = "Value")
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");

}