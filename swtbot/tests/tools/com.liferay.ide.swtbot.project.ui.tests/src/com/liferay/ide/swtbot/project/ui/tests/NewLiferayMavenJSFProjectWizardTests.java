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

import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewLiferayMavenJSFProjectWizardTests extends BaseNewLiferayJSFProjectWizard
{

    @Test
    public void createMavenICEFacesProject()
    {
        String projectName = "testMavenICEFacesProject";

        newLiferayJSFProject( projectName, MAVEN, ICEFACES, StringPool.BLANK );

        String i18nPropertiesFileName = " i18n.properties";
        String facesConfigXmlFileName = "faces-config.xml";
        String pomFileName = "pom.xml";
        String pomContent = "<artifactId>icefaces-ace</artifactId>";

        checkProjectAndFileExist(
            projectName, projectName, "Java Resources", "src/main/resources", i18nPropertiesFileName );
        checkProjectAndFileExist(
            projectName, projectName, "src", "main", "webapp", "WEB-INF", facesConfigXmlFileName );
        checkProjectAndFileExist( projectName, projectName, pomFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomFileName );
    }

    @Test
    public void createMavenJSFStandardProject()
    {
        String projectName = "testMavenJSFStandardProject";

        newLiferayJSFProject( projectName, MAVEN, JSF_STANDARD, StringPool.BLANK );

        String i18nPropertiesFileName = " i18n.properties";
        String facesConfigXmlFileName = "faces-config.xml";
        String pomFileName = "pom.xml";
        String pomContent = "<groupId>com.liferay.faces</groupId>";

        checkProjectAndFileExist(
            projectName, projectName, "Java Resources", "src/main/resources", i18nPropertiesFileName );
        checkProjectAndFileExist(
            projectName, projectName, "src", "main", "webapp", "WEB-INF", facesConfigXmlFileName );
        checkProjectAndFileExist( projectName, projectName, pomFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomFileName );
    }

    @Test
    public void createMavenLiferayFacesAlloyProject()
    {
        String projectName = "testMavenLiferayFacesAlloyProject";

        newLiferayJSFProject( projectName, MAVEN, LIFERAY_FACES_ALLOY, StringPool.BLANK );

        String i18nPropertiesFileName = " i18n.properties";
        String facesConfigXmlFileName = "faces-config.xml";
        String pomFileName = "pom.xml";
        String pomContent = "<artifactId>com.liferay.faces.alloy</artifactId>";

        checkProjectAndFileExist(
            projectName, projectName, "Java Resources", "src/main/resources", i18nPropertiesFileName );
        checkProjectAndFileExist(
            projectName, projectName, "src", "main", "webapp", "WEB-INF", facesConfigXmlFileName );
        checkProjectAndFileExist( projectName, projectName, pomFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomFileName );
    }

    @Test
    public void createMavenPrimeFacesProject()
    {
        String projectName = "testMavenPrimeFacesProject";

        newLiferayJSFProject( projectName, MAVEN, PRIMEFACES, StringPool.BLANK );

        String i18nPropertiesFileName = " i18n.properties";
        String facesConfigXmlFileName = "faces-config.xml";
        String pomFileName = "pom.xml";
        String pomContent = "<artifactId>primefaces</artifactId>";

        checkProjectAndFileExist(
            projectName, projectName, "Java Resources", "src/main/resources", i18nPropertiesFileName );
        checkProjectAndFileExist(
            projectName, projectName, "src", "main", "webapp", "WEB-INF", facesConfigXmlFileName );
        checkProjectAndFileExist( projectName, projectName, pomFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomFileName );
    }

    @Test
    public void createMavenRichFacesProject()
    {
        String projectName = "testMavenRichFacesProject";

        newLiferayJSFProject( projectName, MAVEN, RICHFACES, StringPool.BLANK );

        String i18nPropertiesFileName = " i18n.properties";
        String facesConfigXmlFileName = "faces-config.xml";
        String pomFileName = "pom.xml";
        String pomContent = "<artifactId>richfaces</artifactId>";

        checkProjectAndFileExist(
            projectName, projectName, "Java Resources", "src/main/resources", i18nPropertiesFileName );
        checkProjectAndFileExist(
            projectName, projectName, "src", "main", "webapp", "WEB-INF", facesConfigXmlFileName );
        checkProjectAndFileExist( projectName, projectName, pomFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomFileName );
    }

    @Before
    public void shouldRunTests()
    {

        Assume.assumeTrue( runTest() || runAllTests() );

    }

}
