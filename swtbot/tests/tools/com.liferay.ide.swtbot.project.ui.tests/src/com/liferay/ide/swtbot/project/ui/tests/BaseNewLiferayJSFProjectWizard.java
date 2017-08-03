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

package com.liferay.ide.swtbot.project.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.Assume;
import org.junit.Before;

/**
 * @author Ying Xu
 */
public class BaseNewLiferayJSFProjectWizard extends BaseNewLiferayModuleProjectWizard
{

    String expectedJSFProjectcomponentSuiteItems[] =
        { LIFERAY_FACES_ALLOY, ICEFACES, JSF_STANDARD, PRIMEFACES, RICHFACES };

    NewLiferayJsfProjectWizard newJSFProject = new NewLiferayJsfProjectWizard( bot );

    Tree projectTree = ide.getPackageExporerView().getProjectTree();

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayJSFProject().click();
        sleep();
    }

    public void newLiferayJSFProject(
        String projectName, String buildFramework, String componentSuite, String defaultLocation )
    {
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newJSFProject.getValidationMsg() );
        assertEquals( StringPool.BLANK, newJSFProject.getProjectName().getText() );
        assertEquals( JSF_STANDARD, newJSFProject.getComponentSuite().getText() );
        assertTrue( newJSFProject.getUseDefaultLocation().isChecked() );

        newJSFProject.createJSFProject( projectName, buildFramework, componentSuite );
        sleep( 2000 );

        assertEquals( buildFramework, newJSFProject.getBuildFramework().getText() );

        String[] JSFProjectcomponentSuiteItems = newJSFProject.getComponentSuite().items();

        assertTrue( JSFProjectcomponentSuiteItems.length >= 1 );
        assertEquals( expectedJSFProjectcomponentSuiteItems.length, JSFProjectcomponentSuiteItems.length );

        for( int i = 0; i < JSFProjectcomponentSuiteItems.length; i++ )
        {
            assertTrue( JSFProjectcomponentSuiteItems[i].equals( expectedJSFProjectcomponentSuiteItems[i] ) );
        }

        assertTrue( newJSFProject.finishBtn().isEnabled() );
        newJSFProject.finish();
        sleep( 9000 );

    }

    public boolean checkProjectAndFileExist( String projectName, String... nodes )
    {
        String[] expandNodes = new String[nodes.length - 1];

        for( int i = 0; i < nodes.length - 1; i++ )
        {
            expandNodes[i] = nodes[i];
        }

        projectTree.setFocus();

        return projectTree.expandNode( expandNodes ).isVisible();
    }

}
