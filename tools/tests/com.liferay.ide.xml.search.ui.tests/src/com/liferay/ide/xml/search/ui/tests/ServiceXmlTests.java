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

package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class ServiceXmlTests extends XmlSearchTestsBase
{

    private final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    public static final String SERVICE_XML_SYNTAX_INVALID = "Syntax of \"{0}\" is invalid.";
    private IFile descriptorFile;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.SERVICE_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    @Test
    public void testNamespaceValidation() throws Exception
    {
        // IDE-1836
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        String elementName = "namespace";
        String elementValue = "namespace1";
        setElementContent( descriptorFile, elementName, elementValue );

        String markerMessage = MessageFormat.format( SERVICE_XML_SYNTAX_INVALID, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

    }

}
