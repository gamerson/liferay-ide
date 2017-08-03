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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.ComboBox;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewLiferayJsfProjectWizard extends NewProjectWizard
{

    private ComboBox buildFramework;
    private ComboBox componentSuite;

    public NewLiferayJsfProjectWizard( SWTBot bot )
    {
        super( bot, 2 );

        buildFramework = new ComboBox( bot, BUILD_FRAMEWORK );
        componentSuite = new ComboBox( bot, COMPONENT_SUITE );
    }

    public void createJSFProject( String projectNameValue, String buildFrameworkspace )
    {
        getProjectName().setText( projectNameValue );
        buildFramework.setSelection( buildFrameworkspace );
    }

    public void createJSFProject( String projectNameValue, String buildFrameworkValue, String componentSuiteValue )
    {
        getProjectName().setText( projectNameValue );
        buildFramework.setSelection( buildFrameworkValue );
        componentSuite.setSelection( componentSuiteValue );
    }

    public ComboBox getBuildFramework()
    {
        return buildFramework;
    }

    public ComboBox getComponentSuite()
    {
        return componentSuite;
    }

}
