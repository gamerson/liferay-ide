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

package com.liferay.ide.project.ui.modules;

import org.eclipse.swt.widgets.Shell;

/**
 * @author Simon Jiang
 */
public class LiferayPortletModuleCustomPart extends LiferayPropertyModuleCustomPart
{

    //TODO
    //will let user select property later
    @Override
    protected AddPropertyOverrideDialog getAddPropertyOverrideDialog(final Shell shell)
    {
		AddPropertyOverrideDialog dialog = new AddPropertyOverrideDialog(shell,
				"Add Portlet Property", fieldLabels, null, 
				new Boolean[] { true, true }, new String[] { null, null },
				"Property Selection", "Please select a property", 2);
        return dialog;
    }

	@Override
	protected String[] loadProperties()
	{
	    //TODO
		String[] hookProperties = null;
		return hookProperties;
	}
	
    //TODO
    //will let user select property later
    @Override
    protected EditPropertyOverrideDialog getEditPropertyOverrideDialog( final Shell shell, String[] valuesForText  )
    {
		EditPropertyOverrideDialog dialog = new EditPropertyOverrideDialog(
				shell, "Edit Portlet Property", fieldLabels, null,
				valuesForText, new Boolean[] { true, true }, 
				new String[] { null, null }, "Property Selection",
				"Please select a property", 2);
        return dialog;
    }
}
