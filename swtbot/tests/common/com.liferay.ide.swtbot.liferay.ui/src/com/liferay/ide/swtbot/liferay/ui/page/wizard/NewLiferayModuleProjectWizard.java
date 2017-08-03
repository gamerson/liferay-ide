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
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public class NewLiferayModuleProjectWizard extends NewProjectWizard
{

    private ComboBox buildTypes;
    private ComboBox projectTemplateNames;

    public NewLiferayModuleProjectWizard( SWTBot bot )
    {
        super( bot, 2 );

        projectTemplateNames = new ComboBox( bot, "Project Template Name:" );
        buildTypes = new ComboBox( bot, BUILD_TYPE );
    }

    public void createModuleProject( String text, String projectTemplate )
    {
        projectTemplateNames.setSelection( projectTemplate );
        getProjectName().setText( text );
    }

    public void createModuleProject( String text, String projectTemplate, String buildType )
    {
        projectTemplateNames.setSelection( projectTemplate );
        getProjectName().setText( text );
        buildTypes.setSelection( buildType );
    }

    public ComboBox getBuildTypes()
    {
        return buildTypes;
    }

    public ComboBox getProjectTemplateNames()
    {
        return projectTemplateNames;
    }

}
