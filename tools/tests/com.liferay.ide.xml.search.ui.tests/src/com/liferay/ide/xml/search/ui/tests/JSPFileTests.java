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

import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.verifyQuickFix;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class JSPFileTests extends XmlSearchTestsBase
{

    private IProject project;

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    private IFile getViewJspFile() throws Exception
    {
        final IFile file =  CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( "view.jsp" );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
    }

    @Test
    public void testMessageKey() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testMessageKeyValidation();
        testMessageKeyContentAssist();
        testMessageKeyQuickFix();
    }

    protected void testMessageKeyValidation() throws Exception
    {
        final IFile viewJspFile = getViewJspFile();
        assertNotNull( viewJspFile );

        final String paramElementName = "param";
        final String portletParamElementName = "portlet:param";
        final String liferayPortletParamElementName = "liferay-portlet:param";
        final String attrName = "name";
        final String attrValue = "value";

        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;
        final String exceptedMessageRegex = "Type \"sayHello\" not found.";

        final String bookName = "bookName";
        final String bookNameValue = "liferay-in-action";

        //Test for <portlet:param name="<%= ActionRequest.ACTION_NAME %>" value="sayHello" />
        setAttrValue( viewJspFile, portletParamElementName, attrName, "<%= ActionRequest.ACTION_NAME %>" );
        setAttrValue( viewJspFile, portletParamElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(true, checkMarkerByMessage( viewJspFile, markerType, exceptedMessageRegex, true ));

        //Test for <portlet:param name="javax.portlet.action" value="sayHello" />
        setAttrValue( viewJspFile, portletParamElementName, attrName, "javax.portlet.action" );
        setAttrValue( viewJspFile, portletParamElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(true, checkMarkerByMessage( viewJspFile, markerType, exceptedMessageRegex, true ));

        //Test for <portlet:param name="bookName" value="liferay-in-action" />
        setAttrValue( viewJspFile, portletParamElementName, attrName, bookName );
        setAttrValue( viewJspFile, portletParamElementName, attrValue, bookNameValue );

        buildAndValidate( viewJspFile );

        assertEquals(null, findMarkerByMessage( viewJspFile, markerType, "", true ));

        //Test for <liferay-portlet:param name="<%= ActionRequest.ACTION_NAME %>" value="sayHello" />
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrName, "<%= ActionRequest.ACTION_NAME %>" );
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(true, checkMarkerByMessage( viewJspFile, markerType, exceptedMessageRegex, true ));

        //Test for <liferay-portlet:param name="javax.portlet.action" value="sayHello" />
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrName, "javax.portlet.action" );
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(true, checkMarkerByMessage( viewJspFile, markerType, exceptedMessageRegex, true ));

        //Test for <liferay-portlet:param name="bookName" value="liferay-in-action" />
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrName, bookName );
        setAttrValue( viewJspFile, liferayPortletParamElementName, attrValue, bookNameValue );

        buildAndValidate( viewJspFile );

        assertEquals(null, findMarkerByMessage( viewJspFile, markerType, "", true ));

        //Test for <param name="<%= ActionRequest.ACTION_NAME %>" value="sayHello" />
        setAttrValue( viewJspFile, paramElementName, attrName, "<%= ActionRequest.ACTION_NAME %>" );
        setAttrValue( viewJspFile, paramElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(null, findMarkerByMessage( viewJspFile, markerType, "", true ));

        //Test for <param name="javax.portlet.action" value="sayHello" />
        setAttrValue( viewJspFile, paramElementName, attrName, "javax.portlet.action" );
        setAttrValue( viewJspFile, paramElementName, attrValue, "sayHello" );

        buildAndValidate( viewJspFile );

        assertEquals(null, findMarkerByMessage( viewJspFile, markerType, "", true ));

        //Test for <param name="bookName" value="liferay-in-action" />
        setAttrValue( viewJspFile, paramElementName, attrName, bookName );
        setAttrValue( viewJspFile, paramElementName, attrValue, bookNameValue );

        buildAndValidate( viewJspFile );

        assertEquals(null, findMarkerByMessage( viewJspFile, markerType, "", true ));
    }

    // TODO
    public void testMessageKeyContentAssist()
    {
    }

    // an example of testing quick fix
    protected void testMessageKeyQuickFix() throws Exception
    {
        final IFile viewJspFile = getViewJspFile();
        assertNotNull( viewJspFile );

        final String elementName = "liferay-ui:message";
        final String attrName = "key";

        setAttrValue( viewJspFile, elementName, attrName, "Foo" );
        buildAndValidate( viewJspFile );

        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;
        final String exceptedMessageRegex = "Property.*not found in.*";

        verifyQuickFix( viewJspFile, markerType, exceptedMessageRegex, AddResourceKeyMarkerResolution.class );
    }
}
